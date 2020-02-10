package com.mforest.example.service.login

import cats.data.EitherT
import com.mforest.example.service.Service
import com.mforest.example.service.model.Credentials

trait LoginService[F[_]] extends Service {

  def login(credentials: Credentials): EitherT[F, Error, String]
}
