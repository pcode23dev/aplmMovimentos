package com.pcoder23.service

import com.pcoder23.model.Movimento
import com.pcoder23.model.Utilizador

class GestorMovimentos(private val utilizador: Utilizador) {
    fun inserir(montante: Int, dataInicial: Int, periodicidade: Int, tipo: Char, descricao: String): String? {
        if (montante < 0) return "Montante inválido."
        if (dataInicial !in 0..999) return "Data inválida."
        if (periodicidade < 0) return "Periodicidade inválida."
        if (tipo != 'r' && tipo != 'd') return "Tipo de movimento inválido."
        if (descricao.isBlank()) return "Descrição inválida."

        var data = dataInicial
        do {
            val movimentos = utilizador.movimentosPorData[data]
            movimentos.add(
                Movimento(
                    ordem = movimentos.size,
                    montante = montante,
                    data = data,
                    tipo = tipo,
                    descricao = descricao.trim()
                )
            )
            if (periodicidade == 0) break
            data += periodicidade
        } while (data <= 999)
        return null
    }

    fun listar(data: Int): List<Movimento>? {
        if (data !in 0..999) return null
        return utilizador.movimentosPorData[data].toList()
    }

    fun remover(data: Int, ordem: Int): Boolean {
        if (data !in 0..999 || ordem < 0) return false
        val movimentos = utilizador.movimentosPorData[data]
        val indice = movimentos.indexOfFirst { it.ordem == ordem }
        if (indice == -1) return false
        movimentos.removeAt(indice)
        for (i in movimentos.indices) movimentos[i] = movimentos[i].copy(ordem = i)
        return true
    }

    fun calcularBalanco(data: Int): Int? {
        if (data !in 0..999) return null
        var balanco = 0
        for (dia in 0..data) {
            for (movimento in utilizador.movimentosPorData[dia]) {
                balanco += if (movimento.tipo == 'r') movimento.montante else -movimento.montante
            }
        }
        return balanco
    }

    fun alterarMinimo(valor: Int) {
        utilizador.balancoMinimo = valor
    }

    fun verificarOrcamento(): Boolean {
        var balanco = 0
        for (dia in 0..999) {
            for (movimento in utilizador.movimentosPorData[dia]) {
                balanco += if (movimento.tipo == 'r') movimento.montante else -movimento.montante
            }
            if (balanco < utilizador.balancoMinimo) return false
        }
        return true
    }

    fun determinarDataComportavel(dataInicial: Int, montanteDespesa: Int): Int? {
        if (dataInicial !in 0..999 || montanteDespesa < 0) return null

        val balancos = IntArray(1000)
        var balanco = 0
        for (dia in 0..999) {
            for (movimento in utilizador.movimentosPorData[dia]) {
                balanco += if (movimento.tipo == 'r') movimento.montante else -movimento.montante
            }
            balancos[dia] = balanco
        }

        if (balancos.any { it < utilizador.balancoMinimo }) return -1

        val menorBalancoFuturo = IntArray(1000)
        menorBalancoFuturo[999] = balancos[999]
        for (dia in 998 downTo 0) {
            menorBalancoFuturo[dia] = minOf(balancos[dia], menorBalancoFuturo[dia + 1])
        }

        for (dia in dataInicial..999) {
            if (menorBalancoFuturo[dia] - montanteDespesa >= utilizador.balancoMinimo) return dia
        }
        return -1
    }
}
