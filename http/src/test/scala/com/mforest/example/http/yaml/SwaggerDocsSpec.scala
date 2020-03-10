package com.mforest.example.http.yaml

import com.mforest.example.core.config.app.AppConfig
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class SwaggerDocsSpec extends AnyWordSpec with Matchers {

  "SwaggerDocs" when {

    "yaml" must {

      "respond with valid base yaml string" in {
        val version = "1.0.0"
        val config  = AppConfig(name = "example", description = "example", banner = "example")

        val docs = SwaggerDocs(config = config, version = version, docs = Seq.empty)

        docs.yaml shouldBe
          """openapi: 3.0.1
            |info:
            |  title: example
            |  version: 1.0.0
            |  description: example
            |  contact:
            |    name: Mateusz Ligęza
            |    email: mateusz.kamil.ligeza@gmail.com
            |    url: https://github.com/mrForest13
            |  license:
            |    name: Apache 2.0
            |    url: http://www.apache.org/licenses/LICENSE-2.0.html
            |""".stripMargin
      }
    }
  }
}
