# SupermarketCalculator — Guia de Build e Release (v1.5.7)

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
| 1.5.7 | 13 | 2026-08-08 | Fix 5 bugs MainActivity + opções "Salvar como novo"/"Atualizar" ao editar lista |
| 1.5.8 | 14 | 2026-09-03 | Nova 5ª aba "Simples": calculadora comum (+, −, ×, ÷, %, vírgula, limpar, apagar) |
| 1.5.9 | 15 | 2026-09-03 | Aba Simples: visor com formatação brasileira automática (1.000, 10,50) mantendo cálculo correto |
| 1.5.10 | 16 | 2026-11-02 | Remoção do reset ao digitar número depois do "=" na aba Simples |
| 1.5.11 | 17 | 2026-11-02 | Fix do botão "=" morto após digitar número: nova entrada preserva o resultado anterior e o "=" volta a calcular |

## Assinatura (release keystore)

**⚠️ CRÍTICO — guarde a keystore com segurança.** Sem ela, nenhuma atualização futura
poderá ser instalada por cima da v1.3.0 (o Android exige a MESMA assinatura; trocar = desinstalar = perda de dados).

- **Arquivo:** `release.keystore` (na raiz do projeto)
- **Backup dentro do ecossistema:** `backups/keystores/supermarketcalculator/release.keystore`
  (a pasta `backups/` é ignorada pelo git; o arquivo sumiu do disco em 2026-11-02 e teve de ser recriado)
- **Alias:** `supermarket`
- **StorePass / KeyPass:** `opencode`
- **DN:** `CN=Supermarket Calculator, OU=UmGrau, O=EcoSystemUmGrau, L=Belem, ST=Para, C=BR`
- **Fingerprint SHA-256 ATUAL:** `ce2ae9dfe580df6519f7c2afdd96676a57591b62fd9f851d2c56f1e4bc3510c9`

### Troca de chave em 2026-11-02 (v1.5.11)
- A keystore original **se perdeu** (não estava no disco nem no histórico do git — sempre foi ignorada pelo git).
- O arquivo foi recriado com o mesmo alias, senha e DN, mas com um **par de chaves novo**.
- Fingerprint antigo (perdido, ainda vale para os APKs já publicados v1.2 até v1.5.9):
  `20b1693cd2ccdbe62294e83bb990599e77762618a774842850f0e73e29026562`
- Consequência: APK assinado com a chave nova **não atualiza por cima** dos APKs publicados com a chave antiga
  (`INSTALL_FAILED_UPDATE_INCOMPATIBLE`). Quem tiver a v1.5.9 instalada terá de desinstalar (perde os dados).
- **Migração feita sem perder dados** no aparelho do usuário: os 70 itens customizados foram extraídos,
  restaurados e conferidos. Detalhes em
  `conhecimento/aprendizados/2026-11-02-migracao-assinatura-dados-supermarketcalculator.md`.
- **Estado atual do aparelho:** app instalado em release, fingerprint novo
  `ce2ae9dfe580df6519f7c2afdd96676a57591b62fd9f851d2c56f1e4bc3510c9`, versionCode 17, `android:debuggable="false"`.
- Da v1.5.11 em diante, **todas** as atualizações usam `adb install -r` com esta keystore: os dados do
  usuário são preservados e nada mais precisa desinstalar.

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

**Troca de assinatura já foi resolvida** (2026-11-02, v1.5.11): o aparelho do usuário foi migrado
para a chave nova **sem perder dados**, e a migração foi validada na tela. Da v1.5.11 em diante use
SEMPRE `adb install -r` (mesma assinatura → preserva dados). Se a keystore for perdida de novo, o
procedimento de migração preservando dados está em
`conhecimento/aprendizados/2026-11-02-migracao-assinatura-dados-supermarketcalculator.md`.

### Build de release (precisa das variáveis de ambiente)

O `build.ps1 -Release` aborta sem `RELEASE_STOREPASS`, `RELEASE_KEYPASS` e `RELEASE_KEYALIAS`.
Defina-as **na sessão** (não grave no perfil do Windows) e rode o build:

```powershell
$env:RELEASE_STOREPASS='opencode'; $env:RELEASE_KEYPASS='opencode'; $env:RELEASE_KEYALIAS='supermarket'
.\build.ps1 -Release -OutputName "SupermarketCalculator-v1.5.11"
Copy-Item "SupermarketCalculator-v1.5.11-release.apk" "releases\SupermarketCalculator-v1.5.11.apk"
```

Erros comuns:
- `INSTALL_FAILED_UPDATE_INCOMPATIBLE` → assinatura diferente → desinstalar+reinstalar (perde dados).
- `INSTALL_FAILED_USER_RESTRICTED` → usar `adb install -r -t`.
- `INSTALL_FAILED_INVALID_APK: code is missing` → DEX não está na raiz do APK (build.ps1 cuida).
- `Release build requires RELEASE_STOREPASS...` → variáveis de ambiente não definidas (ver acima).
- `FileNotFoundException: ...release.keystore` → keystore sumiu do projeto → restaurar do backup
  em `C:\Users\David Jr\.config\supermarket-calculator\release.keystore` (nunca regere sem necessidade:
  o par de chaves novo quebra a atualização por cima das versões já instaladas).

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

## Calculadora Simples — regra do botão "=" (fix v1.5.11)

- O visor tem 3 campos: `scExpression` (linha da expressão), `scResult` (resultado anterior) e `scDisplay` (número em edição).
- Após apertar "=", o resultado fica visível em `scExpression`/`scResult` e `scDisplay` fica vazio.
- Ao digitar um número depois do "=", o app entra em **entrada nova** (`scFreshEntry = true`):
  o número vai para `scDisplay` e o resultado anterior **permanece visível** (não é resetado).
- Nesse estado o botão "=" continua funcionando: confirma o número novo (`7 =`).
- Apertar um operador durante a entrada nova descarta o resultado anterior e inicia a conta com o número digitado.
- Antes do fix (v1.5.10), `scLastWasEquals` continuava `true` ao digitar número → o "=" retornava sem fazer nada (botão morto).

## Local do APK publicado

- `releases/SupermarketCalculator-vX.Y.Z.apk` (copiar de `SupermarketCalculator-vX.Y.Z-release.apk`).

## Aprendizados do ecossistema

- `conhecimento/aprendizados/2026-08-08-persistencia-carrinho-supermarketcalculator.md`
- `conhecimento/aprendizados/2026-11-02-bug-fix-equals-button-supermarketcalculator.md`
- `conhecimento/aprendizados/2026-11-02-keystore-release-supermarketcalculator.md`
- `conhecimento/aprendizados/2026-11-02-migracao-assinatura-dados-supermarketcalculator.md`
- Skill `android-pure-sdk` (`.opencode/skills/android-pure-sdk/SKILL.md`) — build resiliente + padrão de persistência.
