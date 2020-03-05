package com.mforest.example.http.token

import cats.Show
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

final class BearerToken(val value: SecureRandomId)

object BearerToken {

  def apply[I](value: TSecBearerToken[I]): BearerToken = new BearerToken(value.id)

  def apply(value: String): BearerToken = new BearerToken(SecureRandomId.apply(value))

  implicit val show: Show[BearerToken] = (token: BearerToken) => s"Bearer ${token.value}"
}
