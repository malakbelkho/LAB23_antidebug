package ma.ensa.mobile.jnibridge;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private LinearLayout cardSecurityStatus;

    private TextView txtSecurityTitle;
    private TextView txtSecurityDetails;
    private TextView txtNativeHello;
    private TextView txtFactorialResult;
    private TextView txtReverseResult;
    private TextView txtArrayResult;
    private TextView txtBenchmarkResult;

    private EditText inputFactorial;
    private EditText inputText;
    private EditText inputNumbers;
    private EditText inputBenchmarkRounds;

    private Button btnSecurityScan;
    private Button btnFactorial;
    private Button btnReverse;
    private Button btnArray;
    private Button btnBenchmark;

    private boolean nativeAccessAllowed = false;

    private native boolean isDebugDetected();
    private native int nativeSecurityCode();
    private native String nativeSecuritySummary();

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
        runSecurityScan();
    }

    private void bindViews() {
        cardSecurityStatus = findViewById(R.id.cardSecurityStatus);

        txtSecurityTitle = findViewById(R.id.txtSecurityTitle);
        txtSecurityDetails = findViewById(R.id.txtSecurityDetails);
        txtNativeHello = findViewById(R.id.txtNativeHello);
        txtFactorialResult = findViewById(R.id.txtFactorialResult);
        txtReverseResult = findViewById(R.id.txtReverseResult);
        txtArrayResult = findViewById(R.id.txtArrayResult);
        txtBenchmarkResult = findViewById(R.id.txtBenchmarkResult);

        inputFactorial = findViewById(R.id.inputFactorial);
        inputText = findViewById(R.id.inputText);
        inputNumbers = findViewById(R.id.inputNumbers);
        inputBenchmarkRounds = findViewById(R.id.inputBenchmarkRounds);

        btnSecurityScan = findViewById(R.id.btnSecurityScan);
        btnFactorial = findViewById(R.id.btnFactorial);
        btnReverse = findViewById(R.id.btnReverse);
        btnArray = findViewById(R.id.btnArray);
        btnBenchmark = findViewById(R.id.btnBenchmark);
    }

    private void prepareDefaultContent() {
        inputFactorial.setText("10");
        inputText.setText("JNI Anti Debug");
        inputNumbers.setText("10, 20, 30, 40, 50");
        inputBenchmarkRounds.setText("5000000");

        txtNativeHello.setText("Analyse native en cours...");
        txtFactorialResult.setText("En attente de validation de sécurité.");
        txtReverseResult.setText("En attente de validation de sécurité.");
        txtArrayResult.setText("En attente de validation de sécurité.");
        txtBenchmarkResult.setText("En attente de validation de sécurité.");
    }

    private void configureActions() {
        btnSecurityScan.setOnClickListener(view -> runSecurityScan());
        btnFactorial.setOnClickListener(view -> runFactorialDemo());
        btnReverse.setOnClickListener(view -> runReverseDemo());
        btnArray.setOnClickListener(view -> runArrayDemo());
        btnBenchmark.setOnClickListener(view -> runBenchmarkDemo());
    }

    private void runSecurityScan() {
        int securityCode = nativeSecurityCode();
        String nativeSummary = nativeSecuritySummary();

        nativeAccessAllowed = securityCode == 0;

        if (nativeAccessAllowed) {
            cardSecurityStatus.setBackgroundResource(R.drawable.bg_security_safe);
            txtSecurityTitle.setText("État sécurité : environnement fiable");
            txtSecurityTitle.setTextColor(Color.parseColor("#14532D"));
            txtSecurityDetails.setText("Code natif : 0\n" + nativeSummary + "\nLes fonctions natives sensibles sont autorisées.");

            applyNativeActionState(true);

            txtNativeHello.setText(nativeGreeting());
            runFactorialDemo();
            runReverseDemo();
            runArrayDemo();
            txtBenchmarkResult.setText("Clique sur le bouton pour comparer Java et C++.");

        } else {
            cardSecurityStatus.setBackgroundResource(R.drawable.bg_security_alert);
            txtSecurityTitle.setText("État sécurité : environnement suspect");
            txtSecurityTitle.setTextColor(Color.parseColor("#7F1D1D"));
            txtSecurityDetails.setText("Code natif : " + securityCode + "\n" + nativeSummary + "\nMode restreint activé.");

            applyNativeActionState(false);

            txtNativeHello.setText("Fonction native sensible désactivée.");
            txtFactorialResult.setText("Calcul natif bloqué par la couche anti-debug.");
            txtReverseResult.setText("Inversion native bloquée par la couche anti-debug.");
            txtArrayResult.setText("Traitement du tableau bloqué par la couche anti-debug.");
            txtBenchmarkResult.setText("Benchmark bloqué par la couche anti-debug.");
        }
    }

    private void applyNativeActionState(boolean enabled) {
        btnFactorial.setEnabled(enabled);
        btnReverse.setEnabled(enabled);
        btnArray.setEnabled(enabled);
        btnBenchmark.setEnabled(enabled);

        float alpha = enabled ? 1.0f : 0.45f;

        btnFactorial.setAlpha(alpha);
        btnReverse.setAlpha(alpha);
        btnArray.setAlpha(alpha);
        btnBenchmark.setAlpha(alpha);
    }

    private boolean ensureNativeAccessAllowed() {
        if (!nativeAccessAllowed) {
            showMessage("Action bloquée : environnement suspect détecté.");
            return false;
        }

        return true;
    }

    private void runFactorialDemo() {
        if (!ensureNativeAccessAllowed()) {
            return;
        }

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
                txtFactorialResult.setText("Erreur native -1 : nombre négatif refusé.");
            } else if (nativeResult == -2) {
                txtFactorialResult.setText("Erreur native -2 : dépassement de la limite int.");
            } else {
                txtFactorialResult.setText("Erreur native inconnue : " + nativeResult);
            }

        } catch (NumberFormatException exception) {
            showMessage("La valeur du factoriel doit être un entier.");
        }
    }

    private void runReverseDemo() {
        if (!ensureNativeAccessAllowed()) {
            return;
        }

        String originalText = inputText.getText().toString();
        String reversedText = nativeMirrorText(originalText);

        txtReverseResult.setText("Texte inversé par C++ : " + reversedText);
    }

    private void runArrayDemo() {
        if (!ensureNativeAccessAllowed()) {
            return;
        }

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
        if (!ensureNativeAccessAllowed()) {
            return;
        }

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