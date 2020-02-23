package com.mforest.example.db.dao

import cats.Id
import cats.data.{Chain, OptionT}
import com.mforest.example.db.Dao
import com.mforest.example.db.query.PermissionQuery
import com.mforest.example.db.row.PermissionRow
import doobie.free.connection.ConnectionIO
import io.chrisdavenport.fuuid.FUUID

trait PermissionDao extends Dao[Id[FUUID], PermissionRow] {

  val tableName: String = "PERMISSIONS"

  def find(name: String): OptionT[ConnectionIO, PermissionRow]
  def findByUser(id: Id[FUUID]): ConnectionIO[Chain[PermissionRow]]
  def delete(id: Id[FUUID]): ConnectionIO[Int]
}

class PermissionDaoImpl extends PermissionDao {

  override def find(name: String): OptionT[ConnectionIO, PermissionRow] = OptionT {
    query.select(name).option
  }

  override def findByUser(id: Id[FUUID]): ConnectionIO[Chain[PermissionRow]] = {
    query.selectByUser(id).to[Chain]
  }

  protected val query: PermissionQuery = new PermissionQuery
}

object PermissionDao {

  def apply(): PermissionDao = new PermissionDaoImpl()
}
