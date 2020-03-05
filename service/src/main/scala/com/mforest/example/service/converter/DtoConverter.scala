package com.mforest.example.service.converter

import cats.data.{Chain, NonEmptyList}

private[service] object DtoConverter {

  type Converter[Row, Dto] = Row => Dto

  implicit class ObjectConverter[Row](row: Row) {

    def to[Dto](implicit converter: Converter[Row, Dto]): Dto = {
      converter(row)
    }
  }

  implicit class ChainConverter[Row](rows: Chain[Row]) {

    def to[Dto](implicit converter: Converter[Row, Dto]): Chain[Dto] = {
      rows.map(converter)
    }
  }

  implicit class NonEmptyListConverter[Row](rows: NonEmptyList[Row]) {

    def to[Dto](implicit converter: Converter[Row, Dto]): NonEmptyList[Dto] = {
      rows.map(converter)
    }
  }
}
