package com.mforest.example.application.initialization

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import com.mforest.example.application.info.BuildInfo
import com.mforest.example.core.config.Config
import com.mforest.example.http.api.{
  AuthenticationApi,
  AuthorizationApi,
  HealthCheckApi,
  PermissionApi,
  RegistrationApi,
  SwaggerApi,
  UserApi
}
import com.mforest.example.http.yaml.SwaggerDocs
import com.mforest.example.http.{Api, Doc}

class ApiInitializer[F[_]: ContextShift: ConcurrentEffect: Timer](config: Config, service: ServiceInitializer[F]) {

  private val documentedApis: Seq[Api[F] with Doc] = Seq(
    UserApi[F](service.user, service.auth),
    PermissionApi[F](service.permission, service.auth),
    HealthCheckApi(service.healthCheck),
    RegistrationApi[F](service.registration),
    AuthorizationApi(service.auth),
    AuthenticationApi(service.login, service.auth)
  )

  private val yamlDocs: String = {
    SwaggerDocs(config.app, BuildInfo.version, documentedApis).yaml
  }

  def apis: Seq[Api[F]] = {
    documentedApis.:+(SwaggerApi[F](yamlDocs, config.swagger))
  }
}

object ApiInitializer {

  def apply[F[_]: ContextShift: ConcurrentEffect: Timer](
      config: Config,
      service: ServiceInitializer[F]
  ): ApiInitializer[F] = {
    new ApiInitializer(config, service)
  }
}
