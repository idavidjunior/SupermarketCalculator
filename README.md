# Supermercado Caixa

Aplicativo Android para calcular compras de mercado e controlar despesas
financeiras, com listas salvas, carrinho por loja e exportação de dados.

## O que o aplicativo faz

- Calculadora com histórico de expressões.
- Registro de compras de mercado por arquivo, com totalização.
- Controle de despesas financeiras por categoria.
- Listas salvas de compras, com carrinho e divisão por loja.
- Exportação dos dados em arquivo.

## Stack

- Java 8, Android API 36.
- SDK puro: sem AndroidX, sem Gradle e sem Kotlin.
- Build por script: `aapt`, `javac`, `jar`, `d8`, `zipalign` e `apksigner`.
- Interface em XML com uma Activity só e páginas dentro de um `FrameLayout`.
- Estado em memória, `SharedPreferences` e arquivos JSON na pasta externa do
  aplicativo.

## Estrutura

| Pasta | Responsabilidade |
|-------|------------------|
| `res/` | Layouts, drawables, strings, cores e estilos |
| `src/com/supermarket/calculator/` | Código Java da Activity principal |
| `src/com/supermarket/calculator/adapter/` | Adapter do carrinho |
| `src/com/supermarket/calculator/models/` | Modelo dos itens do carrinho |
| `docs/architecture/ARQUITETURA.md` | Mapa das quatro abas e do fluxo de dados |
| `docs/diagrams/` | Diagramas das telas e do cálculo |
| `docs/adr/` | Decisões registradas |
| `tests/` | Verificação automatizada da estrutura |

## Abas

1. Calculadora.
2. Despesas, com as sub-abas Compras de Mercado e Despesas Financeiras.
3. Listas salvas, com o carrinho.
4. Configurações.

## Build

```powershell
.\build.ps1
```

## Verificação

```powershell
python tests/verificar_integridade.py
```

## Documentação

- Arquitetura: `docs/architecture/ARQUITETURA.md`
- Metodologia de build: `METODOLOGIA.md`
- Playbook de manutenção: `PLAYBOOK.md`
- Publicação: `RELEASE.md`
