package org.kote.common.tethys

import tethys.{JsonReader, JsonWriter}

import java.net.{URI, URL}
import java.time.Instant
import java.util.UUID

trait TethysInstances {
  implicit val instantReader: JsonReader[Instant] = JsonReader[String].map(Instant.parse)
  implicit val instantWriter: JsonWriter[Instant] = JsonWriter[String].contramap(_.toString)

  implicit val uuidReader: JsonReader[UUID] = JsonReader[String].map(UUID.fromString)
  implicit val uuidWriter: JsonWriter[UUID] = JsonWriter[String].contramap(_.toString)

  implicit val urlReader: JsonReader[URL] = JsonReader[String].map(URI.create(_).toURL)
  implicit val urlWriter: JsonWriter[URL] = JsonWriter[String].contramap(_.toString)
}
