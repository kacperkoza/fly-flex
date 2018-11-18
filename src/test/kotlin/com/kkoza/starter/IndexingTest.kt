package com.kkoza.starter

data class Pair(
        val firstIndexPair: indexPair,
        val secondIndexPair: indexPair
)

data class indexPair(
        val firstIndex: Int,
        val secondIndex: Int
)