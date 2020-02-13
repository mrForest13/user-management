package com.mforest.example.db

import cats.data.OptionT
import com.mforest.example.db.custom.{CustomCompacts, CustomInstances}
import doobie.ConnectionIO
import doobie.syntax.AllSyntax
import doobie.util.query.Query0
import doobie.util.update.Update0

trait Dao[Id, Row] extends CustomInstances with CustomCompacts with AllSyntax {

  def insert(row: Row): ConnectionIO[Int]
  def find(id: Id): OptionT[ConnectionIO, Row]

  protected trait Query {

    def insert(row: Row): Update0
    def select(id: Id): Query0[Row]
  }
}
