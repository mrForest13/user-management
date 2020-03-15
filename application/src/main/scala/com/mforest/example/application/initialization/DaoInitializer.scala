package com.mforest.example.application.initialization

import com.mforest.example.db.dao.{PermissionDao, UserDao}

case class DaoInitializer(user: UserDao = UserDao(), permission: PermissionDao = PermissionDao())
