package com.sample.demo

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File


class JsonParser {

    fun parse(): List<String> {



        val file = File("src/main/kotlin/com/sample/demo/zipcodes.json")

        val jsonString = file.inputStream().readBytes().toString(Charsets.UTF_8)

        val mapper = jacksonObjectMapper()

        val zips: List<ZipData> = mapper.readValue(jsonString, object : TypeReference<List<ZipData>>(){})

        return zips.map {
            it.zipcode
        }

    }
}