package com.mforest.example.service.model

object UserMock {

  def gen(): User = {
    User(
      email = "john.smith@gmail.com",
      password = "example",
      firstName = "john",
      lastName = "smith",
      city = "London",
      country = "England",
      phone = "123456789"
    )
  }
}
