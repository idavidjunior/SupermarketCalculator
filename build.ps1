param(
    [string]$OutputName = "SupermarketCalculator",
    [switch]$Release = $false
)

$BuildDir = "build"
$ANDROID_HOME = $env:ANDROID_HOME
$FullBuildDir = "$pwd\$BuildDir"

# Auto-detect installed build-tools (highest version first)
$BT = Get-ChildItem "$ANDROID_HOME\build-tools" -Directory -ErrorAction SilentlyContinue |
    Sort-Object Name -Descending | Select-Object -First 1 -ExpandProperty FullName
if (-not $BT) { throw "build-tools not found under $ANDROID_HOME" }
# Auto-detect installed platform
$PlatformDir = Get-ChildItem "$ANDROID_HOME\platforms" -Directory -ErrorAction SilentlyContinue |
    Sort-Object Name -Descending | Select-Object -First 1 -ExpandProperty FullName
if (-not $PlatformDir) { throw "platforms not found under $ANDROID_HOME" }
$PLATFORM = "$PlatformDir\android.jar"

if ($Release) {
    $Suffix = "-release"
    $KEYSTORE = "$pwd\release.keystore"
    $STOREPASS = if ($env:RELEASE_STOREPASS) { $env:RELEASE_STOREPASS } else { "opencode" }
    $KEYPASS = if ($env:RELEASE_KEYPASS) { $env:RELEASE_KEYPASS } else { "opencode" }
    $KEYALIAS = if ($env:RELEASE_KEYALIAS) { $env:RELEASE_KEYALIAS } else { "supermarket" }
    Write-Host "=== Build RELEASE APK ===" -ForegroundColor Cyan
} else {
    $Suffix = "-debug"
    $KEYSTORE = "$env:USERPROFILE\.android\debug.keystore"
    $STOREPASS = "android"
    $KEYPASS = "android"
    $KEYALIAS = "androiddebugkey"
    Write-Host "=== Build DEBUG APK ===" -ForegroundColor Green
}

$ApkName = "$OutputName$Suffix"

if (Test-Path $BuildDir) { Remove-Item -Recurse -Force $BuildDir }
New-Item -ItemType Directory -Path "$BuildDir\classes", "$BuildDir\dex" -Force | Out-Null

# Response file for javac — use relative paths (no spaces in repo root) to avoid quoting issues
$srcFiles = Get-ChildItem -Recurse -Path src -Filter "*.java" |
    Where-Object { $_.Name -ne "CalcSimplesActivity.java" } |
    ForEach-Object { $_.FullName }
$srcFileList = Join-Path $FullBuildDir "sources.txt"
$projDir = $PSScriptRoot
$rel = @()
foreach ($f in $srcFiles) {
    $rel += $f.Substring($projDir.Length).TrimStart('\','/')
}
[System.IO.File]::WriteAllLines($srcFileList, $rel, (New-Object System.Text.UTF8Encoding($false)))

function exec($desc, $scriptblock) {
    Write-Host $desc -ForegroundColor Cyan
    & $scriptblock 2>&1
    if ($LASTEXITCODE -ne 0) { throw "$desc FAILED (exit $LASTEXITCODE)" }
}

exec "Generate R.java" { & "$BT\aapt.exe" package -f -m -M AndroidManifest.xml -S res -I $PLATFORM -J src }
exec "Compile Java" { & javac -encoding UTF-8 -cp "$PLATFORM;$env:ANDROID_HOME\extras\androidx\appcompat\appcompat.jar;$env:ANDROID_HOME\extras\androidx\core\core.jar;$env:ANDROID_HOME\extras\androidx\fragment\fragment.jar" -d "$BuildDir\classes" "@$srcFileList" }
exec "Create JAR" { & jar cf "$BuildDir\classes.jar" -C "$BuildDir\classes" . }
exec "Convert to DEX" { cmd /c "`"$BT\d8.bat`" --lib `"$PLATFORM`" --output `"$FullBuildDir\dex`" `"$FullBuildDir\classes.jar`"" }
exec "Package APK" { & "$BT\aapt.exe" package -f -M AndroidManifest.xml -S res -I $PLATFORM -F "$BuildDir\$ApkName-unsigned.apk" }
exec "Add DEX to APK root" { Push-Location "$BuildDir\dex"; try { & "$BT\aapt.exe" add "$FullBuildDir\$ApkName-unsigned.apk" classes.dex } finally { Pop-Location } }
exec "Zipalign" { & "$BT\zipalign.exe" -f 4 "$BuildDir\$ApkName-unsigned.apk" "$BuildDir\$ApkName-aligned.apk" }
exec "Sign APK" { cmd /c "`"$BT\apksigner.bat`" sign --ks `"$KEYSTORE`" --ks-pass pass:$STOREPASS --key-pass pass:$KEYPASS --ks-key-alias $KEYALIAS `"$FullBuildDir\$ApkName-aligned.apk`"" }

Copy-Item "$BuildDir\$ApkName-aligned.apk" "$ApkName.apk" -Force
Write-Host "=== APK ready: $ApkName.apk ===" -ForegroundColor Green
