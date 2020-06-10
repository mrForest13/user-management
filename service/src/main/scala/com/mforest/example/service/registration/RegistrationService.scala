package com.mforest.example.service.registration

import cats.data.EitherT
import cats.effect.Async
import com.mforest.example.core.error.Error
import com.mforest.example.db.dao.UserDao
import com.mforest.example.db.row.UserRow
import com.mforest.example.service.Service
import com.mforest.example.service.hash.HashEngine
import com.mforest.example.service.model.User
import doobie.implicits.AsyncConnectionIO
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import tsec.passwordhashers.PasswordHash

trait RegistrationService[F[_]] extends Service {

  val name: String = "Registration-Service"

  def register(user: User): EitherT[F, Error, String]
}

class RegistrationServiceImpl[F[_]: Async, A](userDao: UserDao, hashEngine: HashEngine[F, A], transactor: Transactor[F])
    extends RegistrationService[F] {

  private val created  = (email: String) => s"The user with email $email has been created."
  private val conflict = (email: String) => s"The user with email $email already exists!"

  override def register(user: User): EitherT[F, Error, String] = {
    for {
      id   <- EitherT.right(FUUID.randomFUUID[F])
      salt <- EitherT.right(FUUID.randomFUUID[F])
      hash <- EitherT.right(hashPassword(user.password, salt))
      _    <- insertUser(user.toRow(id, salt, hash))
    } yield created(user.email)
  }

  private def insertUser(row: UserRow): EitherT[F, Error, Int] = {
    userDao
      .find(row.email)
      .toLeft(row)
      .leftMap(_.email)
      .leftMap(conflict)
      .leftMap[Error](Error.ConflictError)
      .semiflatMap(userDao.insert)
      .transact(transactor)
  }

  private def hashPassword(password: String, salt: FUUID): F[PasswordHash[A]] = {
    hashEngine.hashPassword(password, salt)
  }
}

object RegistrationService {

  def apply[F[_]: Async, A](
      userDao: UserDao,
      hashEngine: HashEngine[F, A],
      transactor: Transactor[F]
  ): RegistrationService[F] = {
    new RegistrationServiceImpl[F, A](userDao, hashEngine, transactor)
  }
}
