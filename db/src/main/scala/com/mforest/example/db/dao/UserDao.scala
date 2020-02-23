package com.mforest.example.db.dao

import cats.Id
import cats.data.OptionT
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.Dao
import com.mforest.example.db.row.UserRow
import doobie.free.connection.ConnectionIO
import doobie.util.query.Query0
import doobie.util.update.Update0
import io.chrisdavenport.fuuid.FUUID

trait UserDao extends Dao[Id[FUUID], UserRow] {

  val tableName: String = "USERS"

  def delete(userId: Id[FUUID], permissionId: Id[FUUID]): ConnectionIO[Int]
  def add(userId: Id[FUUID], permissionId: Id[FUUID]): ConnectionIO[Int]
  def find(email: String): OptionT[ConnectionIO, UserRow]
}

class UserDaoImpl extends UserDao {

  override def find(email: String): OptionT[ConnectionIO, UserRow] = OptionT {
    query.select(email).option
  }

  override def delete(userId: Id[FUUID], permissionId: Id[FUUID]): ConnectionIO[Int] = {
    query.delete(userId, permissionId).run
  }

  override def add(userId: Id[FUUID], permissionId: Id[FUUID]): ConnectionIO[Int] = {
    query.add(userId, permissionId).run
  }

  protected val query: Query = new Query

  protected class Query extends BaseQuery {

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
}

object UserDao {

  def apply(): UserDao = new UserDaoImpl
}
