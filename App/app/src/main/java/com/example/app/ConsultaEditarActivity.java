package com.example.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConsultaEditarActivity extends Activity {

    EditText FechaHora, Motivo, Diagnostico, Tratamiento, UsuarioNombre, MascotaNombre;
    String token;
    int idConsulta;
    int idMascota;
    int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consulta_editar_activity);

        FechaHora = findViewById(R.id.fecha);
        Motivo = findViewById(R.id.motivo);
        Diagnostico = findViewById(R.id.diagnostico);
        Tratamiento = findViewById(R.id.tratamiento);
        UsuarioNombre = findViewById(R.id.usuario);
        MascotaNombre = findViewById(R.id.mascota);

        UsuarioNombre.setEnabled(false);
        MascotaNombre.setEnabled(false);

        FechaHora.setFocusable(false);
        FechaHora.setClickable(true);
        FechaHora.setOnClickListener(v -> mostrarSelectorFechaHora());

        DBHelper dbHelper = new DBHelper(this);
        token = dbHelper.obtenerToken();
        dbHelper.close();

        Intent intent = getIntent();
        String consultaJsonString = intent.getStringExtra("consulta");

        try {
            JSONObject consulta = new JSONObject(consultaJsonString);
            idConsulta = consulta.getInt("id_consulta");

            JSONObject usuarioObj = consulta.getJSONObject("usuario");
            JSONObject mascotaObj = consulta.getJSONObject("mascota");

            idUsuario = usuarioObj.getInt("id_usuario");
            idMascota = mascotaObj.getInt("id_mascota");

            FechaHora.setText(consulta.getString("fecha"));
            Motivo.setText(consulta.getString("motivo"));
            Diagnostico.setText(consulta.getString("diagnostico"));
            Tratamiento.setText(consulta.getString("tratamiento"));
            UsuarioNombre.setText(usuarioObj.getString("nombre"));
            MascotaNombre.setText(mascotaObj.getString("nombre"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnSalir = findViewById(R.id.btnSalir);

        btnGuardar.setOnClickListener(v -> actualizarConsulta());
        btnSalir.setOnClickListener(v -> finish());
    }

    private void mostrarSelectorFechaHora() {
        final Calendar calendario = Calendar.getInstance();

        try {
            String fechaHoraStr = FechaHora.getText().toString();
            String[] partes = fechaHoraStr.split(" ");
            String[] fechaPartes = partes[0].split("-");

            int anio = Integer.parseInt(fechaPartes[0]);
            int mes = Integer.parseInt(fechaPartes[1]) - 1;
            int dia = Integer.parseInt(fechaPartes[2]);

            int hora = 0;
            int minuto = 0;
            if (partes.length > 1) {
                String[] horaPartes = partes[1].split(":");
                hora = Integer.parseInt(horaPartes[0]);
                minuto = Integer.parseInt(horaPartes[1]);
            }

            calendario.set(anio, mes, dia, hora, minuto);
        } catch (Exception e) {
            // Ignorar errores
        }

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            int hora = calendario.get(Calendar.HOUR_OF_DAY);
            int minuto = calendario.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timeView, selectedHour, selectedMinute) -> {
                String fechaHoraSeleccionada = String.format("%04d-%02d-%02d %02d:%02d:00",
                        year, month + 1, dayOfMonth, selectedHour, selectedMinute);
                FechaHora.setText(fechaHoraSeleccionada);
            }, hora, minuto, true);

            timePickerDialog.show();
        }, anio, mes, dia);

        datePickerDialog.show();
    }

    private void actualizarConsulta() {
        String url = "http://10.0.2.2:8000/api/consultas/" + idConsulta;

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(this, "Consulta actualizada", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ConsultaEditarActivity.this, ConsultaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Intent intent = new Intent(ConsultaEditarActivity.this, ConsultaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("_method", "PUT");
                params.put("fecha", FechaHora.getText().toString());
                params.put("motivo", Motivo.getText().toString());
                params.put("diagnostico", Diagnostico.getText().toString());
                params.put("tratamiento", Tratamiento.getText().toString());
                params.put("id_mascota", String.valueOf(idMascota));
                return params;
            }

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
