package com.mforest.example.db.query

import cats.Id
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.Query
import com.mforest.example.db.row.UserRow
import doobie.util.query.Query0
import doobie.util.update.Update0
import io.chrisdavenport.fuuid.FUUID

private[db] final class UserQuery extends Query[Id[FUUID], UserRow] {

  def insert(user: UserRow): Update0 = sql"""
      INSERT INTO USERS (ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME, CITY, COUNTRY, PHONE)
      VALUES (${user.id}, ${user.email}, ${user.hash}, ${user.salt}, ${user.firstName},
      ${user.lastName}, ${user.city}, ${user.country}, ${user.phone})
    """.update

  def delete(id: Id[FUUID]): Update0 = sql"""
      DELETE FROM USERS WHERE ID = $id
    """.update

  def select(id: Id[FUUID]): Query0[UserRow] = sql"""
      SELECT ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME, CITY, COUNTRY, PHONE
      FROM USERS
      WHERE ID = $id
    """.query

  def select(pagination: Pagination): Query0[UserRow] = sql"""
      SELECT ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME, CITY, COUNTRY, PHONE
      FROM USERS
      ORDER BY CREATED_AT ASC
      LIMIT ${pagination.size} OFFSET ${pagination.offset}
    """.query

  def select(email: String): Query0[UserRow] = sql"""
      SELECT ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME, CITY, COUNTRY, PHONE
      FROM USERS
      WHERE EMAIL = $email
    """.query

  def add(userId: Id[FUUID], permissionId: Id[FUUID]): Update0 = sql"""
      INSERT INTO USERS_PERMISSIONS (USER_ID, PERMISSION_ID)
      VALUES ($userId, $permissionId)
      ON CONFLICT DO NOTHING
    """.update

  def delete(userId: Id[FUUID], permissionId: Id[FUUID]): Update0 = sql"""
      DELETE FROM USERS_PERMISSIONS WHERE USER_ID = $userId AND PERMISSION_ID = $permissionId
    """.update
}
