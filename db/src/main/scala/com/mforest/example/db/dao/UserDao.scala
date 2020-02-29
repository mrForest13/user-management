package com.mforest.example.db.dao

import cats.Id
import cats.data.OptionT
import com.mforest.example.db.Dao
import com.mforest.example.db.query.UserQuery
import com.mforest.example.db.row.UserRow
import doobie.free.connection.ConnectionIO
import io.chrisdavenport.fuuid.FUUID

trait UserDao extends Dao[Id[FUUID], UserRow] {

  val tableName: String = "USERS"

  def delete(userId: Id[FUUID], permissionId: Id[FUUID]): ConnectionIO[Int]
  def add(userId: Id[FUUID], permissionId: Id[FUUID]): ConnectionIO[Int]
  def find(email: String): OptionT[ConnectionIO, UserRow]
}

private[db] final class UserDaoImpl extends UserDao {

  override def find(email: String): OptionT[ConnectionIO, UserRow] = OptionT {
    query.select(email).option
  }

  override def delete(userId: Id[FUUID], permissionId: Id[FUUID]): ConnectionIO[Int] = {
    query.delete(userId, permissionId).run
  }

  override def add(userId: Id[FUUID], permissionId: Id[FUUID]): ConnectionIO[Int] = {
    query.add(userId, permissionId).run
  }

  protected val query: UserQuery = new UserQuery
}

object UserDao {

  def apply(): UserDao = new UserDaoImpl
}
