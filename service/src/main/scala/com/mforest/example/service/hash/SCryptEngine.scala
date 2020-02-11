package com.mforest.example.service.hash

import cats.effect.Sync
import io.chrisdavenport.fuuid.FUUID
import tsec.common.VerificationStatus
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

class SCryptEngine[F[_]: Sync] extends HashEngine[F, SCrypt] {

  override def hashPassword(password: String, salt: FUUID): F[PasswordHash[SCrypt]] = {
    hashpw[F](concat(password, salt))
  }

  override def checkPassword(password: String, salt: FUUID, hash: String): F[VerificationStatus] = {
    checkpw[F](concat(password, salt), PasswordHash[SCrypt](hash))
  }
}

object SCryptEngine {

  def apply[F[_]: Sync](): SCryptEngine[F] = new SCryptEngine()
}
