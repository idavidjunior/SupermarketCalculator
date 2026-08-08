# SupermarketCalculator — Guia de Build e Release (v1.4.0)

> Fonte de verdade operacional para compilar, versionar, assinar e instalar o app.
> Atualizado em 2026-08-08.

## Stack

- **Plataforma:** Android SDK puro (sem Gradle, sem AndroidX, sem Kotlin)
- **Build:** `build.ps1` (aapt + javac + jar + d8 + zipalign + apksigner)
- **minSdk 21 / targetSdk 36** (compile com a plataforma instalada na máquina)
- **JAVA:** JDK na PATH (javac, jar, keytool)

## Como compilar

| Tipo | Comando | Saída |
|------|---------|-------|
| Debug | `.\build.ps1` | `SupermarketCalculator-debug.apk` |
| Release | `.\build.ps1 -Release -OutputName "SupermarketCalculator-vX.Y.Z"` | `SupermarketCalculator-vX.Y.Z-release.apk` |

O `build.ps1` **auto-detecta** o build-tools e a plataforma mais recentes instalados
(ex.: `build-tools/35.0.0`, `platforms/android-35`). Suporta espaços no caminho do projeto
(argfile relativo). Sempre gera o `classes.dex` na raiz do APK.

## Versionamento

- `versionCode` e `versionName` ficam em `AndroidManifest.xml` (linhas 4-5).
- **Sempre incrementar `versionCode` em +1** a cada build novo (Play Store e atualização por cima exigem).
- Convenção `versionName`: `1.3.0` (semantic versioning).

Histórico de versões:

| Versão | versionCode | Data | O que mudou |
|--------|-------------|------|-------------|
| 1.2.0 | 3 | 2026-07-31 | Versão anterior (assinatura diferente) |
| 1.3.0 | 4 | 2026-08-08 | Persistência do carrinho ao bloquear tela + build.ps1 resiliente |
| 1.4.0 | 5 | 2026-08-08 | Som no botão Adicionar (3 opções: caixa registradora, pop, bip) |

## Assinatura (release keystore)

**⚠️ CRÍTICO — guarde a keystore com segurança.** Sem ela, nenhuma atualização futura
poderá ser instalada por cima da v1.3.0 (o Android exige a MESMA assinatura; trocar = desinstalar = perda de dados).

- **Arquivo:** `release.keystore` (na raiz do projeto)
- **Alias:** `supermarket`
- **StorePass / KeyPass:** `opencode`
- **DN:** `CN=Supermarket Calculator, OU=UmGrau, O=EcoSystemUmGrau, L=Belem, ST=Para, C=BR`
- **Fingerprint SHA-256:** `20b1693cd2ccdbe62294e83bb990599e77762618a774842850f0e73e29026562`

Verificar assinatura:
```powershell
$BT = (Get-ChildItem "$env:ANDROID_HOME\build-tools" -Directory | Sort-Object Name -Descending | Select-Object -First 1).FullName
& "$BT\apksigner.bat" verify --print-certs "SupermarketCalculator-vX.Y.Z-release.apk"
```

## Instalação no celular (protocolo obrigatório)

TODO APK deve ser: **compilado → instalado no celular via ADB → testado**.

```powershell
$ADB = "$env:ANDROID_HOME\platform-tools\adb.exe"
& $ADB devices                      # confirmar dispositivo
& $ADB install -r "SupermarketCalculator-vX.Y.Z-release.apk"   # atualizar por cima
& $ADB shell am start -n com.supermarket.calculator/.MainActivity
& $ADB logcat -s SuperCalc 2>&1     # checar erros
```

**Troca de assinatura só ocorre UMA vez** (aconteceu na v1.3.0 ao migrar de debug p/ release):
`adb uninstall com.supermarket.calculator` + `adb install <apk>`. **Isso apaga dados locais.**
Da v1.3.0 em diante, use SEMPRE `adb install -r` (mesma assinatura → preserva dados).

Erros comuns:
- `INSTALL_FAILED_UPDATE_INCOMPATIBLE` → assinatura diferente → desinstalar+reinstalar (perde dados).
- `INSTALL_FAILED_USER_RESTRICTED` → usar `adb install -r -t`.
- `INSTALL_FAILED_INVALID_APK: code is missing` → DEX não está na raiz do APK (build.ps1 cuida).

## Persistência do carrinho (fix v1.3.0)

- O carrinho (`items`, ArrayList<CartItem>) agora é salvo em **SharedPreferences** (JSON, chave `cart_state`)
  a **cada mutação** + `onPause()`, e restaurado no `onCreate`.
- Pontos de mutação cobertos: adicionar, editar, +/- quantidade, remover, limpar, carregar lista, "Adicionar todos".
- Se mexer em qualquer um desses fluxos, garanta que `saveCartState()` é chamado.

## Som no botão Adicionar (v1.4.0)

- O botão "Adicionar" toca um som ao adicionar item (após validação do preço).
- **3 sons pré-carregados** em `res/raw/`: `som_caixa.wav` (caixa registradora), `som_pop.wav`, `som_bip.wav`.
- Config em **Config → Som do botão Adicionar** (RadioGroup `soundGroup`, RadioButtons `soundCashRegister`/`soundPop`/`soundBeep`).
- Preferência salva em SharedPreferences (`settings`, chave `KEY_SOUND = "add_sound"`).
- Reprodução via **SoundPool** (`SoundPool.Builder().setMaxStreams(1)`), sons carregados no `onCreate`
  e liberados no `onDestroy`. Tocar: `soundPool.play(id, 1, 1, 1, 0, 1)`.
- Para trocar de som: basta substituir/renomear os WAVs em `res/raw/` (o aapt empacota `.wav` em `res/raw/`
  sem configuração extra) e manter os nomes `som_caixa/som_pop/som_bip`.

## Local do APK publicado

- `releases/SupermarketCalculator-vX.Y.Z.apk` (copiar de `SupermarketCalculator-vX.Y.Z-release.apk`).

## Aprendizados do ecossistema

- `conhecimento/aprendizados/2026-08-08-persistencia-carrinho-supermarketcalculator.md`
- Skill `android-pure-sdk` (`.opencode/skills/android-pure-sdk/SKILL.md`) — build resiliente + padrão de persistência.
