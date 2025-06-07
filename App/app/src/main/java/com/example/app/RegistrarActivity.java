package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class RegistrarActivity extends Activity {
    EditText nombreField, emailField, passwordField;
    Button registrarBtn, volverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Vincular el xml que corresponde a la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_activity);

        //Variables necesarias recuperadas del xml
        nombreField = findViewById(R.id.nombre);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        registrarBtn = findViewById(R.id.registrar);
        volverBtn = findViewById(R.id.volver);
        registrarBtn.setOnClickListener(v -> registrarUsuario());
        //Al presionar el botón de registrar se ejecuta el metodo para registrar un nuevo usuario
        volverBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrarActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void registrarUsuario() {
        //Variables necesarias para enviar mediante solicitud POST
        String nombre = nombreField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        //Checar que los campos no estén vacios
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                //Conexión a la API local
                URL url = new URL("http://10.0.2.2:8000/api/registrar");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                //Objeto JSON con los datos a recibir
                JSONObject json = new JSONObject();
                json.put("nombre", nombre);
                json.put("email", email);
                json.put("password", password);

                //Enviar el JSON
                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                //Si el registro es exitoso regresa al usuario al login
                int responseCode = conn.getResponseCode();
                if (responseCode == 201) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegistrarActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrarActivity.this, MainActivity.class));
                        finish();
                    });
                } else { //Si no es exitoso muestra un error
                    Scanner scanner = new Scanner(conn.getErrorStream()).useDelimiter("\\A");
                    String errorResponse = scanner.hasNext() ? scanner.next() : "Error desconocido";
                    runOnUiThread(() -> Toast.makeText(RegistrarActivity.this, "Error: " + errorResponse, Toast.LENGTH_LONG).show());
                }
                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(RegistrarActivity.this, "Excepción: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
