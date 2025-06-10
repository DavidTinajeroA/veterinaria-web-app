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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MascotaAgregarActivity extends Activity {

    private boolean solicitudEnviada = false;
    EditText nombre, especie, raza, edad, peso;
    String token;
    Button btnAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mascota_agregar_activity);

        //Obtener token desde SQLite
        DBHelper dbHelper = new DBHelper(this);
        token = dbHelper.obtenerToken();
        dbHelper.close();

        //Asociar campos del formulario
        nombre = findViewById(R.id.nombre);
        especie = findViewById(R.id.especie);
        raza = findViewById(R.id.raza);
        edad = findViewById(R.id.edad);
        peso = findViewById(R.id.peso);

        btnAgregar = findViewById(R.id.btnAgregarMascota);
        btnAgregar.setOnClickListener(v -> agregarMascota());

        //Botón para salir
        Button btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(v -> finish());
    }

    private void agregarMascota() {
        //Solicitud POST para agregar una mascota
        String url = "http://10.0.2.2:8000/api/mascotas";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(this, "Mascota agregada", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MascotaAgregarActivity.this, MascotaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Error al agregar mascota", Toast.LENGTH_SHORT).show();
                }
        ) {
            //Parametros a enviar
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre.getText().toString());
                params.put("especie", especie.getText().toString());
                params.put("raza", raza.getText().toString());
                params.put("edad", edad.getText().toString());
                params.put("peso", peso.getText().toString());
                return params;
            }

            //Encabezado de la solicitud HTTP
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        //Evita reintentos automáticos de Volley
        request.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(request);
    }
}