package com.mforest.example.db.dao

import cats.data.OptionT
import com.mforest.example.db.Dao
import com.mforest.example.db.instances.CustomInstances
import com.mforest.example.db.row.PermissionRow
import doobie.free.connection.ConnectionIO
import doobie.syntax.AllSyntax
import doobie.util.query.Query0
import doobie.util.update.Update0

trait PermissionDao extends Dao[PermissionRow] {

  def find(name: String): OptionT[ConnectionIO, PermissionRow]
}

class PermissionDaoImpl extends PermissionDao with CustomInstances with AllSyntax {

  override def insert(row: PermissionRow): ConnectionIO[Int] = {
    Query.insert(row).run
  }

  def find(name: String): OptionT[ConnectionIO, PermissionRow] = OptionT {
    Query.select(name).option
  }

  private object Query {

    def insert(permission: PermissionRow): Update0 = sql"""
      INSERT INTO PERMISSIONS (ID, NAME)
      VALUES (${permission.id}, ${permission.name})
    """.update

    def select(name: String): Query0[PermissionRow] = sql"""
      SELECT ID, NAME
      FROM PERMISSIONS
      WHERE NAME = $name
    """.query
  }
}
