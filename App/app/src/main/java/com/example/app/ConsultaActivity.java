package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ConsultaActivity extends Activity {

    GridLayout contenedorConsultas;
    EditText busquedaConsultas;
    RequestQueue requestQueue;
    DBHelper dbHelper;
    String token;
    int idRol;
    JSONArray consultasGlobal = new JSONArray();
    final String apiUrl = "http://10.0.2.2:8000/api/consultas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);
        token = dbHelper.obtenerToken();
        idRol = dbHelper.obtenerRol();
        dbHelper.close();

        setContentView(R.layout.consulta_activity);

        contenedorConsultas = findViewById(R.id.contenedorConsultas);
        contenedorConsultas.setColumnCount(1);
        busquedaConsultas = findViewById(R.id.busquedaConsultas); // Nuevo campo de búsqueda

        // Botones comunes
        Button btnCita = findViewById(R.id.cita);
        Button btnNotificacion = findViewById(R.id.notificacion);
        Button btnUsuario = findViewById(R.id.usuario);

        btnCita.setOnClickListener(v -> startActivity(new Intent(this, CitaActivity.class)));
        btnNotificacion.setOnClickListener(v -> startActivity(new Intent(this, NotificacionActivity.class)));
        btnUsuario.setOnClickListener(v -> startActivity(new Intent(this, DatosActivity.class)));

        busquedaConsultas.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarConsultas(s.toString().trim());
            }
        });

        cargarConsultas();
    }

    private void cargarConsultas() {
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                apiUrl,
                null,
                response -> {
                    consultasGlobal = response;
                    mostrarConsultas(response);
                },
                error -> {
                    Toast.makeText(this, "Error al obtener consultas", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void mostrarConsultas(JSONArray consultas) {
        contenedorConsultas.removeAllViews();

        try {
            for (int i = 0; i < consultas.length(); i++) {
                JSONObject consulta = consultas.getJSONObject(i);
                agregarConsultaAlLayout(consulta);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void filtrarConsultas(String texto) {
        try {
            JSONArray filtradas = new JSONArray();
            for (int i = 0; i < consultasGlobal.length(); i++) {
                JSONObject consulta = consultasGlobal.getJSONObject(i);
                JSONObject mascota = consulta.getJSONObject("mascota");
                String nombreMascota = mascota.getString("nombre");
                if (nombreMascota.toLowerCase().contains(texto.toLowerCase())) {
                    filtradas.put(consulta);
                }
            }
            mostrarConsultas(filtradas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void agregarConsultaAlLayout(JSONObject consulta) {
        try {
            LinearLayout contenedor = new LinearLayout(this);
            contenedor.setOrientation(LinearLayout.VERTICAL);
            contenedor.setPadding(40, 40, 40, 40);
            contenedor.setBackgroundResource(R.drawable.borde_catalogo);
            contenedor.setGravity(Gravity.CENTER);

            JSONObject mascota = consulta.getJSONObject("mascota");
            String nombreMascota = mascota.getString("nombre");
            String fecha = consulta.getString("fecha");
            String motivo = consulta.getString("motivo");
            String diagnostico = consulta.getString("diagnostico");
            String tratamiento = consulta.getString("tratamiento");

            TextView txt = new TextView(this);
            String info = "Mascota: " + nombreMascota +
                    "\nFecha: " + fecha +
                    "\n\nMotivo: " + motivo +
                    "\n\nDiagnóstico: " + diagnostico +
                    "\n\nTratamiento: " + tratamiento;

            txt.setText(info);
            txt.setTextSize(22);
            txt.setTextColor(getColor(android.R.color.black));
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(0, 0, 0, 50);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                txt.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }

            contenedor.addView(txt);

            // Botones editar y eliminar
            LinearLayout botonesLayout = new LinearLayout(this);
            botonesLayout.setOrientation(LinearLayout.HORIZONTAL);
            botonesLayout.setGravity(Gravity.CENTER);
            botonesLayout.setPadding(0, 10, 0, 0);

            Button btnEditar = new Button(this);
            btnEditar.setBackgroundResource(R.drawable.editar);
            LinearLayout.LayoutParams paramsEditar = new LinearLayout.LayoutParams(150, 150);
            paramsEditar.setMargins(30, 0, 30, 0);
            btnEditar.setLayoutParams(paramsEditar);
            btnEditar.setText("");
            btnEditar.setOnClickListener(v -> editarConsulta(consulta));

            Button btnEliminar = new Button(this);
            btnEliminar.setBackgroundResource(R.drawable.eliminar);
            LinearLayout.LayoutParams paramsEliminar = new LinearLayout.LayoutParams(150, 150);
            paramsEliminar.setMargins(30, 0, 30, 0);
            btnEliminar.setLayoutParams(paramsEliminar);
            btnEliminar.setText("");
            btnEliminar.setOnClickListener(v -> eliminarConsulta(consulta));

            botonesLayout.addView(btnEditar);
            botonesLayout.addView(btnEliminar);

            contenedor.addView(botonesLayout);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.MATCH_PARENT;
            params.setMargins(60, 30, 60, 30);
            contenedor.setLayoutParams(params);

            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            contenedor.startAnimation(anim);

            contenedorConsultas.addView(contenedor);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void editarConsulta(JSONObject consulta) {
        Intent intent = new Intent(this, ConsultaEditarActivity.class);
        intent.putExtra("consulta", consulta.toString());
        startActivity(intent);
    }

    private void eliminarConsulta(JSONObject consulta) {
        try {
            int idConsulta = consulta.getInt("id_consulta");
            String urlEliminar = apiUrl + "/" + idConsulta;

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest request = new StringRequest(
                    Request.Method.DELETE,
                    urlEliminar,
                    response -> {
                        Toast.makeText(this, "Consulta eliminada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ConsultaActivity.this, ConsultaActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        Intent intent = new Intent(ConsultaActivity.this, ConsultaActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
