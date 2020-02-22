package com.mforest.example.service.user

import cats.Id
import cats.data.{Chain, EitherT}
import cats.effect.Async
import cats.implicits._
import doobie.implicits.AsyncConnectionIO
import com.mforest.example.core.error.Error
import com.mforest.example.core.error.Error.NotFoundError
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.dao.{PermissionDao, UserDao}
import com.mforest.example.service.Service
import com.mforest.example.service.converter.DtoConverter.DtoChainConverter
import com.mforest.example.service.dto.UserDto
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID

trait UserService[F[_]] extends Service {

  def revokePermission(userId: Id[FUUID], permissionId: Id[FUUID]): EitherT[F, Error, String]
  def addPermission(userId: Id[FUUID], permissionId: Id[FUUID]): EitherT[F, Error, String]
  def getUsers(pagination: Pagination): EitherT[F, Error, Chain[UserDto]]
}

class UserServiceImpl[F[_]: Async](userDao: UserDao, permissionDao: PermissionDao, transactor: Transactor[F])
    extends UserService[F] {

  private val created  = "The permission has been added!"
  private val deleted  = "The permission has been revoked!"
  private val notFound = "The User or permission does not exist!"

  override def getUsers(pagination: Pagination): EitherT[F, Error, Chain[UserDto]] = EitherT {
    userDao
      .find(pagination)
      .transact(transactor)
      .map(_.to[UserDto])
      .map(_.asRight[Error])
  }

  override def addPermission(userId: Id[FUUID], permissionId: Id[FUUID]): EitherT[F, Error, String] = {
    permissionDao
      .find(permissionId)
      .flatMap(_ => userDao.find(userId))
      .toRight(Error.validation(notFound))
      .semiflatMap(_ => userDao.add(userId, permissionId))
      .map(_ => created)
      .transact(transactor)
  }

  override def revokePermission(userId: Id[FUUID], permissionId: Id[FUUID]): EitherT[F, Error, String] = EitherT {
    userDao.delete(userId, permissionId).transact(transactor).map {
      case count: Int if count > 0  => deleted.asRight
      case count: Int if count <= 0 => NotFoundError(notFound).asLeft
    }
  }
}

object UserService {

  def apply[F[_]: Async](userDai: UserDao, permissionDao: PermissionDao, transactor: Transactor[F]): UserService[F] = {
    new UserServiceImpl[F](userDai, permissionDao, transactor)
  }
}
