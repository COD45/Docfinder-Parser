package com.sample.demo

import com.sample.demo.database.DoctorsRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ServParsApplication

fun main(args: Array<String>) {
	runApplication<ServParsApplication>(*args)
}

val parsingManager = ParsingManager()

@RestController
class MessageController(repository: DoctorsRepository) {

	val checkToken = "DDoSisCoMiNg"

	init {
	    parsingManager.setRepo(repository)
	}

	@GetMapping("/")
	fun index() = "Hello!"

	@GetMapping("/parse")
	fun parse(@RequestParam("token") token: String?): String {
		if (token == checkToken) {
			parsingManager.downloadData()

			return "DONE"
		} else {
			return "ERROR"
		}
	}

	@GetMapping("/search")
	fun search(@RequestParam("token") token: String?, zip: String?): String {
		if (token == checkToken) {
			val result = parsingManager.getByZip(zip)

			return "FOUNG ${result.size} ENTRIES"
		} else {
			return "ERROR"
		}
	}
}
