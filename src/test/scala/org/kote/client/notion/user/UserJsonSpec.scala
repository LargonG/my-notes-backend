package org.kote.client.notion.user

import io.circe.syntax.EncoderOps
import org.kote.client.notion.model.user.{UserId, UserRequest, UserResponse}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.circe.parser._

import java.util.UUID

class UserJsonSpec extends AnyFlatSpec with Matchers with EitherValues {
  it should "encode user request" in {
    val uuid = UUID.fromString("d9962881-f78f-4c18-a2c2-0b3abbdcf5ed")
    UserRequest(
      UserId(uuid),
    ).asJson.toString shouldEqual
      s"""{
         |  "object" : "user",
         |  "id" : "$uuid"
         |}""".stripMargin
  }

  it should "decode full user response" in {
    val uuid = UUID.fromString("35e822a7-bb3f-4da5-bbbc-a9c6498bdcf7")
    val name = "Anton Panov"
    val notEquals = UserResponse(UserId(uuid), None)

    val json =
      s"""
         |{
         |  "object": "user",
         |  "id": "$uuid",
         |  "name": "Anton Panov",
         |  "avatar_url": null,
         |  "type": "person",
         |  "person": {}
         |}
        |""".stripMargin

    decode[UserResponse](json).getOrElse(notEquals) shouldBe UserResponse(
      UserId(uuid),
      Some(name),
    )
  }

  it should "decode short user response" in {
    val uuid = UUID.fromString("35e822a7-bb3f-4da5-bbbc-a9c6498bdcf7")
    val notEquals = UserResponse(
      UserId(UUID.fromString("2e5c1727-e24f-4902-8d8b-32022be8df51")),
      Some("Not equals"),
    )

    val json =
      s"""
         |{
         |  "object": "user",
         |  "id": "$uuid"
         |}
         |""".stripMargin

    decode[UserResponse](json).getOrElse(notEquals) shouldBe UserResponse(
      UserId(uuid),
      None,
    )
  }
}
