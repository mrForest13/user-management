package com.mforest.example.service.model

import com.mforest.example.db.row.UserRow
import io.chrisdavenport.fuuid.FUUID

final case class User(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    city: String,
    country: String,
    phone: String
) {

  def toRow(id: FUUID, salt: FUUID, hash: String): UserRow = {
    UserRow(id, email, hash, salt, firstName, lastName, city, country, phone)
  }
}
