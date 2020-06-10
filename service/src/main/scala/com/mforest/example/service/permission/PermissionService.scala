package com.mforest.example.service.permission

import cats.Id
import cats.data.EitherT.right
import cats.data.{Chain, EitherT}
import cats.effect.Async
import com.mforest.example.core.error.Error
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.dao.PermissionDao
import com.mforest.example.db.row.PermissionRow
import com.mforest.example.service.Service
import com.mforest.example.service.converter.DtoConverter.ChainConverter
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.model.Permission
import doobie.implicits.AsyncConnectionIO
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID

trait PermissionService[F[_]] extends Service {

  val name: String = "Permission-Service"

  def getPermissions(pagination: Pagination): EitherT[F, Error, Chain[PermissionDto]]
  def getPermissions(userId: Id[FUUID]): EitherT[F, Error, Chain[PermissionDto]]
  def addPermission(permission: Permission): EitherT[F, Error, String]
}

class PermissionServiceImpl[F[_]: Async](dao: PermissionDao, transactor: Transactor[F]) extends PermissionService[F] {

  private val created  = (name: String) => s"The permission with name: $name has been created!"
  private val conflict = (name: String) => s"The permission with name: $name already exists!"

  override def addPermission(permission: Permission): EitherT[F, Error, String] = {
    right(FUUID.randomFUUID[F])
      .map(prepareRow(permission))
      .flatMap(insertPermission)
      .as(created(permission.name))
  }

  override def getPermissions(userId: Id[FUUID]): EitherT[F, Error, Chain[PermissionDto]] = EitherT.right {
    dao
      .findByUser(userId)
      .transact(transactor)
      .map(_.to[PermissionDto])
  }

  override def getPermissions(pagination: Pagination): EitherT[F, Error, Chain[PermissionDto]] = EitherT.right {
    dao
      .find(pagination)
      .transact(transactor)
      .map(_.to[PermissionDto])
  }

  private def insertPermission(row: PermissionRow): EitherT[F, Error, Int] = {
    dao
      .find(row.name)
      .map(permission => conflict(permission.name))
      .map[Error](Error.ConflictError)
      .toLeft(row)
      .semiflatMap(dao.insert)
      .transact(transactor)
  }

  private def prepareRow(permission: Permission)(id: FUUID): PermissionRow = {
    PermissionRow(id, permission.name)
  }
}

object PermissionService {

  def apply[F[_]: Async](dao: PermissionDao, transactor: Transactor[F]): PermissionService[F] = {
    new PermissionServiceImpl(dao, transactor)
  }
}
