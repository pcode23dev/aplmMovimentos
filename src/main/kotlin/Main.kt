package com.pcoder23

import com.pcoder23.model.Sessao
import com.pcoder23.model.Utilizador
import com.pcoder23.service.GestorMovimentos
import com.pcoder23.service.GestorUtilizadores
import com.pcoder23.service.ResultadoAutenticacao

fun main() {
    val gestorUtilizadores = GestorUtilizadores()
    var utilizadorActual: Utilizador? = null
    var sessaoActual: Sessao? = null

    while (true) {
        val linha = readLine() ?: break
        if (linha.isBlank()) continue

        val partes = linha.trim().split(Regex("\\s+"), limit = 6)
        val comando = partes[0]
        val args = partes.drop(1)

        if (comando == "u") {
            // Uma nova tentativa de autenticação termina a sessão actualmente aberta.
            sessaoActual?.encerrar()
            sessaoActual = null
            utilizadorActual = null

            if (args.size != 2 || args[0].isBlank() || args[1].isBlank()) {
                println("Uso: u <email> <password>")
                continue
            }
            val resultado = gestorUtilizadores.autenticarOuRegistar(args[0], args[1])
            when (resultado.resultado) {
                ResultadoAutenticacao.UTILIZADOR_CRIADO -> {
                    utilizadorActual = resultado.utilizador
                    sessaoActual = utilizadorActual?.sessoes?.lastOrNull()
                }
                ResultadoAutenticacao.LOGIN_SUCESSO -> {
                    utilizadorActual = resultado.utilizador
                    sessaoActual = utilizadorActual?.sessoes?.lastOrNull()
                }
                ResultadoAutenticacao.PASSWORD_INCORRECTA -> {
                    println("Credenciais inválidas.")
                    continue
                }
            }
            continue
        }

        if (comando == "exit") {
            sessaoActual?.encerrar()
            sessaoActual = null
            utilizadorActual = null
            continue
        }

        val utilizador = utilizadorActual
        val sessao = sessaoActual
        if (utilizador == null || sessao == null || !sessao.ativa) {
            println("É necessário iniciar sessão com: u <email> <password>")
            continue
        }

        val gestor = GestorMovimentos(utilizador)
        when (comando) {
            "i" -> {
                if (args.size < 5) {
                    println("Uso: i <montante> <data> <periodicidade> <r|d> <descricao>")
                    continue
                }
                val montante = args[0].toIntOrNull()
                val data = args[1].toIntOrNull()
                val periodicidade = args[2].toIntOrNull()
                val tipo = args[3].singleOrNull()
                val descricao = args[4]
                if (montante == null || data == null || periodicidade == null || tipo == null) {
                    println("Argumentos inválidos.")
                    continue
                }
                val erro = gestor.inserir(montante, data, periodicidade, tipo, descricao)
                if (erro != null) println(erro)
            }
            "l" -> {
                val data = args.firstOrNull()?.toIntOrNull()
                if (data == null || data !in 0..999) {
                    println("Data inválida.")
                    continue
                }
                gestor.listar(data)?.forEach {
                    println("${it.ordem} ${it.montante} ${it.tipo} ${it.descricao}")
                }
            }
            "r" -> {
                val data = args.getOrNull(0)?.toIntOrNull()
                val ordem = args.getOrNull(1)?.toIntOrNull()
                if (data == null || ordem == null) {
                    println("Uso: r <data> <ordem>")
                    continue
                }
                if (!gestor.remover(data, ordem)) println("Movimento não encontrado.")
            }
            "b" -> {
                val data = args.firstOrNull()?.toIntOrNull()
                val balanco = if (data != null) gestor.calcularBalanco(data) else null
                if (balanco == null) println("Data inválida.") else println(balanco)
            }
            "m" -> {
                val minimo = args.firstOrNull()?.toIntOrNull()
                if (minimo == null) println("Valor mínimo inválido.")
                else gestor.alterarMinimo(minimo)
            }
            "v" -> println(if (gestor.verificarOrcamento()) "Valido" else "Invalido")
            "w" -> {
                val data = args.getOrNull(0)?.toIntOrNull()
                val montante = args.getOrNull(1)?.toIntOrNull()
                if (data == null || data !in 0..999 || montante == null || montante < 0) {
                    println("Argumentos inválidos.")
                    continue
                }
                val resultado = gestor.determinarDataComportavel(data, montante)
                if (resultado == null || resultado == -1) println("Incomportavel") else println(resultado)
            }
            else -> println("Comando desconhecido: $comando")
        }
    }
}
