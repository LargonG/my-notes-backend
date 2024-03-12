package org.kote.client.notion

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.kote.client.notion.{ClientUtils, NotionUserClient}
import org.scalatest
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class NotionUserClientSpec
  extends AsyncFreeSpec
    with AsyncIOSpec
    with Matchers
    with OrderServiceImplSpecUtils {

  "users" - {
    "list" in {
      client.list
        .asserting(_ shouldNot be(List.empty)).value.map(_.getOrElse(scalatest.Assertions.fail()))
    }
  }
}

trait OrderServiceImplSpecUtils extends ClientUtils {
  val client: NotionUserClient[IO] = NotionUserClient.http[IO](backend, notionConfig)
}
