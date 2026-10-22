package com.pcoder23.model

class Utilizador(val email: String, password: String) {
    private val passwordGuardada = password

    internal val movimentosPorData: Array<MutableList<Movimento>> =
        Array(1000) { mutableListOf() }

    var balancoMinimo: Int = 0
        internal set

    val sessoes: MutableList<Sessao> = mutableListOf()

    fun verificarPassword(password: String): Boolean =
        passwordGuardada == password
}
