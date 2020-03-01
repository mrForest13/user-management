package com.mforest.example.service.login

import cats.data.EitherT
import cats.data.EitherT.{fromEither, right}
import cats.effect.Async
import com.mforest.example.core.error.Error
import com.mforest.example.core.error.Error.UnauthorizedError
import com.mforest.example.db.dao.UserDao
import com.mforest.example.db.row.UserRow
import com.mforest.example.service.Service
import com.mforest.example.service.hash.HashEngine
import com.mforest.example.service.model.Credentials
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import tsec.common.{VerificationFailed, VerificationStatus, Verified}

trait LoginService[F[_]] extends Service {

  val name: String = "Login-Service"

  def login(credentials: Credentials): EitherT[F, Error, FUUID]
}

class LoginServiceImpl[F[_]: Async, A](userDao: UserDao, hashEngine: HashEngine[F, A], transactor: Transactor[F])
    extends LoginService[F] {

  private val unauthorized: String = s"Wrong email or password"

  override def login(credentials: Credentials): EitherT[F, Error, FUUID] = {
    for {
      user   <- findUser(credentials.email)
      status <- right(checkPassword(credentials.password, user.salt, user.hash))
      _      <- fromEither(validateStatus(status))
    } yield user.id
  }

  private def findUser(email: String): EitherT[F, Error, UserRow] = {
    userDao
      .find(email)
      .transact(transactor)
      .toRight(UnauthorizedError(unauthorized))
  }

  private def checkPassword(password: String, salt: FUUID, hash: String): F[VerificationStatus] = {
    hashEngine.checkPassword(password, salt, hash)
  }

  private def validateStatus(hash: VerificationStatus): Either[Error, Unit] = {
    hash match {
      case Verified           => ().asRight
      case VerificationFailed => UnauthorizedError(unauthorized).asLeft
    }
  }
}

object LoginService {

  def apply[F[_]: Async, A](
      userDao: UserDao,
      hashEngine: HashEngine[F, A],
      transactor: Transactor[F]
  ): LoginService[F] = {
    new LoginServiceImpl[F, A](userDao, hashEngine, transactor)
  }
}
