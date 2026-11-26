package org.knightingal.flow1000.client.beans
data class AlbumConfig(
  val id: Long,
  val name: String,
  val sourcePath: String,
  val coverSection: Flow1000Section,
)
