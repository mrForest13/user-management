package com.mforest.example.db.row

import cats.effect.IO
import io.chrisdavenport.fuuid.FUUID

object PermissionRowMock {

  def gen(): PermissionRow = {
    PermissionRow(randomUnsafeId, "EXAMPLE_PERMISSION")
  }

  def gen(name: String): PermissionRow = {
    gen().copy(name = name)
  }

  private def randomUnsafeId: FUUID = {
    FUUID.randomFUUID[IO].unsafeRunSync()
  }
}
