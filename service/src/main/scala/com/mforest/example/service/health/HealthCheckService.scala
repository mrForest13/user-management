package com.mforest.example.service.health

import cats.data.{EitherT, NonEmptyList}
import cats.effect.{Concurrent, Timer}
import com.mforest.example.core.error.Error
import com.mforest.example.service.Service
import com.mforest.example.service.converter.DtoConverter.NonEmptyListConverter
import com.mforest.example.service.dto.CheckDto
import doobie.util.transactor.Transactor
import sup.data.{HealthReporter, Tagged}
import sup.modules.doobie.connectionCheck
import sup.{HealthCheck, mods}

import scala.concurrent.duration.DurationInt

trait HealthCheckService[F[_]] extends Service {

  val name: String = "Health-Check-Service"

  def check: EitherT[F, Error, NonEmptyList[CheckDto]]
}

class HealthCheckServiceImpl[F[_]: Concurrent: Timer](transactor: Transactor[F]) extends HealthCheckService[F] {

  type TaggedCheck[A] = Tagged[String, A]

  def check: EitherT[F, Error, NonEmptyList[CheckDto]] = EitherT.right {
    HealthReporter
      .fromChecks(database)
      .check
      .map(_.value)
      .map(_.checks)
      .map(_.to[CheckDto])
  }

  private def database: HealthCheck[F, TaggedCheck] = {
    connectionCheck(transactor)(none)
      .through(mods.recoverToSick)
      .through(mods.timeoutToSick(5.seconds))
      .through(mods.tagWith("database"))
  }
}

object HealthCheckService {

  def apply[F[_]: Concurrent: Timer](transactor: Transactor[F]): HealthCheckService[F] = {
    new HealthCheckServiceImpl(transactor)
  }
}
