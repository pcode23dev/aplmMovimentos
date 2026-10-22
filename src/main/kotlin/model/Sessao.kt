package com.pcoder23.model

import java.time.LocalDateTime

class Sessao(val tokenHash: String, val dataHoraInicio: LocalDateTime = LocalDateTime.now()) {
    var dataHoraFim: LocalDateTime? = null
        private set

    val ativa: Boolean
        get() = dataHoraFim == null

    fun encerrar() {
        if (ativa) dataHoraFim = LocalDateTime.now()
    }
}
