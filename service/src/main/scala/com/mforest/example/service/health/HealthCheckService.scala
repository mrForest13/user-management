package com.mforest.example.service.health

import cats.effect.Async
import com.mforest.example.service.Service
import com.mforest.example.service.model.Check
import doobie.util.transactor.Transactor
import sup.mods
import sup.modules.doobie.connectionCheck

import scala.concurrent.duration.DurationInt

trait HealthCheckService[F[_]] extends Service {

  val name: String = "Health-Check-Service"

}

class HealthCheckServiceImpl[F[_]: Async](transactor: Transactor[F]) extends HealthCheckService[F] {

  def check: F[Check] = {
    connectionCheck(transactor)(timeout = Some(5.seconds))
      .through(mods.tagWith("doobie"))
      .check
      .map(_.value)
      .map(result => Check(result.tag, result.health.isHealthy))
  }
}
