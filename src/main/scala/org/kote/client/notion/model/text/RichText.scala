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

  def text(content: String): Text = Text(content)
}

final case class Text(
    content: String,
) extends RichText

object Text {
  implicit val textEncoder: Encoder[Text] =
    Encoder.instance { source =>
      Json.obj(
        "type" -> "text".asJson,
        "text" -> Json.obj(
          "content" -> source.content.asJson,
        ),
      )
    }

  implicit val textDecoder: Decoder[Text] =
    Decoder.instance(_.get[String]("plain_text")).map(Text(_))
}
