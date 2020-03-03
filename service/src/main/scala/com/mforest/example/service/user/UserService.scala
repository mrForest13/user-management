package com.mforest.example.service.user

import cats.Id
import cats.data.OptionT.liftF
import cats.data.{Chain, EitherT}
import cats.effect.Async
import com.mforest.example.core.error.Error
import com.mforest.example.core.error.Error.NotFoundError
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.dao.{PermissionDao, UserDao}
import com.mforest.example.db.row.PermissionRow
import com.mforest.example.service.Service
import com.mforest.example.service.converter.DtoConverter.ChainConverter
import com.mforest.example.service.dto.{PermissionDto, UserDto}
import doobie.free.connection.ConnectionIO
import doobie.implicits.AsyncConnectionIO
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import redis.clients.jedis.JedisPool
import scalacache.redis.RedisCache
import scalacache.serialization.circe.codec
import scalacache.{Cache, CatsEffect, Flags, Mode}

trait UserService[F[_]] extends Service {

  val name: String = "User-Service"

  def revokePermission(userId: Id[FUUID], permissionId: Id[FUUID]): EitherT[F, Error, String]
  def addPermission(userId: Id[FUUID], permissionId: Id[FUUID]): EitherT[F, Error, String]
  def getUsers(pagination: Pagination): EitherT[F, Error, Chain[UserDto]]
}

class UserServiceImpl[F[_]: Async](
    userDao: UserDao,
    permissionDao: PermissionDao,
    cache: Cache[Chain[PermissionDto]],
    transactor: Transactor[F]
) extends UserService[F] {

  private implicit val flags: Flags  = Flags.defaultFlags
  private implicit val mode: Mode[F] = CatsEffect.modes.async[F]

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
    val action = for {
      _    <- permissionDao.find(permissionId)
      _    <- userDao.find(userId)
      _    <- liftF(userDao.add(userId, permissionId))
      rows <- liftF(permissionDao.findByUser(userId))
      _    <- liftF(updateCache(userId)(rows).pure[ConnectionIO])
    } yield created

    action
      .transact(transactor)
      .toRight(Error.notFound(notFound))
  }

  override def revokePermission(userId: Id[FUUID], permissionId: Id[FUUID]): EitherT[F, Error, String] = EitherT {
    val action = for {
      count <- userDao.delete(userId, permissionId)
      rows  <- permissionDao.findByUser(userId)
      _     <- updateCache(userId)(rows).pure[ConnectionIO]
    } yield count

    action.transact(transactor).map {
      case count: Int if count > 0  => deleted.asRight
      case count: Int if count <= 0 => NotFoundError(notFound).asLeft
    }
  }

  private def updateCache(userId: Id[FUUID])(permissions: Chain[PermissionRow]): F[Any] = {
    cache.put(userId)(permissions.to[PermissionDto])
  }
}

object UserService {

  def apply[F[_]: Async](
      userDai: UserDao,
      permissionDao: PermissionDao,
      client: JedisPool,
      transactor: Transactor[F]
  ): UserService[F] = {
    new UserServiceImpl[F](userDai, permissionDao, RedisCache(client), transactor)
  }
}
