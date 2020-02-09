package com.mforest.example.http.swagger

import cats.syntax.option._
import com.mforest.example.core.config.app.AppConfig
import com.mforest.example.http.Doc
import com.mforest.example.http.doc.UserApiDoc
import sttp.tapir.Endpoint
import sttp.tapir.docs.openapi.TapirOpenAPIDocs
import sttp.tapir.openapi.circe.yaml.TapirOpenAPICirceYaml
import sttp.tapir.openapi.{Contact, Info, License, OpenAPI}

class OpenApi(config: AppConfig, version: String)
    extends Doc
    with UserApiDoc
    with TapirOpenAPIDocs
    with TapirOpenAPICirceYaml {

  override val docs: List[Endpoint[_, _, _, _]] = List.empty ++ super.docs

  private val contact = Contact(
    name = "Mateusz Ligęza".some,
    email = "mateusz.kamil.ligeza@gmail.com".some,
    url = "https://github.com/mrForest13".some
  )

  private val license = License(
    name = "Apache 2.0",
    url = "http://www.apache.org/licenses/LICENSE-2.0.html".some
  )

  private val info = Info(
    title = config.name,
    version = version,
    description = config.description.some,
    contact = contact.some,
    license = license.some
  )

  private val openApi: OpenAPI = docs.toOpenAPI(info)

  val yaml: String = openApi.toYaml
}

object OpenApi {

  def apply(config: AppConfig, version: String): OpenApi = new OpenApi(config, version)
}
