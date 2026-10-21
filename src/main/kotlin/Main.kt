package com.pcoder23


data class Movimento(
    val montante: Int, val data: Int, val descricao: String, val tipo: Tipo, val ordem: Long
)

enum class Tipo {
    RECEITA, DESPESA
}

class Utilizador {

    val movimentosPorData: MutableMap<Int, MutableList<Movimento>> = mutableMapOf()

    var valorMinimo: Int = 0
    var contadorOrdem: Long = 0

    // Insere um movimento único ou vários movimentos periódicos.
    fun inserir(montante: Int, data: Int, periodicidade: Int, tipo: Tipo, descricao: String) {
        var dataAtual = data

        while (true) {
            val lista = movimentosPorData.getOrPut(dataAtual) {
                mutableListOf()
            }

            if (lista.size < 100) {
                lista.add(
                    Movimento(
                        montante = montante, data = dataAtual, descricao = descricao, tipo = tipo, ordem = contadorOrdem++
                    )
                )
            }

            if (periodicidade == 0 || dataAtual + periodicidade > 999) {
                break
            }

            dataAtual += periodicidade
        }
    }

    // Lista os movimentos de uma data pela ordem de inserção.
    fun listar(data: Int) {
        val lista = movimentosPorData[data] ?: emptyList()

        lista.forEachIndexed { indice, movimento ->
            val tipo = if (movimento.tipo == Tipo.RECEITA) 'r' else 'd'

            println(
                "$indice ${movimento.montante} $tipo ${movimento.descricao}"
            )
        }
    }

    // Remove um movimento pelo índice apresentado na listagem.
    fun remover(data: Int, indice: Int) {
        val lista = movimentosPorData[data] ?: return

        if (indice in lista.indices) {
            lista.removeAt(indice)

            if (lista.isEmpty()) {
                movimentosPorData.remove(data)
            }
        }
    }

    // Calcula o balanço acumulado de todas as datas.
    fun arrayBalancos(): IntArray {
        val balancos = IntArray(1000)
        var balanco = 0

        for (data in 0..999) {
            movimentosPorData[data]?.forEach { movimento ->
                balanco += if (movimento.tipo == Tipo.RECEITA) {
                    movimento.montante
                } else {
                    -movimento.montante
                }
            }

            balancos[data] = balanco
        }

        return balancos
    }

    // Apresenta o balanço acumulado até à data indicada.
    fun calcularBalanco(data: Int): Int {
        return arrayBalancos()[data]
    }

    // Altera o balanço mínimo permitido.
    fun alterarValorMinimo(novoValor: Int) {
        valorMinimo = novoValor
    }

    // Verifica se o balanço respeita o mínimo em todas as datas.
    fun orcamentoValido(): Boolean {
        return arrayBalancos().all { it >= valorMinimo }
    }

    // Procura a primeira data em que a despesa não invalida
    // o orçamento, considerando todas as datas até 999.
    fun primeiraDataComportavel(dataInicial: Int, despesa: Int): Int? {
        val balancos = arrayBalancos()

        for (data in dataInicial..999) {
            var valido = true

            for (dataVerificada in 0..999) {
                val balancoDepoisDaDespesa = if (dataVerificada >= data) {
                    balancos[dataVerificada] - despesa
                } else {
                    balancos[dataVerificada]
                }

                if (balancoDepoisDaDespesa < valorMinimo) {
                    valido = false
                    break
                }
            }

            if (valido) {
                return data
            }
        }

        return null
    }
}

fun processarComando(linha: String, utilizador: Utilizador) {
    val partes = linha.trim().split(Regex("\\s+"))

    if (partes.isEmpty() || partes[0].isEmpty()) return

    when (partes[0]) {
        "i" -> {
            val montante = partes[1].toInt()
            val data = partes[2].toInt()
            val periodicidade = partes[3].toInt()

            val tipo = when (partes[4]) {
                "r" -> Tipo.RECEITA
                else -> Tipo.DESPESA
            }

            val descricao = partes[5]

            utilizador.inserir(
                montante, data, periodicidade, tipo, descricao
            )
        }

        "l" -> {
            val data = partes[1].toInt()
            utilizador.listar(data)
        }

        "r" -> {
            val data = partes[1].toInt()
            val indice = partes[2].toInt()

            utilizador.remover(data, indice)
        }

        "b" -> {
            val data = partes[1].toInt()
            println(utilizador.calcularBalanco(data))
        }

        "m" -> {
            val novoValor = partes[1].toInt()
            utilizador.alterarValorMinimo(novoValor)
        }

        "v" -> {
            println(
                if (utilizador.orcamentoValido()) {
                    "Valido"
                } else {
                    "Invalido"
                }
            )
        }

        "w" -> {
            val dataInicial = partes[1].toInt()
            val despesa = partes[2].toInt()

            val resultado = utilizador.primeiraDataComportavel(
                dataInicial, despesa
            )

            println(resultado ?: "Incomportavel")
        }
    }
}

fun main() {
    val utilizador = Utilizador()

    while (true) {
        print(">>> ")
        val linha = readlnOrNull() ?: break

        if (linha.isBlank()) continue

        processarComando(linha, utilizador)
    }
}
