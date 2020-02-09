package com.mforest.example.application.layer

import com.mforest.example.db.dao.UserDao

trait DaoLayer {

  val userDao: UserDao = UserDao()
}
