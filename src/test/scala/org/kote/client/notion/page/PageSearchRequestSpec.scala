package org.kote.client.notion.page

import io.circe.syntax.EncoderOps
import org.kote.client.notion.model.page.PageSearchRequest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PageSearchRequestSpec extends AnyFlatSpec with Matchers {
  it should "create search json" in {
    val search = PageSearchRequest(None, None)

    println(search.asJson.toString())
  }
}
