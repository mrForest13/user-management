package com.mforest.example.service.model

final case class User(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    city: String,
    country: String,
    phone: String
)
