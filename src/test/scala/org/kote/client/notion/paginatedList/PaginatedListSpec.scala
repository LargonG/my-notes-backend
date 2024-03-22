package org.kote.client.notion.paginatedList

import org.kote.client.notion.model.list.PaginatedList
import org.kote.client.notion.model.user.UserResponse
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.circe.parser._
import org.kote.client.notion.model.page.response.PageResponse

class PaginatedListSpec extends AnyFlatSpec with Matchers {
  it should "parse user paginated list" in {
    val json =
      """
        |{
        |    "object": "list",
        |    "results": [
        |        {
        |            "object": "user",
        |            "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca",
        |            "name": "Anton Panov",
        |            "avatar_url": null,
        |            "type": "person",
        |            "person": {}
        |        },
        |        {
        |            "object": "user",
        |            "id": "d5a0e559-30a9-4405-8f92-d9f30ebb3b89",
        |            "name": "My Notes",
        |            "avatar_url": null,
        |            "type": "bot",
        |            "bot": {
        |                "owner": {
        |                    "type": "workspace",
        |                    "workspace": true
        |                },
        |                "workspace_name": "Anton Panov's Notion"
        |            }
        |        }
        |    ],
        |    "next_cursor": null,
        |    "has_more": false,
        |    "type": "user",
        |    "user": {},
        |    "developer_survey": "https://notionup.typeform.com/to/bllBsoI4?utm_source=postman",
        |    "request_id": "0bc5755f-ae9e-40d8-b485-f6ea21654ab2"
        |}
        |""".stripMargin

    val defaultValue = PaginatedList(List.empty, None, hasMore = false)
    val res = decode[PaginatedList[UserResponse]](json)

    res.isRight shouldBe true: Unit
    val list = res.getOrElse(defaultValue)
    list shouldNot equal(defaultValue): Unit

    println(list)
  }

