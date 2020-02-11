package com.mforest.example.service.permission

import cats.data.EitherT
import cats.data.EitherT.right
import cats.effect.Async
import com.mforest.example.core.error.Error
import com.mforest.example.core.error.Error.ConflictError
import com.mforest.example.db.dao.PermissionDao
import com.mforest.example.db.row.PermissionRow
import com.mforest.example.service.Service
import com.mforest.example.service.model.Permission
import doobie.implicits.AsyncConnectionIO
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

trait PermissionService[F[_]] extends Service {

  def addPermission(permission: Permission): EitherT[F, Error, String]
}

class PermissionServiceImpl[F[_]: Async](dao: PermissionDao, transactor: Transactor[F]) extends PermissionService[F] {

  private val created  = (name: String) => s"The permission with name $name has been created"
  private val conflict = (name: String) => s"The permission with name $name already exists!"

  override def addPermission(permission: Permission): EitherT[F, Error, String] = {
    for {
      logger <- right(Slf4jLogger.create[F])
      id     <- right(FUUID.randomFUUID[F])
      row    = prepareRow(id, permission)
      _      <- insertPermission(row)
      info   = created(permission.name)
      _      <- right(logger.info(info))
    } yield info
  }

  private def insertPermission(row: PermissionRow): EitherT[F, Error, Int] = {
    dao
      .find(row.name)
      .map(permission => conflict(permission.name))
      .map[Error](ConflictError)
      .toLeft(row)
      .semiflatMap(dao.insert)
      .transact(transactor)
  }

  private def prepareRow(id: FUUID, permission: Permission): PermissionRow = {
    PermissionRow(id, permission.name)
  }
}
