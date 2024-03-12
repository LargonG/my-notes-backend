package org.kote.service.notion.v1

case class PropertyId(key: String, name: String, value: String) {
  override def toString: String = s"$key-$name-$value"
}
