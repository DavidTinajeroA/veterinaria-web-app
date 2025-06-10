package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class DatosActivity extends Activity {

    EditText Direccion, Telefono;
    Button btnEditar;
    String token;
    int idRol;
    int idDatosUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener token y rol del usuario desde la base local SQLite
        DBHelper dbHelper = new DBHelper(this);
        token = dbHelper.obtenerToken();
        idRol = dbHelper.obtenerRol();
        dbHelper.close();

        //Seleccionar layout según rol (veterinario o usuario)
        if (idRol == 2) {
            setContentView(R.layout.datos_veterinario_activity);
        } else if (idRol == 3) {
            setContentView(R.layout.datos_usuario_activity);
        }

        //Vincular las vistas comunes del layout
        Direccion = findViewById(R.id.direccion);
        Telefono = findViewById(R.id.telefono);
        btnEditar = findViewById(R.id.btnEditarUsuario);

        //Configurar botones de navegación comunes a todos los roles
        Button btnCita = findViewById(R.id.cita);
        Button btnNotificacion = findViewById(R.id.notificacion);
        btnCita.setOnClickListener(view -> startActivity(new Intent(DatosActivity.this, CitaActivity.class)));
        btnNotificacion.setOnClickListener(view -> startActivity(new Intent(DatosActivity.this, NotificacionActivity.class)));

        //Configurar botones específicos según rol
        if (idRol == 2) {
            Button btnConsulta = findViewById(R.id.consulta);
            btnConsulta.setOnClickListener(view -> startActivity(new Intent(DatosActivity.this, ConsultaActivity.class)));
        } else if (idRol == 3) {
            Button btnCatalogo = findViewById(R.id.catalogo);
            Button btnMascota = findViewById(R.id.mascota);
            btnCatalogo.setOnClickListener(view -> startActivity(new Intent(DatosActivity.this, CatalogoActivity.class)));
            btnMascota.setOnClickListener(view -> startActivity(new Intent(DatosActivity.this, MascotaActivity.class)));
        }

        //Listener para botón editar que hace PUT para actualizar datos
        btnEditar.setOnClickListener(v -> {
            String nuevaDireccion = Direccion.getText().toString().trim();
            String nuevoTelefono = Telefono.getText().toString().trim();

            //Validar que los campos no estén vacíos
            if (nuevaDireccion.isEmpty() || nuevoTelefono.isEmpty()) {
                Toast.makeText(DatosActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            //URL de la API para actualizar datos del usuario según idDatosUsuario
            String url = "http://10.0.2.2:8000/api/datosUsuario/" + idDatosUsuario;

            //Preparar objeto JSON con los datos a actualizar
            JSONObject putData = new JSONObject();
            try {
                putData.put("direccion", nuevaDireccion);
                putData.put("num_telefonico", nuevoTelefono);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(DatosActivity.this, "Error al preparar datos", Toast.LENGTH_SHORT).show();
                return;
            }

            //Crear solicitud PUT con autorización y JSON en body
            com.android.volley.toolbox.JsonObjectRequest putRequest = new com.android.volley.toolbox.JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    putData,
                    response -> {
                        //En caso de éxito, mostrar mensaje y recargar la actividad
                        Toast.makeText(DatosActivity.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DatosActivity.this, DatosActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        //En caso de error, recargar actividad para intentar de nuevo
                        Intent intent = new Intent(DatosActivity.this, DatosActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    //Añadir headers para autorización y tipo de contenido JSON
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            //Encolar la petición PUT en la cola de Volley
            Volley.newRequestQueue(DatosActivity.this).add(putRequest);
        });

        //Cargar datos actuales del usuario para mostrar en la UI
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        String url = "http://10.0.2.2:8000/api/datosUsuario";

        //Solicitud GET para obtener datos del usuario
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    if (response.length() == 0) {
                        //Si no hay datos, redirigir a la actividad para crear datos nuevos
                        Intent intent = new Intent(DatosActivity.this, DatosSubirActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            //Obtener primer objeto JSON con los datos del usuario
                            JSONObject datos = response.getJSONObject(0);
                            //Guardar id para futuras actualizaciones
                            idDatosUsuario = datos.getInt("id_datosUsuario");
                            //Mostrar datos en los EditText
                            Direccion.setText(datos.getString("direccion"));
                            Telefono.setText(datos.getString("num_telefonico"));
                            //Mostrar botón editar
                            btnEditar.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(DatosActivity.this, "Error al parsear datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    //Mostrar mensaje si hay error al obtener datos
                    Toast.makeText(DatosActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //Añadir token de autorización en headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        //Encolar la petición GET en la cola de Volley
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }
}
