package com.mforest.example.http.token

import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

final class BarerToken(value: SecureRandomId) {

  override def toString: String = s"Bearer $value"
}

object BarerToken {

  def apply[I](value: TSecBearerToken[I]): BarerToken = new BarerToken(value.id)

  def apply(value: String): BarerToken = new BarerToken(value.asInstanceOf[SecureRandomId])
}
