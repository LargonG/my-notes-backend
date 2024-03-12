package org.kote.client.notion.model.block

import cats.implicits.toFunctorOps
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}
import org.kote.client.notion.model.block.BlockType.asString
import org.kote.client.notion.model.parent.PageParent
import org.kote.client.notion.model.text.RichText
import org.kote.client.notion.utils.Typed
import org.kote.client.notion.utils.Typed.ToTyped

import java.util.UUID

//////////////
// Response //
//////////////

case class BlockResponse(
    id: BlockId,
    parent: PageParent,
    achieved: Boolean,
    value: BlockResponseValue,
)

object BlockResponse {
  implicit val blockResponseDecoder: Decoder[BlockResponse] =
    Decoder.instance { cur =>
      for {
        id <- cur.get[BlockId]("id")
        parent <- cur.get[PageParent]("parent")
        valueType <- cur.get[BlockType]("type")
        achieved <- cur.get[Boolean]("achieved")
        value <- valueType match {
          case ParagraphType => cur.get[ParagraphResponse](valueType)
        }
      } yield BlockResponse(id, parent, achieved, value)
    }
}

/////////////////////
// Possible values //
/////////////////////

sealed trait BlockResponseValue

object BlockResponseValue {
  implicit val blockResponseValueDecoder: Decoder[BlockResponseValue] =
    List[Decoder[BlockResponseValue]](
      Decoder[ParagraphResponse].widen,
    ).reduceLeft(_ or _)

  implicit val blockResponseValueType: Typed[BlockResponseValue, BlockType] = {
    case ParagraphResponse(_) => ParagraphType
  }
}

final case class ParagraphResponse(richText: List[RichText]) extends BlockResponseValue

object ParagraphResponse {
  implicit val paragraphResponseDecoder: Decoder[ParagraphResponse] =
    Decoder.instance(_.get[List[RichText]]("rich_text")).map(ParagraphResponse(_))
}

/////////////
// Request //
/////////////

case class BlockRequest(
    value: BlockRequestValue,
)

object BlockRequest {
  implicit val blockRequestEncoder: Encoder[BlockRequest] =
    Encoder.instance { source =>
      Json.obj(
        "object" -> "block".asJson,
        "type" -> source.value.toType.asJson,
        asString(source.value.toType) -> source.value.asJson,
      )
    }

  def paragraph(list: List[RichText]): BlockRequest =
    BlockRequest(ParagraphRequest(list))
}

/////////////////////
// Possible values //
/////////////////////

sealed trait BlockRequestValue

object BlockRequestValue {
  implicit val blockRequestValueEncoder: Encoder[BlockRequestValue] =
    Encoder.instance { case paragraph @ ParagraphRequest(_) =>
      paragraph.asJson
    }

  implicit val blockRequestValueType: Typed[BlockRequestValue, BlockType] = {
    case ParagraphRequest(_) => ParagraphType
  }
}

final case class ParagraphRequest(richText: List[RichText]) extends BlockRequestValue

object ParagraphRequest {
  implicit val paragraphRequestEncoder: Encoder[ParagraphRequest] =
    Encoder.instance { source =>
      Json.obj("type" -> "text".asJson, "rich_text" -> source.richText.asJson)
    }
}

final case class BlockId(inner: UUID) extends AnyVal

object BlockId {
  implicit val blockIdEncoder: Encoder[BlockId] = Encoder.encodeUUID.contramap(_.inner)
  implicit val blockIdDecoder: Decoder[BlockId] = Decoder.decodeUUID.map(BlockId(_))
}
