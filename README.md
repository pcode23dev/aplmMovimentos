# Movimentos — versão revista do Laboratório #1

## Estrutura
- `Main.kt`: leitura e encaminhamento dos comandos e gestão da sessão actual.
- `model/Movimento.kt`: entidade Movimento.
- `model/Utilizador.kt`: utilizador, dados de movimentos isolados por utilizador e balanço mínimo.
- `model/Sessao.kt`: token hash, data/hora de início e encerramento da sessão.
- `service/GestorUtilizadores.kt`: criação de utilizadores, autenticação e criação de sessões.
- `service/GestorMovimentos.kt`: operações do laboratório sobre os movimentos do utilizador autenticado.

## Comandos
- `u <email> <password>` — cria o utilizador se o email ainda não existir; caso contrário, autentica se a palavra-passe corresponder.
- `i <montante> <data> <periodicidade> <r|d> <descrição>` — insere receita (`r`) ou despesa (`d`).
- `l <data>` — lista os movimentos da data.
- `r <data> <ordem>` — remove o movimento.
- `b <data>` — mostra o balanço até à data.
- `m <valor>` — define o balanço mínimo.
- `v` — verifica o orçamento.
- `w <dataInicial> <montanteDespesa>` — procura a primeira data comportável.
- `exit` — encerra a sessão actual, sem terminar o programa.

Os comandos de movimentos só funcionam com uma sessão autenticada. Os dados dos movimentos e o balanço mínimo pertencem a cada utilizador e ficam isolados dos restantes utilizadores durante a execução do programa.

## Correcções incluídas
- Organização em pacotes `model` e `service`, com classes de domínio e serviços.
- Implementação da criação/autenticação de utilizadores e registo de sessões.
- Token de sessão representado por um hash SHA-256 único; registo de data/hora de início e fim.
- `exit` encerra a sessão, mas o ciclo principal continua activo.
- Remoção das mensagens de sucesso extra nos comandos `i` e `r`, para manter a saída procedural compatível com o exemplo do enunciado.
- Validação de argumentos e datas antes de aceder às listas.
- Descrições de movimentos podem conter espaços.
- O programa termina normalmente quando o fluxo de entrada termina (EOF).

## Validação feita
O código foi compilado com `kotlinc` e o exemplo procedural do enunciado foi executado após iniciar sessão com `u teste@exemplo.ao 123`. Os resultados dos comandos `i/l/r/b/m/v/w` coincidiram com a sequência de saída esperada do exemplo.
