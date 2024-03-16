package org.kote.client.notion.db

import io.circe.syntax.EncoderOps
import org.kote.client.notion.model.database.DbSearchRequest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SearchBoardSpec extends AnyFlatSpec with Matchers {
  it should "encode" in {
    val request = DbSearchRequest(None, None)

    println(s"\"${request.asJson.toString()}\"")
  }
}
