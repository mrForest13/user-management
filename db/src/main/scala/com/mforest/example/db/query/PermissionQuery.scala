package com.mforest.example.db.query

import cats.Id
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.Query
import com.mforest.example.db.row.PermissionRow
import doobie.util.query.Query0
import doobie.util.update.Update0
import io.chrisdavenport.fuuid.FUUID

private[db] final class PermissionQuery extends Query[Id[FUUID], PermissionRow] {

  def insert(permission: PermissionRow): Update0 = sql"""
      INSERT INTO PERMISSIONS (ID, NAME) VALUES (${permission.id}, ${permission.name})
    """.update

  def select(id: Id[FUUID]): Query0[PermissionRow] = sql"""
      SELECT ID, NAME FROM PERMISSIONS WHERE ID = $id
    """.query

  def delete(id: Id[FUUID]): Update0 = sql"""
      DELETE FROM PERMISSIONS WHERE ID = $id
    """.update

  def select(pagination: Pagination): Query0[PermissionRow] = sql"""
      SELECT ID, NAME
      FROM PERMISSIONS
      ORDER BY CREATED_AT ASC
      LIMIT ${pagination.size} OFFSET ${pagination.offset}
    """.query

  def selectByUser(id: Id[FUUID]): Query0[PermissionRow] = sql"""
      SELECT ID, NAME
      FROM PERMISSIONS
      JOIN USERS_PERMISSIONS
      ON PERMISSIONS.ID = USERS_PERMISSIONS.PERMISSION_ID
      WHERE USERS_PERMISSIONS.USER_ID = $id
    """.query

  def select(name: String): Query0[PermissionRow] = sql"""
      SELECT ID, NAME FROM PERMISSIONS WHERE NAME = $name
    """.query
}
