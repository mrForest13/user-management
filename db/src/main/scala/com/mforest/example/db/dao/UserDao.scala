package com.mforest.example.db.dao

import cats.data.OptionT
import com.mforest.example.db.Dao
import com.mforest.example.db.instances.CustomInstances
import com.mforest.example.db.model.UserRow
import doobie.free.connection.ConnectionIO
import doobie.syntax.AllSyntax
import doobie.util.query.Query0
import doobie.util.update.Update0

trait UserDao extends Dao[UserRow] {

  def find(email: String): OptionT[ConnectionIO, UserRow]
}

class UserDaoImpl extends UserDao with CustomInstances with AllSyntax {

  def insert(user: UserRow): ConnectionIO[Int] = {
    Query.insert(user).run
  }

  def find(email: String): OptionT[ConnectionIO, UserRow] = OptionT {
    Query.select(email).option
  }

  private object Query {

    def insert(user: UserRow): Update0 = sql"""
      INSERT INTO USERS (ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME, CITY, COUNTRY, PHONE)
      VALUES (${user.id}, ${user.email}, ${user.hash}, ${user.salt}, ${user.firstName},
      ${user.lastName}, ${user.city}, ${user.country}, ${user.phone})
    """.update

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
