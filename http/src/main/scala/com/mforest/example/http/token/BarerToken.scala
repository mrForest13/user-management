package com.mforest.example.http.token

import tsec.common.SecureRandomId

import scala.language.implicitConversions

class BarerToken(val value: SecureRandomId) {

  override def toString: String = s"Bearer $value"
}

object BarerToken {

  def apply(value: String): BarerToken = new BarerToken(value.asInstanceOf[SecureRandomId])

  implicit def fromString(value: String): BarerToken = new BarerToken(value.asInstanceOf[SecureRandomId])
}
