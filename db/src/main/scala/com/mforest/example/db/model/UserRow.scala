package com.mforest.example.db.model

import cats.Id
import io.chrisdavenport.fuuid.FUUID

case class UserRow(
    id: Id[FUUID],
    email: String,
    hash: String,
    salt: FUUID,
    firstName: String,
    lastName: String,
    city: String,
    country: String,
    phone: String
)
