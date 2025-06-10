package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MascotaEditarActivity extends Activity {
    EditText etNombre, etEspecie, etRaza, etEdad, etPeso;
    String token;
    int idMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mascota_editar_activity);

        etNombre = findViewById(R.id.nombre);
        etEspecie = findViewById(R.id.especie);
        etRaza = findViewById(R.id.raza);
        etEdad = findViewById(R.id.edad);
        etPeso = findViewById(R.id.peso);

        //Recuperar token de la BD
        DBHelper dbHelper = new DBHelper(this);
        token = dbHelper.obtenerToken();
        dbHelper.close();

        //Recuperar datos desde el intent
        Intent intent = getIntent();
        idMascota = intent.getIntExtra("id_mascota", -1);
        etNombre.setText(intent.getStringExtra("nombre"));
        etEspecie.setText(intent.getStringExtra("especie"));
        etRaza.setText(intent.getStringExtra("raza"));
        etEdad.setText(String.valueOf(intent.getIntExtra("edad", 0)));
        etPeso.setText(String.valueOf(intent.getDoubleExtra("peso", 0)));

        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnSalir = findViewById(R.id.btnSalir);

        btnGuardar.setOnClickListener(v -> actualizarMascota());
        btnSalir.setOnClickListener(v -> finish());
    }

    private void actualizarMascota() {
        String url = "http://10.0.2.2:8000/api/mascotas/" + idMascota;
        StringRequest request = new StringRequest(
                Request.Method.POST,//POST en lugar de PUT para que funcione con Volley
                url,
                response -> {
                    Toast.makeText(this, "Mascota actualizada", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MascotaEditarActivity.this, MascotaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Intent intent = new Intent(MascotaEditarActivity.this, MascotaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
        ) {
            @Override
            //Parametros a enviar
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("_method", "PUT");
                params.put("nombre", etNombre.getText().toString());
                params.put("especie", etEspecie.getText().toString());
                params.put("raza", etRaza.getText().toString());
                params.put("edad", etEdad.getText().toString());
                params.put("peso", etPeso.getText().toString());
                return params;
            }

            //Encabezado para la solicitud
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
