package com.mforest.example.db

import doobie.ConnectionIO

trait Dao[Row] {

  def insert(user: Row): ConnectionIO[Int]
}
