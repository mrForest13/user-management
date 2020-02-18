package com.mforest.example.db.dao

import cats.Id
import cats.data.{Chain, OptionT}
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.Dao
import com.mforest.example.db.row.PermissionRow
import doobie.free.connection.ConnectionIO
import doobie.util.query.Query0
import doobie.util.update.Update0
import io.chrisdavenport.fuuid.FUUID

trait PermissionDao extends Dao[Id[FUUID], PermissionRow] {

  def find(name: String): OptionT[ConnectionIO, PermissionRow]
  def find(pagination: Pagination): ConnectionIO[Chain[PermissionRow]]
  def findByUser(id: Id[FUUID]): ConnectionIO[Chain[PermissionRow]]
  def delete(id: Id[FUUID]): ConnectionIO[Int]
}

class PermissionDaoImpl extends PermissionDao {

  override def insert(row: PermissionRow): ConnectionIO[Int] = {
    Query.insert(row).run
  }

  override def find(id: Id[FUUID]): OptionT[ConnectionIO, PermissionRow] = OptionT {
    Query.select(id).option
  }

  override def find(name: String): OptionT[ConnectionIO, PermissionRow] = OptionT {
    Query.select(name).option
  }

  override def find(pagination: Pagination): ConnectionIO[Chain[PermissionRow]] = {
    Query.select(pagination).to[Chain]
  }

  override def delete(id: Id[FUUID]): ConnectionIO[Int] = {
    Query.delete(id).run
  }

  override def findByUser(id: Id[FUUID]): ConnectionIO[Chain[PermissionRow]] = {
    Query.selectByUser(id).to[Chain]
  }

  private object Query extends Query {

    def insert(permission: PermissionRow): Update0 = sql"""
      INSERT INTO PERMISSIONS (ID, NAME) VALUES (${permission.id}, ${permission.name})
    """.update

    def select(id: Id[FUUID]): Query0[PermissionRow] = sql"""
      SELECT ID, NAME FROM PERMISSIONS WHERE ID = $id
    """.query

    def selectByUser(id: Id[FUUID])(implicit d: DummyImplicit): Query0[PermissionRow] = sql"""
      SELECT ID, NAME
      FROM PERMISSIONS
      JOIN USERS_PERMISSIONS
      ON PERMISSIONS.ID = USERS_PERMISSIONS.PERMISSION_ID
      WHERE USERS_PERMISSIONS.USER_ID = $id
    """.query

    def delete(id: Id[FUUID]): Update0 = sql"""
      DELETE FROM PERMISSIONS WHERE ID = $id
    """.update

    def select(name: String): Query0[PermissionRow] = sql"""
      SELECT ID, NAME FROM PERMISSIONS WHERE NAME = $name
    """.query

    def select(pagination: Pagination): Query0[PermissionRow] = sql"""
      SELECT ID, NAME FROM PERMISSIONS
      ORDER BY CREATED_AT ASC
      LIMIT ${pagination.size} OFFSET ${pagination.offset}
    """.query
  }
}

object PermissionDao {

  def apply(): PermissionDao = new PermissionDaoImpl()
}
