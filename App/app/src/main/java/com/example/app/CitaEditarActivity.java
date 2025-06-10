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

public class CitaEditarActivity extends Activity {

    EditText etFechaHora, etUsuarioNombre, etMascotaNombre;
    String token;
    int idCita;
    int idUsuario;
    int idMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cita_editar_activity);

        //Referencias a los EditText del layout
        etFechaHora=findViewById(R.id.fecha);
        etUsuarioNombre=findViewById(R.id.usuario);
        etMascotaNombre=findViewById(R.id.mascota);

        //Usuario y mascota son solo para mostrar, no editables
        etUsuarioNombre.setEnabled(false);
        etMascotaNombre.setEnabled(false);

        //EditText fecha y hora no editable manualmente, solo por selector
        etFechaHora.setFocusable(false);
        etFechaHora.setClickable(true);
        etFechaHora.setOnClickListener(v->mostrarSelectorFechaHora());

        //Obtener token de SQLite para autorización
        DBHelper dbHelper=new DBHelper(this);
        token=dbHelper.obtenerToken();
        dbHelper.close();

        //Obtener datos de la cita enviados en JSON desde actividad anterior
        Intent intent=getIntent();
        String citaJsonString=intent.getStringExtra("cita");
        try{
            JSONObject cita=new JSONObject(citaJsonString);
            idCita=cita.getInt("id_cita");

            //Guardar IDs para enviar al actualizar cita
            idUsuario=cita.getJSONObject("usuario").getInt("id_usuario");
            idMascota=cita.getJSONObject("mascota").getInt("id_mascota");

            //Mostrar fecha y hora actual de la cita en el EditText
            String fechaHora=cita.getString("fecha"); //formato "YYYY-MM-DD HH:mm:ss"
            etFechaHora.setText(fechaHora);

            //Mostrar nombres de usuario y mascota en EditTexts correspondientes
            String nombreUsuario=cita.getJSONObject("usuario").getString("nombre");
            String nombreMascota=cita.getJSONObject("mascota").getString("nombre");
            etUsuarioNombre.setText(nombreUsuario);
            etMascotaNombre.setText(nombreMascota);

        }catch(Exception e){
            e.printStackTrace();
        }

        //Referencias a botones guardar y salir
        Button btnGuardar=findViewById(R.id.btnGuardar);
        Button btnSalir=findViewById(R.id.btnSalir);

        //Al guardar, enviar petición para actualizar la cita
        btnGuardar.setOnClickListener(v->actualizarCita());

        //Al salir, cerrar actividad sin guardar
        btnSalir.setOnClickListener(v->finish());
    }

    private void mostrarSelectorFechaHora(){
        final Calendar calendario=Calendar.getInstance();

        //Intentar cargar fecha y hora actuales del EditText para iniciar selector con esos valores
        try{
            String fechaHoraStr=etFechaHora.getText().toString();
            String[] partes=fechaHoraStr.split(" ");
            String[] fechaPartes=partes[0].split("-");

            int anio=Integer.parseInt(fechaPartes[0]);
            int mes=Integer.parseInt(fechaPartes[1])-1; //Mes base 0 para Calendar
            int dia=Integer.parseInt(fechaPartes[2]);

            int hora=0;
            int minuto=0;
            if(partes.length>1){
                String[] horaPartes=partes[1].split(":");
                hora=Integer.parseInt(horaPartes[0]);
                minuto=Integer.parseInt(horaPartes[1]);
            }

            calendario.set(anio,mes,dia,hora,minuto);
        }catch(Exception e){
            //Si falla parseo, usar fecha y hora actual del sistema
        }

        //Mostrar selector de fecha con valores iniciales
        int anio=calendario.get(Calendar.YEAR);
        int mes=calendario.get(Calendar.MONTH);
        int dia=calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog=new DatePickerDialog(this,(view,year,month,dayOfMonth)->{
            //Cuando se selecciona fecha, mostrar selector de hora
            int hora=calendario.get(Calendar.HOUR_OF_DAY);
            int minuto=calendario.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog=new TimePickerDialog(this,(timeView,selectedHour,selectedMinute)->{
                //Construir cadena fecha y hora seleccionadas para mostrar en EditText
                String fechaHoraSeleccionada=String.format("%04d-%02d-%02d %02d:%02d:00",
                        year,month+1,dayOfMonth,selectedHour,selectedMinute);
                etFechaHora.setText(fechaHoraSeleccionada);
            },hora,minuto,true);

            timePickerDialog.show();
        },anio,mes,dia);

        datePickerDialog.show();
    }

    private void actualizarCita(){
        //URL para actualizar cita con ID específico
        String url="http://10.0.2.2:8000/api/citas/"+idCita;

        //Petición POST con override para usar PUT (Laravel espera _method=PUT)
        StringRequest request=new StringRequest(
                Request.Method.POST,
                url,
                response->{
                    //Mostrar mensaje de éxito y volver a lista de citas
                    Toast.makeText(this,"Cita actualizada",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(CitaEditarActivity.this,CitaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error->{
                    //En caso de error, volver a lista sin mostrar mensaje
                    Intent intent=new Intent(CitaEditarActivity.this,CitaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
        ){
            @Override
            protected Map<String,String> getParams(){
                //Parámetros para la actualización: metodo PUT simulado y datos de la cita
                Map<String,String> params=new HashMap<>();
                params.put("_method","PUT"); //Laravel interpreta este para PUT
                params.put("fecha",etFechaHora.getText().toString());
                params.put("id_usuario",String.valueOf(idUsuario));
                params.put("id_mascota",String.valueOf(idMascota));
                return params;
            }

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                //Añadir token Bearer para autorización en headers
                Map<String,String> headers=new HashMap<>();
                headers.put("Authorization","Bearer "+token);
                return headers;
            }
        };

        //Encolar petición en la cola de Volley para ejecutarla
        Volley.newRequestQueue(this).add(request);
    }
}
