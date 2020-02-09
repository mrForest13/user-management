package com.mforest.example.service.hash

import io.chrisdavenport.fuuid.FUUID
import tsec.common.VerificationStatus
import tsec.passwordhashers.{PasswordHash, PasswordHashAPI}

trait HashEngine[F[_], A] extends PasswordHashAPI[A] {

  def hashPassword(password: String, salt: FUUID): F[PasswordHash[A]]
  def checkPassword(password: String, hash: String, salt: FUUID): F[VerificationStatus]

  final def concat[T](password: String, salt: T): Array[Char] = {
    password.concat(salt.toString).toCharArray
  }
}
