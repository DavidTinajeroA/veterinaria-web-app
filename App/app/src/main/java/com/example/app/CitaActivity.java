package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CitaActivity extends Activity {
    GridLayout layoutCitas;
    //Token de autenticación obtenido de la BD
    String token;
    int idRol;
    final String apiUrl = "http://10.0.2.2:8000/api/citas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener token y rol del usuario desde SQLite
        DBHelper dbHelper = new DBHelper(this);
        token = dbHelper.obtenerToken();
        idRol = dbHelper.obtenerRol();
        dbHelper.close();

        //Cargar layout diferente según el rol del usuario
        if (idRol == 2) {
            setContentView(R.layout.cita_veterinario_activity);
        } else {
            setContentView(R.layout.cita_usuario_activity);
        }

        //Inicializar el GridLayout que contendrá las citas
        layoutCitas = findViewById(R.id.contenedorCitas);
        layoutCitas.setColumnCount(1);

        //Configurar botones comunes y sus redirecciones
        Button btnUsuario = findViewById(R.id.usuario);
        Button btnNotificacion = findViewById(R.id.notificacion);

        btnUsuario.setOnClickListener(view->startActivity(new Intent(CitaActivity.this, DatosActivity.class)));
        btnNotificacion.setOnClickListener(view->startActivity(new Intent(CitaActivity.this, NotificacionActivity.class)));

        //Configurar botones específicos según el rol
        if (idRol == 2) {
            Button btnConsulta = findViewById(R.id.consulta);
            btnConsulta.setOnClickListener(view->startActivity(new Intent(CitaActivity.this, ConsultaActivity.class)));

        } else if (idRol == 3) {
            Button btnCatalogo = findViewById(R.id.catalogo);
            Button btnMascota = findViewById(R.id.mascota);

            btnCatalogo.setOnClickListener(view->startActivity(new Intent(CitaActivity.this, CatalogoActivity.class)));
            btnMascota.setOnClickListener(view->startActivity(new Intent(CitaActivity.this, MascotaActivity.class)));
        }

        cargarCitas();
    }

    //Realiza la petición GET para obtener las citas desde la API
    private void cargarCitas() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                apiUrl,
                null,
                this::mostrarCitas,
                error->Toast.makeText(this,"Error al obtener citas",Toast.LENGTH_SHORT).show()
        ){
            //Agregar encabezado Authorization con el token Bearer
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> headers=new HashMap<>();
                headers.put("Authorization","Bearer "+token);
                return headers;
            }
        };

        queue.add(request);
    }

    //Procesa y muestra la lista de citas recibidas en formato JSON
    private void mostrarCitas(JSONArray citas) {
        //Limpiar vistas previas antes de mostrar nuevas citas
        layoutCitas.removeAllViews();

        try{
            //Recorrer cada cita y agregarla al layout
            for(int i=0;i<citas.length();i++){
                JSONObject cita=citas.getJSONObject(i);
                agregarCitaAlLayout(cita);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Crea y agrega una vista con la información de una cita al layout principal
    private void agregarCitaAlLayout(JSONObject cita){
        try{
            //Crear contenedor vertical para la cita
            LinearLayout contenedor=new LinearLayout(this);
            contenedor.setOrientation(LinearLayout.VERTICAL);
            contenedor.setPadding(40,40,40,40);
            contenedor.setBackgroundResource(R.drawable.borde_catalogo);
            contenedor.setGravity(Gravity.CENTER);

            //Extraer datos de la cita
            String fecha=cita.getString("fecha");
            String nombreMascota=cita.getJSONObject("mascota").getString("nombre");
            String nombreUsuario=cita.getJSONObject("usuario").getString("nombre");

            //Crear TextView para mostrar los datos de la cita
            TextView txt=new TextView(this);
            txt.setText("Fecha: "+fecha+
                    "\nMascota: "+nombreMascota+
                    "\nUsuario: "+nombreUsuario);
            txt.setTextSize(18);
            txt.setTextColor(getColor(android.R.color.black));
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(0,0,0,20);

            //Ajustar tamaño de texto automáticamente en versiones recientes
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                txt.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }

            contenedor.addView(txt);

            //Si el rol es veterinario, agregar botones de editar y eliminar
            if(idRol==2){
                LinearLayout botonesLayout=new LinearLayout(this);
                botonesLayout.setOrientation(LinearLayout.HORIZONTAL);
                botonesLayout.setGravity(Gravity.CENTER);
                botonesLayout.setPadding(0,10,0,0);

                Button btnEditar=new Button(this);
                btnEditar.setBackgroundResource(R.drawable.editar);
                LinearLayout.LayoutParams paramsEditar=new LinearLayout.LayoutParams(150,150);
                paramsEditar.setMargins(30,0,30,0);
                btnEditar.setLayoutParams(paramsEditar);
                btnEditar.setText("");
                btnEditar.setOnClickListener(v->editarCita(cita));

                Button btnEliminar=new Button(this);
                btnEliminar.setBackgroundResource(R.drawable.eliminar);
                LinearLayout.LayoutParams paramsEliminar=new LinearLayout.LayoutParams(150,150);
                paramsEliminar.setMargins(30,0,30,0);
                btnEliminar.setLayoutParams(paramsEliminar);
                btnEliminar.setText("");
                btnEliminar.setOnClickListener(v->eliminarCita(cita));

                botonesLayout.addView(btnEditar);
                botonesLayout.addView(btnEliminar);

                contenedor.addView(botonesLayout);
            }

            //Definir parámetros de layout para el contenedor
            GridLayout.LayoutParams params=new GridLayout.LayoutParams();
            params.width=GridLayout.LayoutParams.MATCH_PARENT;
            params.setMargins(60,20,60,20);
            contenedor.setLayoutParams(params);

            //Aplicar animación de aparición
            Animation anim=AnimationUtils.loadAnimation(this,R.anim.fade_in);
            contenedor.startAnimation(anim);

            layoutCitas.addView(contenedor);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //Redirige a la actividad para editar la cita, enviando los datos en un extra
    private void editarCita(JSONObject cita){
        Intent intent=new Intent(this,CitaEditarActivity.class);
        intent.putExtra("cita",cita.toString());
        startActivity(intent);
    }

    //Elimina la cita haciendo una petición DELETE a la API
    private void eliminarCita(JSONObject cita){
        try{
            //Obtener id de la cita a eliminar
            int idCita=cita.getInt("id_cita");
            String urlEliminar=apiUrl+"/"+idCita;

            RequestQueue queue=Volley.newRequestQueue(this);

            StringRequest request=new StringRequest(
                    Request.Method.DELETE,
                    urlEliminar,
                    response->{
                        Toast.makeText(this,"Cita eliminada",Toast.LENGTH_SHORT).show();

                        //Refrescar la actividad para actualizar la lista
                        Intent intent=new Intent(CitaActivity.this,CitaActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    },
                    error->{
                        //En caso de error también refrescar la actividad
                        Intent intent=new Intent(CitaActivity.this,CitaActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
            ){
                //Agregar encabezado Authorization con el token Bearer
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError{
                    Map<String,String> headers=new HashMap<>();
                    headers.put("Authorization","Bearer "+token);
                    return headers;
                }
            };

            //Enviar petición DELETE
            queue.add(request);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
