package com.mforest.example.http.token

import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

private[http] final class BearerToken(value: SecureRandomId) {

  override def toString: String = s"Bearer $value"
}

object BearerToken {

  def apply[I](value: TSecBearerToken[I]): BearerToken = new BearerToken(value.id)

  def apply(value: String): BearerToken = new BearerToken(SecureRandomId.apply(value))
}
