package com.mforest.example.db

import cats.data.{Chain, OptionT}
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.custom.{CustomCompacts, CustomInstances}
import doobie.ConnectionIO
import doobie.syntax.AllSyntax
import doobie.util.query.Query0
import doobie.util.update.Update0

trait Dao[Id, Row] extends CustomInstances with CustomCompacts with AllSyntax {

  def tableName: String

  def insert(row: Row): ConnectionIO[Int] = {
    query.insert(row).run
  }

  def delete(id: Id): ConnectionIO[Int] = {
    query.delete(id).run
  }

  def find(id: Id): OptionT[ConnectionIO, Row] = OptionT {
    query.select(id).option
  }

  def find(pagination: Pagination): ConnectionIO[Chain[Row]] = {
    query.select(pagination).to[Chain]
  }

  protected def query: BaseQuery

  protected trait BaseQuery {

    def insert(row: Row): Update0
    def delete(id: Id): Update0
    def select(id: Id): Query0[Row]
    def select(pagination: Pagination): Query0[Row]
  }
}
