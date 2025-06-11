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
    //Campos de entrada y botones de la pantalla de login
    EditText emailField, passwordField;
    Button loginButton, registerButton, recuperarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Enlazar vistas de la interfaz con variables Java
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.registro);
        recuperarButton = findViewById(R.id.recuperar);

        //Acción al presionar el botón de login
        loginButton.setOnClickListener(v -> loginUser());

        //Acción al presionar el botón de registro
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrarActivity.class);
            startActivity(intent);
        });

        //Recuperar contraseña enviando el correo
        recuperarButton.setOnClickListener(v -> enviarSolicitudRecuperacion());

        //Verificar si hay una sesión previamente guardada en SQLite
        DBHelper dbHelper = new DBHelper(this);
        int savedRol = dbHelper.obtenerRol();
        String savedToken = dbHelper.obtenerToken();

        //Si hay token, redirigir según el rol
        if (savedToken != null && !savedToken.isEmpty()) {
            if (savedRol == 2) {
                startActivity(new Intent(MainActivity.this, HomeVeterinarioActivity.class));
                finish();
            } else if (savedRol == 3) {
                startActivity(new Intent(MainActivity.this, HomeUsuarioActivity.class));
                finish();
            }
        }
    }

    private void loginUser() {
        //Obtener los datos del formulario
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        //Validaciones básicas del formulario
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor llena ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(MainActivity.this, "Correo no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        //Limpiar el campo de contraseña por seguridad
        passwordField.post(() -> passwordField.setText(""));

        //Ejecutar la solicitud HTTP en un hilo secundario
        new Thread(() -> {
            try {
                //Configurar la conexión HTTP al backend
                URL url = new URL("http://10.0.2.2:8000/api/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                //Crear el cuerpo del JSON con email y contraseña
                JSONObject json = new JSONObject();
                json.put("email", email);
                json.put("password", password);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    //Leer y procesar la respuesta del servidor
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    //Extraer el token y los datos del usuario
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String token = jsonResponse.getString("token");
                    JSONObject user = jsonResponse.getJSONObject("user");
                    int idRol = user.getInt("id_rol");
                    String nombre = user.getString("nombre");

                    //Guardar el token, rol y nombre en SQLite
                    DBHelper dbHelper = new DBHelper(MainActivity.this);
                    dbHelper.guardarSesion(idRol, token, nombre);

                    //Redirigir al usuario al home según su rol
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        if (idRol == 2) {
                            intent = new Intent(MainActivity.this, HomeVeterinarioActivity.class);
                        } else if (idRol == 3) {
                            intent = new Intent(MainActivity.this, HomeUsuarioActivity.class);
                        } else {
                            return;
                        }
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al intentar iniciar sesión", Toast.LENGTH_SHORT).show());
                }
                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
    private void enviarSolicitudRecuperacion() {
        String email = emailField.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(MainActivity.this, "Correo no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8000/api/recuperar"); // URL de tu API para recuperar contraseña
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("email", email);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Correo de recuperación enviado", Toast.LENGTH_LONG).show());
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String errorMsg = "Error";
                    try {
                        JSONObject errJson = new JSONObject(response.toString());
                        errorMsg = errJson.optString("message", errorMsg);
                    } catch (Exception ignored) {}

                    final String finalErrorMsg = errorMsg;
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, finalErrorMsg, Toast.LENGTH_LONG).show());
                }

                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
