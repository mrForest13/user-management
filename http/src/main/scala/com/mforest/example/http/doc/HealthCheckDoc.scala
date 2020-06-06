package com.mforest.example.http.doc

import cats.data.NonEmptyList
import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.service.dto.HealthCheckDto
import sttp.model.StatusCode
import sttp.tapir.Endpoint

private[http] trait HealthCheckDoc extends Doc {

  override def endpoints: NonEmptyList[Endpoint[_, _, _, _]] = NonEmptyList.of(healthCheckEndpoint)

  protected val healthCheckEndpoint: Endpoint[Unit, Fail[Error], Ok[NonEmptyList[HealthCheckDto]], Nothing] = {
    endpoint.get
      .tag("Health Check Api")
      .summary("Checks if application resources are up")
      .in("api" / "health-check")
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Ok,
            jsonBody[Ok[NonEmptyList[HealthCheckDto]]]
              .example(
                StatusResponse.Ok(HealthCheckDoc.checks)
              )
          )
        )
      )
      .errorOut(
        oneOf[Fail[Error]](
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

object HealthCheckDoc {

  private val checks = NonEmptyList.of(
    HealthCheckDto(service = "database", healthy = true),
    HealthCheckDto(service = "cache", healthy = true)
  )
}
