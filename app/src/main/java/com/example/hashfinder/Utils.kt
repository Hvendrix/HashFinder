package com.example.hashfinder


// Для удобного перевода из списка чисел в список строк
fun MutableList<Int>.toMutableListOfString(): MutableList<String> {
    return map {
        it.toString()
    }.toMutableList()
}