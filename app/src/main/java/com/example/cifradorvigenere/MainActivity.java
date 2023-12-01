package com.example.cifradorvigenere;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText textInput;
    private EditText keyInput;
    private TextView cipherTextOutput;
    private TextView numericValuesOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Configurar un Handler para retrasar el inicio de la actividad principal
        new Handler().postDelayed(() -> {
            // Código que se ejecutará después del tiempo de espera (2000 ms)
            setContentView(R.layout.activity_main);

            // Resto del código de inicialización
            textInput = findViewById(R.id.text_input);
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);

            keyInput = findViewById(R.id.key_input);
            keyInput.setInputType(InputType.TYPE_CLASS_TEXT);

            cipherTextOutput = findViewById(R.id.cipher_text_output);
            numericValuesOutput = findViewById(R.id.numeric_values_output);
        }, 2000);
    }

    public void onEncrypt(View view) {
        String text = textInput.getText().toString();
        String key = keyInput.getText().toString();

        // Validamos el texto a cifrar.
        if (!validateText(text)) {
            cipherTextOutput.setText("El texto debe contener solo caracteres alfanuméricos, espacios, comas, puntos, puntos y coma y comillas.");
            return;
        }
        // Generamos la clave de cifrado utilizando MD5.
        if (key.isEmpty()) {
            cipherTextOutput.setText("La clave de cifrado no puede estar vacía.");
            return;
        }
        key = generateMD5Hash(key);

        StringBuilder ciphertext = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            char k = key.charAt(i % key.length());
            int encryptedChar = (c + k) % 256; // Reducir módulo 256

            // Agregar el carácter cifrado al resultado
            ciphertext.append(encryptedChar).append(" ");
        }

        // Mostramos el resultado del cifrado.
        String result = ciphertext.toString();
        cipherTextOutput.setText(result);

        // Copiamos el resultado al portapapeles
        copyToClipboard(result);
    }

    public void onDecrypt(View view) {
        EditText encryptedTextInput = findViewById(R.id.encrypted_text_input);
        EditText decryptionKeyInput = findViewById(R.id.decryption_key_input);
        TextView decryptedTextOutput = findViewById(R.id.decrypted_text_output);

        String encryptedText = encryptedTextInput.getText().toString();
        String decryptionKey = decryptionKeyInput.getText().toString();

        // Validamos el texto cifrado y la clave.
        if (!validateText(encryptedText) || !validateText(decryptionKey)) {
            decryptedTextOutput.setText("El texto cifrado y la clave deben contener solo caracteres alfanuméricos.");
            return;
        }

        // Generamos la clave de descifrado utilizando MD5.
        decryptionKey = generateMD5Hash(decryptionKey);

        StringBuilder plaintext = new StringBuilder();
        String[] parts = encryptedText.trim().split(" ");
        for (String part : parts) {
            try {
                int encryptedChar = Integer.parseInt(part);
                char k = decryptionKey.charAt(plaintext.length() % decryptionKey.length());
                int decryptedChar = (encryptedChar - k + 256) % 256; // Asegurar resultado positivo

                // Convertir el resultado a un carácter imprimible
                if (Character.isISOControl(decryptedChar)) {
                    plaintext.append(" "); // Carácter de reemplazo para no imprimibles
                } else {
                    plaintext.append((char) decryptedChar);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                plaintext.append(" "); // Carácter de reemplazo para casos de error
            }
        }

        // Mostramos el resultado del descifrado.
        decryptedTextOutput.setText(plaintext.toString());
    }

    private boolean validateText(String text) {
        Pattern pattern = Pattern.compile("^[\\p{Print}&&[^\\p{Cntrl}]]*$");
        return pattern.matcher(text).matches();
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Ciphertext", text);
        clipboard.setPrimaryClip(clip);
    }

    private String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02X", b)); // Cambio a formato hexadecimal en mayúsculas
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}









