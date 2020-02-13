package com.mforest.example.service.converter

import cats.data.Chain

object DtoConverter {

  type Converter[Row, Dto] = Row => Dto

  implicit class DtoConverterHelper[Row](row: Row) {

    def to[Dto](implicit converter: Converter[Row, Dto]): Dto = {
      converter(row)
    }
  }

  implicit class DtoChainConverterHelper[Row](rows: Chain[Row]) {

    def to[Dto](implicit converter: Converter[Row, Dto]): Chain[Dto] = {
      rows.map(converter)
    }
  }
}
