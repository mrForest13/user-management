package com.mforest.example.http.token

import cats.Show

final case class BasicToken(value: String)

object BasicToken {

  implicit val show: Show[BasicToken] = (token: BasicToken) => s"Basic ${token.value}"
}
