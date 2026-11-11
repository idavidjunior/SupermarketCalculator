# ADR 0002: Build por script, sem Gradle

## Status

Aceito.

## Contexto

O projeto precisa ser compilado em máquinas com peu espaço em disco e sem a
cadeia de ferramentas do Android Studio. O Gradle e o AndroidX traziam um
peso e um consumo de recursos que não compensavam para um aplicativo de
quatro abas.

## Decisão

Compilar com `aapt`, `javac`, `jar`, `d8`, `zipalign` e `apksigner`, com o
`build.ps1` orquestrando as etapas. Sem Gradle, sem AndroidX e sem Kotlin.

## Consequências

- O build depende de JDK e Android SDK no `PATH`, com `ANDROID_HOME` configurado.
- Não há biblioteca de apoio para UI, XML ou injeção de dependência.
- O passo a passo completo do build está em `METODOLOGIA.md` e vale para
  qualquer outro aplicativo Android com a mesma restrição.
