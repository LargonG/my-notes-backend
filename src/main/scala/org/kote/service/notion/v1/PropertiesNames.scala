package org.kote.service.notion.v1

object PropertiesNames {
  private val prefix = "My Notes"

  val titlePropertyName = "title"
  val assignsPropertyName = s"$prefix Assigns"
  val groupPropertyName = s"$prefix Group"
  val statusPropertyName = s"$prefix Status"
  val filesPropertyName = s"$prefix Files"
}
