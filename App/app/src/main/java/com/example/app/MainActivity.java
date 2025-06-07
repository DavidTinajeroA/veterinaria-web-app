package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    EditText emailField, passwordField;
    Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Iniciar la main activity que es el login
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Variables necesarias recuperadas del xml
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.registro);

        //Ejecutar la el metodo para el login al hacer click al botón
        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrarActivity.class);
            startActivity(intent);
        });
    }

    //Metodo de login que realiza la solicitud al API para comprobar las credenciales
    //Si es exitoso redirige al usuario según su rol a la pantalla correspondiente
    private void loginUser() {

        //Recuperar los campos y checar que el llenado cumpla con restricciones básicas
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor llena ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(MainActivity.this, "Correo no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        //Limpiar al enviar login
        passwordField.post(() -> passwordField.setText(""));

        new Thread(() -> {
            try {
                //Conexión a la API local
                URL url = new URL("http://10.0.2.2:8000/api/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                //Objeto JSON con las credenciales
                JSONObject json = new JSONObject();
                json.put("email", email);
                json.put("password", password);

                //Enviar el JSON
                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                //Checar respuesta, si es exitosa se guarda como un string
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    //Convertir el string a JSON y recuperar el rol del usuario logeado
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONObject user = jsonResponse.getJSONObject("user");
                    int idRol = user.getInt("id_rol");

                    //Cambio de pantalla según el rol
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        //Si el rol corresponde al de veterinario manda a la pantalla principal de veterinario
                        if (idRol == 2) {
                            intent = new Intent(MainActivity.this, HomeVeterinarioActivity.class);
                        } else if (idRol == 3) { //Si el rol corresponde al de usuario manda a la pantalla principal de usuario
                            intent = new Intent(MainActivity.this, HomeUsuarioActivity.class);
                        } else {
                            return;
                        }
                        startActivity(intent);
                        finish();
                    });
                } else { //Si la respuesta no es exitosa manda error
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al intentar iniciar sesión", Toast.LENGTH_SHORT).show());
                }
                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
