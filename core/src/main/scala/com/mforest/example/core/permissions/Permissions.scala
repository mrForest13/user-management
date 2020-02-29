package com.mforest.example.core.permissions

import scala.language.implicitConversions

object Permissions extends Enumeration {

  type Permission = Value

  val USER_MANAGEMENT_REVOKE_PERMISSION_FOR_USERS: Permission = Value
  val USER_MANAGEMENT_ADD_PERMISSION_FOR_USERS: Permission    = Value
  val USER_MANAGEMENT_GET_USER_PERMISSIONS: Permission        = Value
  val USER_MANAGEMENT_GET_PERMISSIONS: Permission             = Value
  val USER_MANAGEMENT_ADD_PERMISSION: Permission              = Value
  val USER_MANAGEMENT_GET_USERS: Permission                   = Value

  implicit def toString(permission: Permission): String = permission.toString
}
