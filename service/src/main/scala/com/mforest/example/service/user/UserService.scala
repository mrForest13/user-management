package com.mforest.example.service.user

import cats.data.EitherT
import cats.data.EitherT.right
import cats.effect.Async
import com.mforest.example.core.error.Error
import com.mforest.example.core.error.Error.ConflictError
import com.mforest.example.db.dao.UserDao
import com.mforest.example.db.model.UserRow
import com.mforest.example.service.Service
import com.mforest.example.service.model.User
import doobie.implicits.AsyncConnectionIO
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

trait UserService[F[_]] extends Service {

  def createUser(user: User): EitherT[F, Error, String]
}

class UserServiceImpl[F[_]: Async](dao: UserDao, transactor: Transactor[F]) extends UserService[F] {

  private val created  = (email: String) => s"The user with email $email has been created"
  private val conflict = (email: String) => s"User with email $email already exists!"

  override def createUser(user: User): EitherT[F, Error, String] = {
    for {
      logger <- right(Slf4jLogger.create[F])
      id     <- right(FUUID.randomFUUID[F])
      salt   <- right(FUUID.randomFUUID[F])
      hash   <- right(hashPassword(user.password, salt))
      row    = prepareRow(id, salt, hash, user)
      _      <- insertUser(row)
      info   = created(user.email)
      _      <- right(logger.info(info))
    } yield info
  }

  private def insertUser(row: UserRow): EitherT[F, Error, Int] = {
    dao
      .find(row.email)
      .map(user => conflict(user.email))
      .map[Error](ConflictError)
      .toLeft(row)
      .semiflatMap(dao.insert)
      .transact(transactor)
  }

  private def hashPassword(password: String, salt: FUUID): F[PasswordHash[SCrypt]] = {
    val withSalt = password.concat(salt.toString())
    SCrypt.hashpw[F](withSalt.toCharArray)
  }

  private def prepareRow(id: FUUID, salt: FUUID, hash: String, user: User): UserRow = {
    UserRow(
      id = id,
      email = user.email,
      hash = hash,
      salt = salt,
      firstName = user.firstName,
      lastName = user.lastName,
      city = user.city,
      country = user.country,
      phone = user.phone
    )
  }
}

object UserService {

  def apply[F[_]: Async](dao: UserDao, transactor: Transactor[F]): UserService[F] = {
    new UserServiceImpl[F](dao, transactor)
  }
}
