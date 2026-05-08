package ma.ensa.mobile.jnibridge;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private TextView txtNativeHello;
    private TextView txtFactorialResult;
    private TextView txtReverseResult;
    private TextView txtArrayResult;

    private EditText inputFactorial;
    private EditText inputText;
    private EditText inputNumbers;

    private Button btnFactorial;
    private Button btnReverse;
    private Button btnArray;

    private native String nativeGreeting();
    private native int nativeFactorialSafe(int number);
    private native String nativeMirrorText(String text);
    private native int nativeSumValues(int[] values);

    static {
        System.loadLibrary("secure_bridge");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.parseColor("#10172A"));

        bindViews();
        prepareDefaultContent();
        configureActions();
    }

    private void bindViews() {
        txtNativeHello = findViewById(R.id.txtNativeHello);
        txtFactorialResult = findViewById(R.id.txtFactorialResult);
        txtReverseResult = findViewById(R.id.txtReverseResult);
        txtArrayResult = findViewById(R.id.txtArrayResult);

        inputFactorial = findViewById(R.id.inputFactorial);
        inputText = findViewById(R.id.inputText);
        inputNumbers = findViewById(R.id.inputNumbers);

        btnFactorial = findViewById(R.id.btnFactorial);
        btnReverse = findViewById(R.id.btnReverse);
        btnArray = findViewById(R.id.btnArray);
    }

    private void prepareDefaultContent() {
        txtNativeHello.setText(nativeGreeting());

        inputFactorial.setText("10");
        inputText.setText("JNI is powerful");
        inputNumbers.setText("10, 20, 30, 40, 50");

        runFactorialDemo();
        runReverseDemo();
        runArrayDemo();
    }

    private void configureActions() {
        btnFactorial.setOnClickListener(view -> runFactorialDemo());
        btnReverse.setOnClickListener(view -> runReverseDemo());
        btnArray.setOnClickListener(view -> runArrayDemo());
    }

    private void runFactorialDemo() {
        try {
            String rawValue = inputFactorial.getText().toString().trim();

            if (rawValue.isEmpty()) {
                showMessage("Veuillez saisir un nombre.");
                return;
            }

            int number = Integer.parseInt(rawValue);
            int nativeResult = nativeFactorialSafe(number);

            if (nativeResult >= 0) {
                txtFactorialResult.setText("Résultat natif : " + number + "! = " + nativeResult);
            } else if (nativeResult == -1) {
                txtFactorialResult.setText("Erreur native -1 : le factoriel d’un nombre négatif est refusé.");
            } else if (nativeResult == -2) {
                txtFactorialResult.setText("Erreur native -2 : dépassement de la limite int détecté.");
            } else {
                txtFactorialResult.setText("Erreur native inconnue : " + nativeResult);
            }

        } catch (NumberFormatException exception) {
            showMessage("La valeur du factoriel doit être un entier.");
        }
    }

    private void runReverseDemo() {
        String originalText = inputText.getText().toString();
        String reversedText = nativeMirrorText(originalText);

        txtReverseResult.setText("Texte inversé par C++ : " + reversedText);
    }

    private void runArrayDemo() {
        try {
            int[] parsedNumbers = parseNumbersFromInput();
            int nativeSum = nativeSumValues(parsedNumbers);

            if (nativeSum >= 0) {
                txtArrayResult.setText("Somme native du tableau : " + nativeSum);
            } else {
                txtArrayResult.setText("Erreur native pendant le traitement du tableau : " + nativeSum);
            }

        } catch (NumberFormatException exception) {
            showMessage("Utilise seulement des entiers séparés par des virgules.");
        }
    }

    private int[] parseNumbersFromInput() {
        String rawText = inputNumbers.getText().toString().trim();

        if (rawText.isEmpty()) {
            return new int[]{};
        }

        String[] parts = rawText.split(",");
        ArrayList<Integer> collectedValues = new ArrayList<>();

        for (String part : parts) {
            String cleanedPart = part.trim();

            if (!cleanedPart.isEmpty()) {
                collectedValues.add(Integer.parseInt(cleanedPart));
            }
        }

        int[] finalArray = new int[collectedValues.size()];

        for (int i = 0; i < collectedValues.size(); i++) {
            finalArray[i] = collectedValues.get(i);
        }

        return finalArray;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}