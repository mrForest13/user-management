package com.mforest.example.http.doc

import java.time.Instant

import cats.data.{NonEmptyChain, NonEmptyList}
import cats.effect.IO
import cats.implicits.catsSyntaxOptionId
import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BearerToken
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.model.SessionInfo
import io.chrisdavenport.fuuid.FUUID
import sttp.model.StatusCode
import sttp.tapir.Endpoint
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

private[http] trait AuthorizationApiDoc extends Doc {

  override def endpoints: NonEmptyList[Endpoint[_, _, _, _]] = NonEmptyList.of(validatePermissionEndpoint)

  protected val validatePermissionEndpoint
      : Endpoint[(String, Token), Fail[Error], (BearerToken, Ok[SessionInfo]), Nothing] = {
    endpoint.get
      .tag("Authorization Api")
      .summary("Valid user permission")
      .in("permissions" / path[String]("permission") / "validate")
      .in(auth.bearer)
      .out(header[BearerToken]("Authorization"))
      .out(
        oneOf(
          statusMappingClassMatcher(
            StatusCode.Ok,
            jsonBody[Ok[SessionInfo]]
              .example(
                StatusResponse.Ok(AuthorizationApiDoc.sessionInfo)
              ),
            classOf[Ok[SessionInfo]]
          )
        )
      )
      .errorOut(
        oneOf[Fail[Error]](
          statusMappingFromMatchType(
            StatusCode.BadRequest,
            jsonBody[Fail[Error.ValidationError]]
              .example(
                StatusResponse.Fail(Error.ValidationError("Invalid value for: header Authorization!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.NotFound,
            jsonBody[Fail[Error.NotFoundError]]
              .example(
                StatusResponse.Fail(Error.NotFoundError("The permission with name EXAMPLE_PERMISSION not exists!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.Forbidden,
            jsonBody[Fail[Error.ForbiddenError]]
              .example(
                StatusResponse.Fail(
                  Error.ForbiddenError("The server is refusing to respond to it! You don't have permission!")
                )
              )
          ),
          statusMappingFromMatchType(
            StatusCode.ServiceUnavailable,
            jsonBody[Fail[Error.UnavailableError]]
              .example(
                StatusResponse.Fail(Error.UnavailableError("The server is currently unavailable!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.InternalServerError,
            jsonBody[Fail[Error.InternalError]]
              .example(
                StatusResponse.Fail(Error.InternalError("There was an internal server error!"))
              )
          )
        )
      )
  }
}

object AuthorizationApiDoc {

  private val permissions: NonEmptyChain[PermissionDto] = NonEmptyChain(
    PermissionDto(
      id = randomUnsafeId,
      name = "FIRST_EXAMPLE_PERMISSION"
    ),
    PermissionDto(
      id = randomUnsafeId,
      name = "SECOND_EXAMPLE_PERMISSION"
    ),
    PermissionDto(
      id = randomUnsafeId,
      name = "THIRD_EXAMPLE_PERMISSION"
    )
  )

  private val sessionInfo: SessionInfo = SessionInfo(
    identity = permissions,
    authenticator = TSecBearerToken(
      id = SecureRandomId.Strong.generate,
      identity = randomUnsafeId,
      expiry = Instant.now,
      lastTouched = Instant.now.some
    )
  )

  private def randomUnsafeId: FUUID = {
    FUUID.randomFUUID[IO].unsafeRunSync()
  }
}
