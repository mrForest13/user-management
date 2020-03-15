package com.mforest.example.core.config.swagger

final case class SwaggerConfig(contextPath: String, yamlName: String, redirectQuery: Map[String, Seq[String]])
