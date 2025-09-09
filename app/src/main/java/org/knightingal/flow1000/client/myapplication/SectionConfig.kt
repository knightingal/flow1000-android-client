package org.knightingal.flow1000.client.myapplication

class SectionConfig(var name: String, var encrypted: Boolean, var baseUrl: String)

val SECTION_CONFIG_MAP: Map<String, SectionConfig> = initSectionConfig()

fun initSectionConfig(): Map<String, SectionConfig> {
    // TODO: so joke, the album config is hard coded here!!!
    return mapOf(
        "flow1000" to SectionConfig("flow1000", true, "encrypted"),
        "ship" to SectionConfig("ship", true, "encrypted"),
        "1803" to SectionConfig("1803", false, "1803"),
        "1804" to SectionConfig("1804", false, "1804"),
        "1805" to SectionConfig("1805", false, "1805"),
        "1806" to SectionConfig("1806", false, "1806"),
        "1807" to SectionConfig("1807", false, "1807"),
    )
}

fun getSectionConfig(name: String?) : SectionConfig {

    return SECTION_CONFIG_MAP.getOrDefault(name, SectionConfig("flow1000", true, "encrypted"))
}