package org.kote.client.notion.model.text

import cats.implicits.toFunctorOps
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

sealed trait RichText

object RichText {
  implicit val richTextEncoder: Encoder[RichText] = Encoder.instance { case text @ Text(_) =>
    text.asJson
  }

  implicit val richTextDecoder: Decoder[RichText] = List[Decoder[RichText]](
    Decoder[Text].widen,
  ).reduceLeft(_ or _)
}

final case class Text(
    plainText: String,
) extends RichText

object Text {
  implicit val textEncoder: Encoder[Text] =
    Encoder.instance { source =>
      Json.obj(
        "type" -> "text".asJson,
        "text" -> Json.obj(
          "content" -> source.plainText.asJson,
        ),
      )
    }

  implicit val textDecoder: Decoder[Text] =
    Decoder.instance(_.get[String]("plain_text")).map(Text(_))
}
