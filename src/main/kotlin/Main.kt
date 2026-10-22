package com.pcoder23

val movimentosPorData = Array(1000) {
    mutableListOf<Movimento>()

}

var balancoMinimo: Int = 0



fun processarComando(cmd: String) {
    val partes = cmd.trim().split(Regex("\\s+"))

    when (partes[0]) {
        "i" -> inserirMovimento(partes.drop(1))
        "l" -> listarMovimentos(partes.drop(1))
        "r" -> removerMovimento(partes.drop(1))
        "b" -> calcularBalanco(partes.drop(1))
        "m" -> alterarMinimo(partes.drop(1))
        "v" -> verificarOrcamento()
        "w" -> determinarDataComportavel(partes.drop(1))
        "exit" -> println("Comando exit: vamos implementar o encerramento da sessão mais tarde.")
        else -> println("Comando desconhecido: ${partes[0]}")
    }
}

fun inserirMovimento(args: List<String>) {

    if (args.size < 5) {
        println("Erro: Argumentos insuficientes. [ex: i 10000 0 30 r Salario]")
        return
    }

    if (args[0].toIntOrNull() == null) {
        println("Insira o montante de movimento [ex: i 10000]")
        return
    }

    if (args[1].toIntOrNull() == null) {
        println("Insira a data em que o movimento estara a ser realizado [ex: i 10000 0]")
        return
    }

    if (args[2].toIntOrNull() == null) {
        println("Insira a periodicidade do movimento [ex: i 10000 0 30]")
        return
    }

    if (args[3].isEmpty() || (args[3][0] != 'r' && args[3][0] != 'd')) {
        println("Insira o tipo de movimento: despesa (d) ou receita (r) [ex: i 10000 0 r/d]")
        return
    }

    if (args[4].isBlank()) {
        println("Insira a descricao do movimento [ex: i 10000 0 r/d descricao]")
        return
    }

    val montante = args[0].toInt()
    val dataInicial = args[1].toInt()
    val periodicidade = args[2].toInt()
    val tipo = args[3][0]
    val descricao = args[4]

    var data = dataInicial

    do {
        val movimento = Movimento(
            ordem = movimentosPorData[data].size,
            montante = montante,
            data = data,
            tipo = tipo,
            descricao = descricao
        )

        movimentosPorData[data].add(movimento)

        if (periodicidade == 0) {
            break
        }

        data += periodicidade

    } while (data <= 999)

    println("Movimento(s) inserido(s) com sucesso!")
}


fun listarMovimentos(args: List<String>) {
    val data = args[0].toInt()
    val movimentos = movimentosPorData[data]

    for (movimento in movimentos) {
        println(
            "${movimento.ordem} ${movimento.montante} ${movimento.tipo} ${movimento.descricao}"
        )
    }
}


fun removerMovimento(args: List<String>) {
    if (args.size < 2) {
        println("Uso: r <data> <ordem>")
        return
    }

    val data = args[0].toIntOrNull()
    val ordem = args[1].toIntOrNull()

    if (data == null || data !in 0..999) {
        println("Data inválida.")
        return
    }

    if (ordem == null || ordem < 0) {
        println("Ordem inválida.")
        return
    }

    val movimentos = movimentosPorData[data]

    val indice = movimentos.indexOfFirst {
        it.ordem == ordem
    }

    if (indice == -1) {
        println("Movimento não encontrado.")
        return
    }

    movimentos.removeAt(indice)

    for (i in movimentos.indices) {
        val movimento = movimentos[i]

        movimentos[i] = movimento.copy(ordem = i)
    }

    println("Movimento removido com sucesso!")
}



fun calcularBalanco(args: List<String>) {
    if (args.isEmpty()) {
        println("Uso: b <data>")
        return
    }

    val data = args[0].toIntOrNull()

    if (data == null || data !in 0..999) {
        println("Data inválida.")
        return
    }

    var balanco = 0

    for (dia in 0..data) {
        for (movimento in movimentosPorData[dia]) {
            when (movimento.tipo) {
                'r' -> balanco += movimento.montante
                'd' -> balanco -= movimento.montante
            }
        }
    }

    println(balanco)
}


fun alterarMinimo(args: List<String>) {
    if (args.isEmpty()) {
        println("Uso: m <valor mínimo>")
        return
    }

    val novoMinimo = args[0].toIntOrNull()

    if (novoMinimo == null) {
        println("Valor mínimo inválido.")
        return
    }

    balancoMinimo = novoMinimo
}


fun verificarOrcamento() {
    var balanco = 0

    for (data in 0..999) {
        for (movimento in movimentosPorData[data]) {
            when (movimento.tipo) {
                'r' -> balanco += movimento.montante
                'd' -> balanco -= movimento.montante
            }
        }

        if (balanco < balancoMinimo) {
            println("Invalido")
            return
        }
    }

    println("Valido")
}



fun determinarDataComportavel(args: List<String>) {
    if (args.size < 2) {
        println("Uso: w <dataInicial> <montanteDespesa>")
        return
    }

    val dataInicial = args[0].toIntOrNull()
    val montanteDespesa = args[1].toIntOrNull()

    if (dataInicial == null || dataInicial !in 0..999) {
        println("Data inicial inválida.")
        return
    }

    if (montanteDespesa == null || montanteDespesa < 0) {
        println("Montante da despesa inválido.")
        return
    }

    // 1. Calcular o balanço existente em cada dia
    val balancos = IntArray(1000)
    var balanco = 0

    for (dia in 0..999) {
        for (movimento in movimentosPorData[dia]) {
            when (movimento.tipo) {
                'r' -> balanco += movimento.montante
                'd' -> balanco -= movimento.montante
            }
        }

        balancos[dia] = balanco
    }

    // 2. Se o orçamento já é inválido, não existe solução
    if (balancos.any { it < balancoMinimo }) {
        println("Incomportavel")
        return
    }

    // 3. Calcular o menor balanço futuro para cada dia
    val menorBalancoFuturo = IntArray(1000)

    menorBalancoFuturo[999] = balancos[999]

    for (dia in 998 downTo 0) {
        menorBalancoFuturo[dia] = minOf(
            balancos[dia],
            menorBalancoFuturo[dia + 1]
        )
    }

    // 4. Procurar a primeira data em que a despesa é possível
    for (dia in dataInicial..999) {
        if (menorBalancoFuturo[dia] - montanteDespesa >= balancoMinimo) {
            println(dia)
            return
        }
    }

    println("Incomportavel")
}

fun main() {
    while (true) {
        print(">>> ")
        val cmd = readln()

        processarComando(cmd)
    }
}
