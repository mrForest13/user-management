package com.mforest.example.db.row

import cats.effect.IO
import io.chrisdavenport.fuuid.FUUID

object UserRowMock {

  def gen(): UserRow = UserRow(
    id = randomUnsafeId,
    email = "john.smith@gmail.com",
    hash = "hash",
    salt = randomUnsafeId,
    firstName = "john",
    lastName = "smith",
    city = "London",
    country = "England",
    phone = "123456789"
  )

  def gen(email: String): UserRow = {
    gen().copy(email = email)
  }

  private def randomUnsafeId: FUUID = {
    FUUID.randomFUUID[IO].unsafeRunSync()
  }
}
