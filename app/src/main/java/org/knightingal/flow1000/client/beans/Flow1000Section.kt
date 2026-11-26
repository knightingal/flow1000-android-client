package org.knightingal.flow1000.client.beans

data class Flow1000Section(
  val id: Long,
  val name: String,
  val dirName: String,
  val cover: String,
  val album: String,
  val coverWidth: Int,
  val coverHeight: Int,
)