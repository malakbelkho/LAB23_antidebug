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
    private TextView txtBenchmarkResult;

    private EditText inputFactorial;
    private EditText inputText;
    private EditText inputNumbers;
    private EditText inputBenchmarkRounds;

    private Button btnFactorial;
    private Button btnReverse;
    private Button btnArray;
    private Button btnBenchmark;

    private native String nativeGreeting();
    private native int nativeFactorialSafe(int number);
    private native String nativeMirrorText(String text);
    private native int nativeSumValues(int[] values);
    private native long nativeBenchmarkLoop(int rounds);

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
        txtBenchmarkResult = findViewById(R.id.txtBenchmarkResult);

        inputFactorial = findViewById(R.id.inputFactorial);
        inputText = findViewById(R.id.inputText);
        inputNumbers = findViewById(R.id.inputNumbers);

        btnFactorial = findViewById(R.id.btnFactorial);
        btnReverse = findViewById(R.id.btnReverse);
        btnArray = findViewById(R.id.btnArray);
        btnBenchmark = findViewById(R.id.btnBenchmark);
        inputBenchmarkRounds = findViewById(R.id.inputBenchmarkRounds);
    }

    private void prepareDefaultContent() {
        txtNativeHello.setText(nativeGreeting());

        inputFactorial.setText("10");
        inputText.setText("JNI is powerful");
        inputNumbers.setText("10, 20, 30, 40, 50");

        runFactorialDemo();
        runReverseDemo();
        runArrayDemo();
        inputBenchmarkRounds.setText("5000000");
        txtBenchmarkResult.setText("Clique sur le bouton pour comparer Java et C++.");
    }

    private void configureActions() {
        btnFactorial.setOnClickListener(view -> runFactorialDemo());
        btnReverse.setOnClickListener(view -> runReverseDemo());
        btnArray.setOnClickListener(view -> runArrayDemo());
        btnBenchmark.setOnClickListener(view -> runBenchmarkDemo());
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

    private long javaBenchmarkLoop(int rounds) {
        long total = 0;

        for (int i = 1; i <= rounds; i++) {
            total += i;
        }

        return total;
    }

    private void runBenchmarkDemo() {
        try {
            String rawRounds = inputBenchmarkRounds.getText().toString().trim();

            if (rawRounds.isEmpty()) {
                showMessage("Veuillez saisir un nombre d’itérations.");
                return;
            }

            int rounds = Integer.parseInt(rawRounds);

            if (rounds <= 0) {
                showMessage("Le nombre d’itérations doit être positif.");
                return;
            }

            long javaStart = System.nanoTime();
            long javaResult = javaBenchmarkLoop(rounds);
            long javaEnd = System.nanoTime();

            long nativeStart = System.nanoTime();
            long nativeResult = nativeBenchmarkLoop(rounds);
            long nativeEnd = System.nanoTime();

            double javaDurationMs = (javaEnd - javaStart) / 1_000_000.0;
            double nativeDurationMs = (nativeEnd - nativeStart) / 1_000_000.0;

            String result =
                    "Calcul : somme de 1 à " + rounds +
                            "\nRésultat Java : " + javaResult +
                            "\nTemps Java : " + String.format("%.3f", javaDurationMs) + " ms" +
                            "\nRésultat C++ : " + nativeResult +
                            "\nTemps C++ : " + String.format("%.3f", nativeDurationMs) + " ms";

            txtBenchmarkResult.setText(result);

        } catch (NumberFormatException exception) {
            showMessage("Le benchmark doit recevoir un entier valide.");
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}