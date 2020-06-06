package com.mforest.example.application.initialization

import com.mforest.example.db.dao.{PermissionDao, UserDao}

final case class DaoInitializer(user: UserDao = UserDao(), permission: PermissionDao = PermissionDao())
