package com.mforest.example.service

import cats.syntax.AllSyntax
import doobie.syntax.ToConnectionIOOps

private[service] trait Service extends ToConnectionIOOps with AllSyntax {

  def name: String
}
