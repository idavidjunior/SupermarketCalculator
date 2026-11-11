# Fluxo das quatro abas

```mermaid
flowchart TD
    A[MainActivity<br/>FrameLayout] --> B[Calculadora]
    A --> C[Despesas]
    A --> D[Listas salvas]
    A --> E[Configuracoes]
    C --> C1[Compras de Mercado]
    C --> C2[Despesas Financeiras]
    D --> D1[Lista de descricao e valor]
    D --> D2[Carrinho<br/>CartAdapter]
    D1 --> F[Arquivos JSON<br/>ExternalFilesDir]
    D2 --> F
    E --> G[SharedPreferences]
```

## Etapa 1. Navegação

A `MainActivity` monta todas as páginas no mesmo `FrameLayout` e troca a
visibilidade conforme a aba escolhida. Só uma página fica visível por vez.

## Etapa 2. Calculadora

A expressão é montada na tela e avaliada em memória. O histórico fica
disponível para a consulta.

## Etapa 3. Despesas

Compras de mercado e despesas financeiras têm arquivos separados. Cada
arquivo guarda uma lista de lançamentos com data, descrição, valor e
categoria.

## Etapa 4. Listas salvas

Uma lista nova nasce da tela de entrada, com descrição, valor e categoria. O
carrinho acumula os itens e o `CartAdapter` desenha a lista.

## Etapa 5. Persistência

As listas e os lançamentos vão para arquivos JSON na pasta externa do
aplicativo. As preferências ficam no `SharedPreferences`.
