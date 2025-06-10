package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DatosSubirActivity extends Activity {

    EditText Direccion, Telefono;
    Button btnSubir;
    String token;
    int idRol;  // 2: veterinario, 3: usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener token y rol del usuario desde la base de datos SQLite local
        DBHelper dbHelper = new DBHelper(this);
        token = dbHelper.obtenerToken();
        idRol = dbHelper.obtenerRol();
        dbHelper.close();

        //Elegir layout según el rol del usuario
        if (idRol == 2) {
            setContentView(R.layout.datos_veterinario_activity);
        } else if (idRol == 3) {
            setContentView(R.layout.datos_usuario_activity);
        }

        //Vincular los EditText y el botón del layout
        Direccion = findViewById(R.id.direccion);
        Telefono = findViewById(R.id.telefono);
        btnSubir = findViewById(R.id.btnEditarUsuario); //Usamos el mismo botón que en DatosActivity

        //Configurar botones de navegación comunes
        Button btnCita = findViewById(R.id.cita);
        Button btnNotificacion = findViewById(R.id.notificacion);
        btnCita.setOnClickListener(view -> startActivity(new Intent(DatosSubirActivity.this, CitaActivity.class)));
        btnNotificacion.setOnClickListener(view -> startActivity(new Intent(DatosSubirActivity.this, NotificacionActivity.class)));

        //Configurar botones de navegación según rol
        if (idRol == 2) {
            Button btnConsulta = findViewById(R.id.consulta);
            btnConsulta.setOnClickListener(view -> startActivity(new Intent(DatosSubirActivity.this, ConsultaActivity.class)));
        } else if (idRol == 3) {
            Button btnCatalogo = findViewById(R.id.catalogo);
            Button btnMascota = findViewById(R.id.mascota);
            btnCatalogo.setOnClickListener(view -> startActivity(new Intent(DatosSubirActivity.this, CatalogoActivity.class)));
            btnMascota.setOnClickListener(view -> startActivity(new Intent(DatosSubirActivity.this, MascotaActivity.class)));
        }

        //Configurar acción del botón para subir los datos vía POST
        btnSubir.setOnClickListener(v -> {
            String nuevaDireccion = Direccion.getText().toString().trim();
            String nuevoTelefono = Telefono.getText().toString().trim();

            //Validar que los campos no estén vacíos
            if (nuevaDireccion.isEmpty() || nuevoTelefono.isEmpty()) {
                Toast.makeText(DatosSubirActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            //Validar que el teléfono tenga exactamente 10 dígitos numéricos
            if (!nuevoTelefono.matches("\\d{10}")) {
                Toast.makeText(DatosSubirActivity.this, "El teléfono debe ser de 10 dígitos numéricos", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2:8000/api/datosUsuario";

            JSONObject postData = new JSONObject();
            try {
                //Preparar JSON con los datos para enviar al servidor
                postData.put("direccion", nuevaDireccion);
                postData.put("num_telefonico", nuevoTelefono);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(DatosSubirActivity.this, "Error al preparar datos", Toast.LENGTH_SHORT).show();
                return;
            }

            //Crear la solicitud POST para enviar datos a la API
            JsonObjectRequest postRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    postData,
                    response -> {
                        //Mostrar mensaje de éxito y volver a DatosActivity
                        Toast.makeText(DatosSubirActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DatosSubirActivity.this, DatosActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        //En caso de error también volver a DatosActivity
                        Intent intent = new Intent(DatosSubirActivity.this, DatosActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
            ) {
                //Añadir encabezados para autorización y tipo de contenido JSON
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            //Evitar reintentos automáticos para prevenir solicitudes duplicadas
            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            //Añadir la solicitud a la cola de Volley para ejecutarla
            Volley.newRequestQueue(DatosSubirActivity.this).add(postRequest);
        });
    }
}
