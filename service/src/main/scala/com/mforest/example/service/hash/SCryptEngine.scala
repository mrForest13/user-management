package com.mforest.example.service.hash

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import tsec.common.VerificationStatus
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

class SCryptEngine[F[_]: Sync] extends HashEngine[F, SCrypt] {

  private val generated = (hash: String) => s"Generated hash: $hash"
  private val check     = (hash: String) => s"Checking hash: $hash"

  override def hashPassword(password: String, salt: FUUID): F[PasswordHash[SCrypt]] = {
    for {
      logger <- Slf4jLogger.create[F]
      hash   <- hashpw[F](concat(password, salt))
      _      <- logger.info(generated(hash))
    } yield hash
  }

  override def checkPassword(password: String, salt: FUUID, hash: String): F[VerificationStatus] = {
    for {
      logger <- Slf4jLogger.create[F]
      crypt  = PasswordHash[SCrypt](hash)
      status <- checkpw[F](concat(password, salt), crypt)
      _      <- logger.info(check(hash))
    } yield status
  }
}

object SCryptEngine {

  def apply[F[_]: Sync](): HashEngine[F, SCrypt] = new SCryptEngine()
}
