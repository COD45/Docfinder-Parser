package com.sample.demo

import java.io.File

fun appendLog(message: String) {
    println(message)
    File("app.log").appendText("$message\n")
}