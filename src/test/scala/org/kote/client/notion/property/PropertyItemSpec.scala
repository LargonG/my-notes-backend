package org.kote.client.notion.property

import io.circe.parser._
import org.kote.client.notion.model.property.PropertyItem
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PropertyItemSpec extends AnyFlatSpec with Matchers with EitherValues {
  it should "parse null" in {
    val json = """null""".stripMargin

    val res = decode[Option[String]](json)

    res.isRight shouldBe true: Unit
    res.getOrElse(Some("hello")) shouldBe None
  }

  it should "returns list of titles" in {
    val json =
      """
        |{
        |    "object": "list",
        |    "results": [
        |        {
        |            "object": "property_item",
        |            "type": "people",
        |            "id": "kkkk",
        |            "people": {
        |                "object": "user",
        |                "id": "8dcef509-b7b6-488b-9737-4e2af587cf23",
        |                "name": "Ivan"
        |            }
        |        },
        |        {
        |            "object": "property_item",
        |            "type": "people",
        |            "id": "kkkk",
        |            "people": {
        |                "object": "user",
        |                "id": "96447a79-2236-493b-a080-338a57ce45bc",
        |                "name": "Lisa"
        |            }
        |        }
        |    ],
        |    "next_cursor": null,
        |    "has_more": false,
        |    "type": "property_item",
        |    "property_item": {
        |        "id": "kkkk",
        |        "next_url": null,
        |        "type": "people",
        |        "people": {}
        |    },
        |    "developer_survey": "https://notionup.typeform.com/to/lhiugytfyfuyjgy",
        |    "request_id": "4ed57757-40a4-4133-b6ab-4d59de14c3ed"
        |}
        |""".stripMargin

    val result = decode[PropertyItem](json)
    result.isRight shouldBe true
  }
}
