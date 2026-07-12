# Conta Corrente - Desafio Técnico

API REST simples de conta corrente: abertura, depósito, saque, transferência e extrato.

## Stack

Java 8, Quarkus 1.13.7 (última versão da série 1.x compatível com Java 8), PostgreSQL, Hibernate/Panache.

Escolhi Java 8 de propósito pra simular um cenário legado.

## Rodando

docker compose -f docker-compose/docker-compose.yml up -d
./mvnw compile quarkus:dev

API sobe em localhost:8080.

## Endpoints

- POST /contas/abrir — abre conta { "documento": "12345678900" }
- POST /contas/deposito — { "numeroConta": 167665, "valor": 100.00 }
- POST /contas/saque — { "numeroConta": 167665, "valor": 30.00 }
- POST /contas/transferencia — { "numeroContaOrigem": 167665, "numeroContaDestino": 583921, "valor": 20.00 }
- GET /contas/{numero}/extrato?inicio=2026-07-01&fim=2026-07-12&pagina=0&tamanhoPagina=20

## Decisões técnicas

Concorrência: uso lock pessimista (SELECT FOR UPDATE) em vez de otimista. Numa conta corrente é comum ter várias operações simultâneas na mesma conta, e lock otimista geraria muito retry sob carga. Pessimista simplesmente serializa o acesso àquela linha.

Transferência: pra evitar deadlock quando duas transferências acontecem em sentidos opostos ao mesmo tempo, sempre travo as contas na mesma ordem (menor número primeiro), independente de origem/destino.

Saldo: guardado como campo agregado na conta (não recalculado via SUM toda vez), atualizado dentro da mesma transação que grava a movimentação.

Movimentação é imutável — só INSERT, nunca UPDATE. Serve como trilha de auditoria.

Rollback: tudo roda dentro de @Transactional, então qualquer erro no meio do caminho desfaz automaticamente o que já tinha sido feito.