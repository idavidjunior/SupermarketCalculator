# ADR 0001: Uma Activity só com páginas em FrameLayout

## Status

Aceito.

## Contexto

O aplicativo tem quatro áreas de trabalho: calculadora, despesas, listas
salvas e configurações. Cada área é um conjunto de campos e listas. Com
AndroidX e Fragmentos, isso viraria várias classes e dependências.

## Decisão

Manter uma única `MainActivity`. As páginas são layouts XML inflados dentro
de um `FrameLayout`, e a troca de aba acontece por `View.VISIBLE` e
`View.GONE`.

## Consequências

- Sem AndroidX e sem Fragmentos, o APK fica menor.
- Todo o estado fica no mesmo contexto da Activity, o que simplifica a lógica.
- A `MainActivity` concentra muita responsabilidade, por isso a regra do
  projeto é manter as classes de apoio em `adapter/` e `models/`.
- A troca de aba recria a visão, então o estado das páginas é salvo em
  `SharedPreferences` e nos arquivos JSON.
