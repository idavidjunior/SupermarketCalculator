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
                break;
            case R.id.btnDot:
                if (!texto.contains(".")) display.setText(texto + ".");
                break;
            case R.id.btnClear:
                display.setText("");
                operando1 = operador = operando2 = "";
                break;
            case R.id.btnNeg:
                if (!texto.isEmpty()) display.setText("-" + texto);
                break;
            case R.id.btnPercent:
                display.setText("" + (Double.parseDouble(texto) / 100));
                break;
            case R.id.btnDiv: case R.id.btnMul: case R.id.btnMinus: case R.id.btnPlus:
                if (!texto.isEmpty() && !operador.isEmpty()) calcular();
                operando1 = texto;
                operador = ((Button) v).getText().toString();
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
                case "÷": r = a / b; break;
                case "×": r = a * b; break;
                case "‑": r = a - b; break;
                case "+": r = a + b; break;
                default: return;
            }
            display.setText(String.format("%s", r));
            operando1 = operando2 = operador = "";
        } catch (NumberFormatException ignored) {}
    }
}