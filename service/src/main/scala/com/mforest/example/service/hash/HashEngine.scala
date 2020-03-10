package com.mforest.example.service.hash

import cats.Show
import cats.implicits.toShow
import io.chrisdavenport.fuuid.FUUID
import tsec.common.VerificationStatus
import tsec.passwordhashers.{PasswordHash, PasswordHashAPI}

trait HashEngine[F[_], A] extends PasswordHashAPI[A] {

  def hashPassword(password: String, salt: FUUID): F[PasswordHash[A]]
  def checkPassword(password: String, salt: FUUID, hash: String): F[VerificationStatus]

  final def concat[T: Show](password: String, salt: T): Array[Char] = {
    password.concat(salt.show).toCharArray
  }
}
