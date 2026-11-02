package com.supermarket.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class CalcSimplesActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText display;
    private String operando1 = "";
    private String operador = "";
    private String operando2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc_simples);

        display = findViewById(R.id.txtDisplay);
        // Configura todos os botões para usar onClick
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnClear, R.id.btnNeg, R.id.btnPercent,
                R.id.btnDiv, R.id.btnMul, R.id.btnMinus, R.id.btnPlus,
                R.id.btnEquals
        };
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        String texto = display.getText().toString();
        int id = v.getId();

        switch (id) {
            case R.id.btn0: case R.id.btn1: case R.id.btn2: case R.id.btn3:
            case R.id.btn4: case R.id.btn5: case R.id.btn6: case R.id.btn7:
            case R.id.btn8: case R.id.btn9:
                display.setText(texto + ((Button) v).getText());
                if (!operador.isEmpty()) {
                    operando2 = display.getText().toString();
                } else {
                    operando1 = display.getText().toString();
                }
                break;
            case R.id.btnDot:
                if (!texto.contains(".")) display.setText(texto + ".");
                if (!operador.isEmpty()) operando2 = display.getText().toString();
                else operando1 = display.getText().toString();
                break;
            case R.id.btnClear:
                display.setText("");
                operando1 = operador = operando2 = "";
                break;
            case R.id.btnNeg:
                if (!texto.isEmpty()) {
                    display.setText(texto.startsWith("-") ? texto.substring(1) : "-" + texto);
                    if (!operador.isEmpty()) operando2 = display.getText().toString();
                    else operando1 = display.getText().toString();
                }
                break;
            case R.id.btnPercent:
                if (texto.isEmpty()) break;
                try {
                    double value = Double.parseDouble(texto.replace(",", "."));
                    if (Double.isFinite(value)) {
                        display.setText(String.format(java.util.Locale.US, "%.10f", value / 100).replace(",", "."));
                        if (!operador.isEmpty()) operando2 = display.getText().toString();
                        else operando1 = display.getText().toString();
                    }
                } catch (NumberFormatException ignored) {}
                break;
            case R.id.btnDiv: case R.id.btnMul: case R.id.btnMinus: case R.id.btnPlus:
                if (!operando1.isEmpty() && !operador.isEmpty() && !operando2.isEmpty()) calcular();
                else if (!texto.isEmpty()) operando1 = texto;
                operador = ((Button) v).getText().toString();
                operando2 = "";
                if (!operando1.isEmpty()) display.setText("");
                break;
            case R.id.btnEquals:
                if (!operador.isEmpty()) calcular();
                break;
        }
    }

    private void calcular() {
        try {
            double a = Double.parseDouble(operando1);
            double b = Double.parseDouble(operando2);
            double r;
            switch (operador) {
                case "÷":
                    if (b == 0) {
                        display.setText("Erro: divisão por zero");
                        operando1 = operando2 = operador = "";
                        return;
                    }
                    r = a / b;
                    break;
                case "×": r = a * b; break;
                case "‑": case "−": r = a - b; break;
                case "+": r = a + b; break;
                default: return;
            }
            display.setText(String.format("%s", r));
            operando1 = operando2 = operador = "";
        } catch (NumberFormatException ignored) {}
    }
}