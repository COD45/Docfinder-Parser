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
val parser = JsonParser()

@RestController
class MessageController(repository: DoctorsRepository) {

	init {
	    parsingManager.setRepo(repository)
	}

	@GetMapping("/")
	fun index() = "Hello!"

	@GetMapping("/parse")
	fun parse(@RequestParam("name") name: String?): String {

		parsingManager.downloadData()

		return "DONE"
	}
}
