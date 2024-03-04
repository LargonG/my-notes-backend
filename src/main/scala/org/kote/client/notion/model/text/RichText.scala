package org.kote.client.notion.model.text

sealed trait RichText

final case class Text(
    plainText: String,
) extends RichText
