package com.mforest.example.db

import com.mforest.example.core.model.Pagination
import com.mforest.example.db.custom.CustomInstances
import doobie.syntax.ToSqlInterpolator
import doobie.util.query.Query0
import doobie.util.update.Update0

private[db] trait Query[Id, Row] extends CustomInstances with ToSqlInterpolator {

  def tableName: String

  def insert(row: Row): Update0
  def delete(id: Id): Update0
  def select(id: Id): Query0[Row]
  def select(pagination: Pagination): Query0[Row]
}
