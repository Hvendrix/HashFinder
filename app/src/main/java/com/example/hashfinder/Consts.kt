package com.example.hashfinder

import java.security.SecureRandom


object Consts {
    private val numList = (0..9).toMutableList()
    val acceptableValues = numList.toMutableListOfString()
    val rand = SecureRandom()

    init {
        acceptableValues.addAll(mutableListOf("A", "B", "C", "D", "E", "F"))
    }
}