package com.mforest.example.core

import cats.effect.{Resource, Sync}
import com.mforest.example.core.config.Config
import pureconfig.generic.auto.exportReader
import pureconfig.module.catseffect.loadConfigF

class ConfigLoader[F[_]: Sync] {

  def load: F[Config] = loadConfigF[F, Config]

  def asResource: Resource[F, Config] = Resource.liftF(load)
}

object ConfigLoader {

  def apply[F[_]: Sync]: ConfigLoader[F] = new ConfigLoader()
}
