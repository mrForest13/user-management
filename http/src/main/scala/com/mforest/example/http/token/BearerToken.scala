package com.mforest.example.http.token

import cats.Show
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

final case class BearerToken(value: SecureRandomId)

object BearerToken {

  def apply[I](value: TSecBearerToken[I]): BearerToken = new BearerToken(value.id)

  def fromString(value: String): BearerToken = new BearerToken(SecureRandomId.apply(value))

  implicit val show: Show[BearerToken] = (token: BearerToken) => s"Bearer ${token.value}"
}
