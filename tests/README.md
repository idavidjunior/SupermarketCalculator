# Testes do SupermarketCalculator

Verificação por script Python padrão, sem dependências externas.

## Executar

```powershell
python tests/verificar_integridade.py
```

O script sai com código 1 quando alguma verificação falha.

## O que é verificado

- Presença do código em `src/com/supermarket/calculator`, com a Activity
  principal, a Splash, o adapter do carrinho e o modelo de item.
- Presença dos recursos de layout, strings e cores.
- Pacote e Activity principal declarados no manifesto.
- Presença das quatro páginas na `MainActivity`.
- Existência do `build.ps1` e da documentação de arquitetura.
