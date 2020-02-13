package com.mforest.example.application.layer

import com.mforest.example.db.dao.{PermissionDao, UserDao}

trait DaoLayer {

  val userDao: UserDao             = UserDao()
  val permissionDao: PermissionDao = PermissionDao()
}
