package com.mforest.example.core.config.app

final case class AppConfig(name: String, description: String, banner: String) {

  val stripBanner: List[String] = {
    banner.stripMargin
      .split(System.lineSeparator)
      .toList
  }
}
