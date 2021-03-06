package com.mforest.example.core

import cats.effect.{Resource, Sync}
import com.mforest.example.core.config.Config
import pureconfig.generic.auto.exportReader
import pureconfig.module.catseffect.loadConfigF

class ConfigLoader[F[_]: Sync] {

  def config(): Resource[F, Config] = Resource.liftF(loadConfigF[F, Config])
}

object ConfigLoader {

  def apply[F[_]: Sync]: ConfigLoader[F] = new ConfigLoader()
}