  it should "parse page paginated list" in {
    val json =
      """
        |{
        |    "object": "list",
        |    "results": [
        |        {
        |            "object": "page",
        |            "id": "e1a9265f-b30e-4747-b725-43a893a3d42b",
        |            "created_time": "2024-03-03T19:43:00.000Z",
        |            "last_edited_time": "2024-03-08T23:38:00.000Z",
        |            "created_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "last_edited_by": {
        |                "object": "user",
        |                "id": "d5a0e559-30a9-4405-8f92-d9f30ebb3b89"
        |            },
        |            "cover": null,
        |            "icon": null,
        |            "parent": {
        |                "type": "database_id",
        |                "database_id": "cd252ce3-0a41-49aa-afbf-a18aea6a901d"
        |            },
        |            "archived": false,
        |            "properties": {
        |                "Assign": {
        |                    "id": "A%7Do~",
        |                    "type": "people",
        |                    "people": []
        |                },
        |                "Date": {
        |                    "id": "QSDS",
        |                    "type": "date",
        |                    "date": null
        |                },
        |                "tag": {
        |                    "id": "ceUX",
        |                    "type": "rich_text",
        |                    "rich_text": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "abacaba",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "abacaba",
        |                            "href": null
        |                        }
        |                    ]
        |                },
        |                "Status": {
        |                    "id": "fRyf",
        |                    "type": "status",
        |                    "status": {
        |                        "id": "3c693a8a-6e00-497b-8d8a-c8a6d732d882",
        |                        "name": "Done",
        |                        "color": "green"
        |                    }
        |                },
        |                "Name": {
        |                    "id": "title",
        |                    "type": "title",
        |                    "title": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "Card 1",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "Card 1",
        |                            "href": null
        |                        }
        |                    ]
        |                }
        |            },
        |            "url": "https://www.notion.so/Card-1-e1a9265fb30e4747b72543a893a3d42b",
        |            "public_url": null
        |        },
        |        {
        |            "object": "page",
        |            "id": "7981b2fd-0c98-409f-bb11-0fe5a4e2a70c",
        |            "created_time": "2024-03-03T20:14:00.000Z",
        |            "last_edited_time": "2024-03-06T19:10:00.000Z",
        |            "created_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "last_edited_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "cover": null,
        |            "icon": null,
        |            "parent": {
        |                "type": "database_id",
        |                "database_id": "16d408f9-1f00-407c-9840-8a789ee6c75d"
        |            },
        |            "archived": false,
        |            "properties": {
        |                "Status": {
        |                    "id": "BgrT",
        |                    "type": "select",
        |                    "select": {
        |                        "id": "434dd506-6217-403d-8338-15cbc32f7810",
        |                        "name": "Allow",
        |                        "color": "green"
        |                    }
        |                },
        |                "Description": {
        |                    "id": "EUfT",
        |                    "type": "rich_text",
        |                    "rich_text": []
        |                },
        |                "Person": {
        |                    "id": "GgLj",
        |                    "type": "people",
        |                    "people": [
        |                        {
        |                            "object": "user",
        |                            "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca",
        |                            "name": "Anton Panov",
        |                            "avatar_url": null,
        |                            "type": "person",
        |                            "person": {}
        |                        },
        |                        {
        |                            "object": "user",
        |                            "id": "0fbc8350-a578-4288-875d-c146edbfefc2",
        |                            "name": "Margarita",
        |                            "avatar_url": null,
        |                            "type": "person",
        |                            "person": {}
        |                        }
        |                    ]
        |                },
        |                "Status 1": {
        |                    "id": "N%60a%40",
        |                    "type": "status",
        |                    "status": {
        |                        "id": "1623fd32-3589-4eb8-8a7c-1da896c7afbb",
        |                        "name": "Not started",
        |                        "color": "default"
        |                    }
        |                },
        |                "Created time": {
        |                    "id": "TUo%40",
        |                    "type": "created_time",
        |                    "created_time": "2024-03-03T20:14:00.000Z"
        |                },
        |                "Новое поле": {
        |                    "id": "Ulla",
        |                    "type": "rich_text",
        |                    "rich_text": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "🥦Vegetable",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "🥦Vegetable",
        |                            "href": null
        |                        }
        |                    ]
        |                },
        |                "Group": {
        |                    "id": "Vm%3Ay",
        |                    "type": "rich_text",
        |                    "rich_text": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "asdf",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "asdf",
        |                            "href": null
        |                        }
        |                    ]
        |                },
        |                "Last edited time": {
        |                    "id": "dLFN",
        |                    "type": "last_edited_time",
        |                    "last_edited_time": "2024-03-06T19:10:00.000Z"
        |                },
        |                "Выбор без выбыра": {
        |                    "id": "mJGR",
        |                    "type": "select",
        |                    "select": {
        |                        "id": "274fadbc-a119-42cb-808f-9a22f30389eb",
        |                        "name": "🥦Vegetable",
        |                        "color": "purple"
        |                    }
        |                },
        |                "": {
        |                    "id": "mUt%40",
        |                    "type": "rich_text",
        |                    "rich_text": []
        |                },
        |                "Выбор без выбора": {
        |                    "id": "nBjn",
        |                    "type": "select",
        |                    "select": {
        |                        "id": "37344085-2560-40b0-8744-9bddc9adee68",
        |                        "name": "💪Protein",
        |                        "color": "yellow"
        |                    }
        |                },
        |                "Name": {
        |                    "id": "title",
        |                    "type": "title",
        |                    "title": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "Hello, page",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "Hello, page",
        |                            "href": null
        |                        }
        |                    ]
        |                }
        |            },
        |            "url": "https://www.notion.so/Hello-page-7981b2fd0c98409fbb110fe5a4e2a70c",
        |            "public_url": null
        |        },
        |        {
        |            "object": "page",
        |            "id": "7f439d03-d31d-40d1-b97b-9068aa473a2d",
        |            "created_time": "2024-03-03T21:47:00.000Z",
        |            "last_edited_time": "2024-03-05T17:16:00.000Z",
        |            "created_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "last_edited_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "cover": null,
        |            "icon": null,
        |            "parent": {
        |                "type": "database_id",
        |                "database_id": "16d408f9-1f00-407c-9840-8a789ee6c75d"
        |            },
        |            "archived": false,
        |            "properties": {
        |                "Status": {
        |                    "id": "BgrT",
        |                    "type": "select",
        |                    "select": null
        |                },
        |                "Description": {
        |                    "id": "EUfT",
        |                    "type": "rich_text",
        |                    "rich_text": []
        |                },
        |                "Person": {
        |                    "id": "GgLj",
        |                    "type": "people",
        |                    "people": []
        |                },
        |                "Status 1": {
        |                    "id": "N%60a%40",
        |                    "type": "status",
        |                    "status": {
        |                        "id": "1623fd32-3589-4eb8-8a7c-1da896c7afbb",
        |                        "name": "Not started",
        |                        "color": "default"
        |                    }
        |                },
        |                "Created time": {
        |                    "id": "TUo%40",
        |                    "type": "created_time",
        |                    "created_time": "2024-03-03T21:47:00.000Z"
        |                },
        |                "Новое поле": {
        |                    "id": "Ulla",
        |                    "type": "rich_text",
        |                    "rich_text": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "🍎Fruit",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "🍎Fruit",
        |                            "href": null
        |                        }
        |                    ]
        |                },
        |                "Group": {
        |                    "id": "Vm%3Ay",
        |                    "type": "rich_text",
        |                    "rich_text": []
        |                },
        |                "Last edited time": {
        |                    "id": "dLFN",
        |                    "type": "last_edited_time",
        |                    "last_edited_time": "2024-03-05T17:16:00.000Z"
        |                },
        |                "Выбор без выбыра": {
        |                    "id": "mJGR",
        |                    "type": "select",
        |                    "select": {
        |                        "id": "73091846-a067-4e99-be71-ae05dbb478e7",
        |                        "name": "🍎Fruit",
        |                        "color": "red"
        |                    }
        |                },
        |                "": {
        |                    "id": "mUt%40",
        |                    "type": "rich_text",
        |                    "rich_text": []
        |                },
        |                "Выбор без выбора": {
        |                    "id": "nBjn",
        |                    "type": "select",
        |                    "select": {
        |                        "id": "c5f3ae0d-eb94-4b48-96fb-41122d3f3dc9",
        |                        "name": "🥦Vegetable",
        |                        "color": "purple"
        |                    }
        |                },
        |                "Name": {
        |                    "id": "title",
        |                    "type": "title",
        |                    "title": []
        |                }
        |            },
        |            "url": "https://www.notion.so/7f439d03d31d40d1b97b9068aa473a2d",
        |            "public_url": null
        |        },
        |        {
        |            "object": "page",
        |            "id": "972830dc-30de-4fd0-bd48-860d5a01f220",
        |            "created_time": "2024-03-03T20:18:00.000Z",
        |            "last_edited_time": "2024-03-05T17:15:00.000Z",
        |            "created_by": {
        |                "object": "user",
        |                "id": "d5a0e559-30a9-4405-8f92-d9f30ebb3b89"
        |            },
        |            "last_edited_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "cover": null,
        |            "icon": null,
        |            "parent": {
        |                "type": "database_id",
        |                "database_id": "16d408f9-1f00-407c-9840-8a789ee6c75d"
        |            },
        |            "archived": false,
        |            "properties": {
        |                "Status": {
        |                    "id": "BgrT",
        |                    "type": "select",
        |                    "select": null
        |                },
        |                "Description": {
        |                    "id": "EUfT",
        |                    "type": "rich_text",
        |                    "rich_text": []
        |                },
        |                "Person": {
        |                    "id": "GgLj",
        |                    "type": "people",
        |                    "people": []
        |                },
        |                "Status 1": {
        |                    "id": "N%60a%40",
        |                    "type": "status",
        |                    "status": {
        |                        "id": "1623fd32-3589-4eb8-8a7c-1da896c7afbb",
        |                        "name": "Not started",
        |                        "color": "default"
        |                    }
        |                },
        |                "Created time": {
        |                    "id": "TUo%40",
        |                    "type": "created_time",
        |                    "created_time": "2024-03-03T20:18:00.000Z"
        |                },
        |                "Новое поле": {
        |                    "id": "Ulla",
        |                    "type": "rich_text",
        |                    "rich_text": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "💪Protein",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "💪Protein",
        |                            "href": null
        |                        }
        |                    ]
        |                },
        |                "Group": {
        |                    "id": "Vm%3Ay",
        |                    "type": "rich_text",
        |                    "rich_text": []
        |                },
        |                "Last edited time": {
        |                    "id": "dLFN",
        |                    "type": "last_edited_time",
        |                    "last_edited_time": "2024-03-05T17:15:00.000Z"
        |                },
        |                "Выбор без выбыра": {
        |                    "id": "mJGR",
        |                    "type": "select",
        |                    "select": null
        |                },
        |                "": {
        |                    "id": "mUt%40",
        |                    "type": "rich_text",
        |                    "rich_text": []
        |                },
        |                "Выбор без выбора": {
        |                    "id": "nBjn",
        |                    "type": "select",
        |                    "select": {
        |                        "id": "16614dc8-3f52-49e2-a57c-d76c7cae0710",
        |                        "name": "🍎Fruit",
        |                        "color": "red"
        |                    }
        |                },
        |                "Name": {
        |                    "id": "title",
        |                    "type": "title",
        |                    "title": []
        |                }
        |            },
        |            "url": "https://www.notion.so/972830dc30de4fd0bd48860d5a01f220",
        |            "public_url": null
        |        },
        |        {
        |            "object": "page",
        |            "id": "2db5e284-97f8-4322-8106-fd46f73b37e4",
        |            "created_time": "2024-03-03T16:22:00.000Z",
        |            "last_edited_time": "2024-03-03T20:14:00.000Z",
        |            "created_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "last_edited_by": {
        |                "object": "user",
        |                "id": "d5a0e559-30a9-4405-8f92-d9f30ebb3b89"
        |            },
        |            "cover": null,
        |            "icon": {
        |                "type": "external",
        |                "external": {
        |                    "url": "https://www.notion.so/icons/compass_lightgray.svg"
        |                }
        |            },
        |            "parent": {
        |                "type": "workspace",
        |                "workspace": true
        |            },
        |            "archived": false,
        |            "properties": {
        |                "title": {
        |                    "id": "title",
        |                    "type": "title",
        |                    "title": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "Teamspace Home",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "Teamspace Home",
        |                            "href": null
        |                        }
        |                    ]
        |                }
        |            },
        |            "url": "https://www.notion.so/Teamspace-Home-2db5e28497f843228106fd46f73b37e4",
        |            "public_url": null
        |        },
        |        {
        |            "object": "page",
        |            "id": "29da1d83-8878-489d-b681-5bde159011f2",
        |            "created_time": "2024-03-03T19:43:00.000Z",
        |            "last_edited_time": "2024-03-03T19:45:00.000Z",
        |            "created_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "last_edited_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "cover": null,
        |            "icon": null,
        |            "parent": {
        |                "type": "database_id",
        |                "database_id": "cd252ce3-0a41-49aa-afbf-a18aea6a901d"
        |            },
        |            "archived": false,
        |            "properties": {
        |                "Assign": {
        |                    "id": "A%7Do~",
        |                    "type": "people",
        |                    "people": []
        |                },
        |                "Date": {
        |                    "id": "QSDS",
        |                    "type": "date",
        |                    "date": null
        |                },
        |                "tag": {
        |                    "id": "ceUX",
        |                    "type": "rich_text",
        |                    "rich_text": []
        |                },
        |                "Status": {
        |                    "id": "fRyf",
        |                    "type": "status",
        |                    "status": {
        |                        "id": "98b6df6d-6e66-4cad-a83f-7a01981048a6",
        |                        "name": "Not started",
        |                        "color": "default"
        |                    }
        |                },
        |                "Name": {
        |                    "id": "title",
        |                    "type": "title",
        |                    "title": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "Card 2",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "Card 2",
        |                            "href": null
        |                        }
        |                    ]
        |                }
        |            },
        |            "url": "https://www.notion.so/Card-2-29da1d838878489db6815bde159011f2",
        |            "public_url": null
        |        },
        |        {
        |            "object": "page",
        |            "id": "199059c1-77b3-4f70-94a6-886ae0307671",
        |            "created_time": "2024-03-03T19:43:00.000Z",
        |            "last_edited_time": "2024-03-03T19:44:00.000Z",
        |            "created_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "last_edited_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "cover": null,
        |            "icon": null,
        |            "parent": {
        |                "type": "database_id",
        |                "database_id": "cd252ce3-0a41-49aa-afbf-a18aea6a901d"
        |            },
        |            "archived": false,
        |            "properties": {
        |                "Assign": {
        |                    "id": "A%7Do~",
        |                    "type": "people",
        |                    "people": []
        |                },
        |                "Date": {
        |                    "id": "QSDS",
        |                    "type": "date",
        |                    "date": null
        |                },
        |                "tag": {
        |                    "id": "ceUX",
        |                    "type": "rich_text",
        |                    "rich_text": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "abacaba",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "abacaba",
        |                            "href": null
        |                        }
        |                    ]
        |                },
        |                "Status": {
        |                    "id": "fRyf",
        |                    "type": "status",
        |                    "status": {
        |                        "id": "98b6df6d-6e66-4cad-a83f-7a01981048a6",
        |                        "name": "Not started",
        |                        "color": "default"
        |                    }
        |                },
        |                "Name": {
        |                    "id": "title",
        |                    "type": "title",
        |                    "title": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "Card 3",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "Card 3",
        |                            "href": null
        |                        }
        |                    ]
        |                }
        |            },
        |            "url": "https://www.notion.so/Card-3-199059c177b34f7094a6886ae0307671",
        |            "public_url": null
        |        },
        |        {
        |            "object": "page",
        |            "id": "3014c9d9-d1c0-4cd0-819f-ee300cf495e6",
        |            "created_time": "2023-12-16T18:50:00.000Z",
        |            "last_edited_time": "2024-03-03T16:21:00.000Z",
        |            "created_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "last_edited_by": {
        |                "object": "user",
        |                "id": "cc210ef7-473c-49fa-b648-e0b872ac73ca"
        |            },
        |            "cover": null,
        |            "icon": null,
        |            "parent": {
        |                "type": "workspace",
        |                "workspace": true
        |            },
        |            "archived": false,
        |            "properties": {
        |                "title": {
        |                    "id": "title",
        |                    "type": "title",
        |                    "title": [
        |                        {
        |                            "type": "text",
        |                            "text": {
        |                                "content": "Some other integration",
        |                                "link": null
        |                            },
        |                            "annotations": {
        |                                "bold": false,
        |                                "italic": false,
        |                                "strikethrough": false,
        |                                "underline": false,
        |                                "code": false,
        |                                "color": "default"
        |                            },
        |                            "plain_text": "Some other integration",
        |                            "href": null
        |                        }
        |                    ]
        |                }
        |            },
        |            "url": "https://www.notion.so/Some-other-integration-3014c9d9d1c04cd0819fee300cf495e6",
        |            "public_url": null
        |        }
        |    ],
        |    "next_cursor": null,
        |    "has_more": false,
        |    "type": "page_or_database",
        |    "page_or_database": {},
        |    "developer_survey": "https://notionup.typeform.com/to/bllBsoI4?utm_source=postman",
        |    "request_id": "050173f2-1d69-4848-97ba-c833ff387325"
        |}
        |""".stripMargin

    val defaultValue = PaginatedList(List.empty, None, hasMore = false)
    val res = decode[PaginatedList[PageResponse]](json)

    println(res)

    res.isRight shouldBe true: Unit
    val list = res.getOrElse(defaultValue)
    list shouldNot equal(defaultValue): Unit

    println(list)
  }
}
