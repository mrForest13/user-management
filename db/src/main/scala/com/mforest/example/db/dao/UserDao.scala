package com.mforest.example.db.dao

import cats.Id
import cats.data.OptionT
import com.mforest.example.db.Dao
import com.mforest.example.db.row.UserRow
import doobie.free.connection.ConnectionIO
import doobie.util.query.Query0
import doobie.util.update.Update0
import io.chrisdavenport.fuuid.FUUID

trait UserDao extends Dao[Id[FUUID], UserRow] {

  def find(email: String): OptionT[ConnectionIO, UserRow]
}

class UserDaoImpl extends UserDao {

  override def insert(row: UserRow): ConnectionIO[Int] = {
    Query.insert(row).run
  }

  override def find(id: Id[FUUID]): OptionT[ConnectionIO, UserRow] = OptionT {
    Query.select(id).option
  }

  override def find(email: String): OptionT[ConnectionIO, UserRow] = OptionT {
    Query.select(email).option
  }

  private object Query extends Query {

    def insert(user: UserRow): Update0 = sql"""
      INSERT INTO USERS (ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME, CITY, COUNTRY, PHONE)
      VALUES (${user.id}, ${user.email}, ${user.hash}, ${user.salt}, ${user.firstName},
      ${user.lastName}, ${user.city}, ${user.country}, ${user.phone})
    """.update

    def select(id: Id[FUUID]): Query0[UserRow] = sql"""
      SELECT ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME, CITY, COUNTRY, PHONE
      FROM USERS
      WHERE ID = $id
    """.query

    def select(email: String): Query0[UserRow] = sql"""
      SELECT ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME, CITY, COUNTRY, PHONE
      FROM USERS
      WHERE EMAIL = $email
    """.query
  }
}

object UserDao {

  def apply(): UserDao = new UserDaoImpl
}
