"""Verificacao de integridade do projeto SupermarketCalculator.

Confere que o codigo, os recursos e o build batem entre si.
Uso: python tests/verificar_integridade.py
"""

import sys
import xml.etree.ElementTree as ElementTree
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
PACOTE = Path("src") / "com" / "supermarket" / "calculator"
FALHAS = []


def checar(condicao, mensagem):
    if condicao:
        print(f"[OK] {mensagem}")
    else:
        print(f"[FALHA] {mensagem}")
        FALHAS.append(mensagem)


def main():
    codigo = RAIZ / PACOTE
    checar(codigo.is_dir(), "pasta de codigo existe: src/com/supermarket/calculator")
    if codigo.is_dir():
        for arquivo in ["MainActivity.java", "SplashActivity.java", "R.java"]:
            checar((codigo / arquivo).is_file(), f"classe existe: {arquivo}")
        checar((codigo / "adapter" / "CartAdapter.java").is_file(),
               "adapter existe: adapter/CartAdapter.java")
        checar((codigo / "models" / "CartItem.java").is_file(),
               "modelo existe: models/CartItem.java")

    for recurso in ["res/layout/activity_main.xml", "res/values/strings.xml",
                    "res/values/colors.xml"]:
        checar((RAIZ / recurso).is_file(), f"recurso existe: {recurso}")

    manifesto = RAIZ / "AndroidManifest.xml"
    checar(manifesto.is_file(), "AndroidManifest.xml existe")
    if manifesto.is_file():
        raiz = ElementTree.parse(manifesto).getroot()
        pacote = raiz.get("package")
        checar(pacote == "com.supermarket.calculator",
               f"pacote do manifesto e com.supermarket.calculator")
        activities = [
            elemento.get("{http://schemas.android.com/apk/res/android}name")
            for elemento in raiz.iter("activity")
        ]
        checar(any(nome and "MainActivity" in nome for nome in activities),
               "MainActivity declarada no manifesto")

    activity = codigo / "MainActivity.java"
    if activity.is_file():
        texto = activity.read_text(encoding="utf-8", errors="replace")
        for aba in ["calculatorPage", "expensesPage", "savedListsPage"]:
            checar(aba in texto, f"página presente na Activity: {aba}")

    checar((RAIZ / "build.ps1").is_file(), "build.ps1 existe")
    checar((RAIZ / "README.md").is_file(), "README.md existe")
    checar((RAIZ / "docs" / "architecture" / "ARQUITETURA.md").is_file(),
           "arquitetura documentada em docs/architecture")

    print()
    if FALHAS:
        print(f"RESULTADO: {len(FALHAS)} verificacao(oes) falharam")
        return 1
    print("RESULTADO: todas as verificacoes passaram")
    return 0


if __name__ == "__main__":
    sys.exit(main())
