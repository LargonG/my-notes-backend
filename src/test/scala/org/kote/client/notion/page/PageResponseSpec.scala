package org.kote.client.notion.page

import io.circe.parser.decode
import org.kote.client.notion.model.database.DbId
import org.kote.client.notion.model.page.{PageId, PagePropertyResponse, PageResponse}
import org.kote.client.notion.model.parent.Parent
import org.kote.client.notion.model.user.{UserId, UserResponse}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class PageResponseSpec extends AnyFlatSpec with Matchers {
  it should "parse status property" in {
    val json =
      """
        |{
        |    "id": "N%60a%40",
        |    "type": "status",
        |    "status": {
        |        "id": "1623fd32-3589-4eb8-8a7c-1da896c7afbb",
        |        "name": "Not started",
        |        "color": "default"
        |    }
        |}
        |""".stripMargin

    val res = decode[PagePropertyResponse](json)

    println(res)

    res.isRight shouldBe true: Unit
  }

  it should "parse" in {
    val json =
      """
        |{
        |    "object": "page",
        |    "id": "7f439d03-d31d-40d1-b97b-9068aa473a2d",
        |    "created_time": "2024-03-03T21:47:00.000Z",
        |    "last_edited_time": "2024-03-05T17:16:00.000Z",
        |    "created_by": {
        |        "object": "user",
        |        "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |    },
        |    "last_edited_by": {
        |        "object": "user",
        |        "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |    },
        |    "cover": null,
        |    "icon": null,
        |    "parent": {
        |        "type": "database_id",
        |        "database_id": "16d408f9-1f00-407c-9840-8a789ee6c75d"
        |    },
        |    "archived": false,
        |    "properties": {
        |        "Status": {
        |            "id": "BgrT",
        |            "type": "select",
        |            "select": null
        |        },
        |        "Description": {
        |            "id": "EUfT",
        |            "type": "rich_text",
        |            "rich_text": []
        |        },
        |        "Person": {
        |            "id": "GgLj",
        |            "type": "people",
        |            "people": []
        |        },
        |        "Status 1": {
        |            "id": "N%60a%40",
        |            "type": "status",
        |            "status": {
        |                "id": "1623fd32-3589-4eb8-8a7c-1da896c7afbb",
        |                "name": "Not started",
        |                "color": "default"
        |            }
        |        },
        |        "Created time": {
        |            "id": "TUo%40",
        |            "type": "created_time",
        |            "created_time": "2024-03-03T21:47:00.000Z"
        |        },
        |        "Новое поле": {
        |            "id": "Ulla",
        |            "type": "rich_text",
        |            "rich_text": [
        |                {
        |                    "type": "text",
        |                    "text": {
        |                        "content": "🍎Fruit",
        |                        "link": null
        |                    },
        |                    "annotations": {
        |                        "bold": false,
        |                        "italic": false,
        |                        "strikethrough": false,
        |                        "underline": false,
        |                        "code": false,
        |                        "color": "default"
        |                    },
        |                    "plain_text": "🍎Fruit",
        |                    "href": null
        |                }
        |            ]
        |        },
        |        "Group": {
        |            "id": "Vm%3Ay",
        |            "type": "rich_text",
        |            "rich_text": []
        |        },
        |        "Last edited time": {
        |            "id": "dLFN",
        |            "type": "last_edited_time",
        |            "last_edited_time": "2024-03-05T17:16:00.000Z"
        |        },
        |        "Выбор без выбыра": {
        |            "id": "mJGR",
        |            "type": "select",
        |            "select": {
        |                "id": "73091846-a067-4e99-be71-ae05dbb478e7",
        |                "name": "🍎Fruit",
        |                "color": "red"
        |            }
        |        },
        |        "": {
        |            "id": "mUt%40",
        |            "type": "rich_text",
        |            "rich_text": []
        |        },
        |        "Выбор без выбора": {
        |            "id": "nBjn",
        |            "type": "select",
        |            "select": {
        |                "id": "c5f3ae0d-eb94-4b48-96fb-41122d3f3dc9",
        |                "name": "🥦Vegetable",
        |                "color": "purple"
        |            }
        |        },
        |        "Name": {
        |            "id": "title",
        |            "type": "title",
        |            "title": []
        |        }
        |    },
        |    "url": "https://www.notion.so/7f439d03d31d40d1b97b9068aa473a2d",
        |    "public_url": null
        |}
        |""".stripMargin

    val defaultValue = PageResponse(
      PageId(UUID.fromString("2c89ef22-e18b-4d2d-9337-8b03ab5c3507")),
      UserResponse(
        UserId(UUID.fromString("d610ba71-8ae3-4373-9c28-e78a97a2dd05")),
        None,
      ),
      Parent.db(DbId(UUID.fromString("525718c6-4cbd-4e96-b944-07a65d1ed07a"))),
      archived = false,
      Map(),
    )
    val res = decode[PageResponse](json)

    println(res)

    res.isRight shouldBe true: Unit
    val list = res.getOrElse(defaultValue)
    list shouldNot equal(defaultValue): Unit

    println(list)
  }
}
