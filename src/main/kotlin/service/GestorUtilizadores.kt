package com.pcoder23.service

import com.pcoder23.model.Sessao
import com.pcoder23.model.Utilizador
import java.security.MessageDigest
import java.util.UUID

enum class ResultadoAutenticacao { UTILIZADOR_CRIADO, LOGIN_SUCESSO, PASSWORD_INCORRECTA }

data class AutenticacaoResult(val utilizador: Utilizador?, val resultado: ResultadoAutenticacao)

class GestorUtilizadores {
    private val utilizadores = linkedMapOf<String, Utilizador>()

    fun autenticarOuRegistar(email: String, password: String): AutenticacaoResult {
        val existente = utilizadores[email]
        if (existente != null) {
            if (!existente.verificarPassword(password)) {
                return AutenticacaoResult(null, ResultadoAutenticacao.PASSWORD_INCORRECTA)
            }
            criarSessao(existente)
            return AutenticacaoResult(existente, ResultadoAutenticacao.LOGIN_SUCESSO)
        }

        val novo = Utilizador(email, password)
        utilizadores[email] = novo
        criarSessao(novo)
        return AutenticacaoResult(novo, ResultadoAutenticacao.UTILIZADOR_CRIADO)
    }

    private fun criarSessao(utilizador: Utilizador) {
        val token = UUID.randomUUID().toString()
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(token.toByteArray(Charsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte) }
        utilizador.sessoes.add(Sessao(hash))
    }
}
