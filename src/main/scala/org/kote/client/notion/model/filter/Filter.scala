package org.kote.client.notion.model.filter

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}

sealed trait Filter

case object PageFilter extends Filter {
  implicit val encoder: Encoder[PageFilter.type] =
    Encoder.instance { _ =>
      Json.obj(
        "value" -> "page".asJson,
        "property" -> "object".asJson,
      )
    }
}

case object DatabaseFilter extends Filter {
  implicit val encoder: Encoder[DatabaseFilter.type] =
    Encoder.instance { _ =>
      Json.obj(
        "value" -> "database".asJson,
        "property" -> "object".asJson,
      )
    }
}
