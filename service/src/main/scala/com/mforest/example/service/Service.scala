package com.mforest.example.service

import doobie.syntax.ToConnectionIOOps

trait Service extends ToConnectionIOOps {

  def name: String
}
