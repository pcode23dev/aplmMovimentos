package com.pcoder23.model

data class Movimento(
    val ordem: Int,
    val montante: Int,
    val data: Int,
    val tipo: Char,
    val descricao: String
)
