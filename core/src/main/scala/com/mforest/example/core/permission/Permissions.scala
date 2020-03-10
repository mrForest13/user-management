package com.mforest.example.core.permission

import cats.Show

object Permissions extends Enumeration {

  type Permission = Value

  val USER_MANAGEMENT_REVOKE_PERMISSION_FOR_USERS: Permission = Value
  val USER_MANAGEMENT_ADD_PERMISSION_FOR_USERS: Permission    = Value
  val USER_MANAGEMENT_GET_USER_PERMISSIONS: Permission        = Value
  val USER_MANAGEMENT_GET_PERMISSIONS: Permission             = Value
  val USER_MANAGEMENT_ADD_PERMISSION: Permission              = Value
  val USER_MANAGEMENT_GET_USERS: Permission                   = Value

  implicit val show: Show[Permission] = Show.fromToString
}
