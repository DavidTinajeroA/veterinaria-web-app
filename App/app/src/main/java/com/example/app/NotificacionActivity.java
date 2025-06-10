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
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class NotificacionActivity extends Activity {

    GridLayout gridNotificaciones; //Contenedor visual en formato grid para mostrar notificaciones
    String token; //Token de autenticación del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener token y rol desde SQLite
        DBHelper dbHelper = new DBHelper(this);
        int idRol = dbHelper.obtenerRol();
        token = dbHelper.obtenerToken();
        dbHelper.close();

        //Cargar layout correspondiente al rol del usuario logeado
        if (idRol == 2) {
            setContentView(R.layout.notificacion_veterinario_activity);
        } else {
            setContentView(R.layout.notificacion_usuario_activity);
        }

        //Inicializar el GridLayout
        gridNotificaciones = findViewById(R.id.gridNotificaciones);
        gridNotificaciones.setColumnCount(1);

        //Cargar las notificaciones de la API
        cargarNotificaciones();

        //Botones recuperados del xml
        findViewById(R.id.cita).setOnClickListener(v -> startActivity(new Intent(this, CitaActivity.class)));
        findViewById(R.id.usuario).setOnClickListener(v -> startActivity(new Intent(this, DatosActivity.class)));

        if (idRol == 2) {
            findViewById(R.id.consulta).setOnClickListener(v -> startActivity(new Intent(this, ConsultaActivity.class)));
        } else {
            findViewById(R.id.catalogo).setOnClickListener(v -> startActivity(new Intent(this, CatalogoActivity.class)));
            findViewById(R.id.mascota).setOnClickListener(v -> startActivity(new Intent(this, MascotaActivity.class)));
        }
    }

    //Realiza la solicitud a la API para obtener las notificaciones
    private void cargarNotificaciones() {
        String url = "http://10.0.2.2:8000/api/notificaciones";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> mostrarNotificaciones(response),
                Throwable::printStackTrace
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //Agregar encabezado de autorización con el token del usuario logeado
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }

    //Itera sobre las notificaciones y las agrega visualmente al grid
    private void mostrarNotificaciones(JSONArray notificaciones) {
        gridNotificaciones.removeAllViews(); //Limpiar notificaciones anteriores

        try {
            for (int i = 0; i < notificaciones.length(); i++) {
                JSONObject noti = notificaciones.getJSONObject(i);
                agregarNotificacionAlGrid(noti); //Agregar cada notificación al grid
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Crear visualmente una notificación y agregarla al grid
    private void agregarNotificacionAlGrid(JSONObject noti) {
        try {
            //Obtener datos de la notificación
            String titulo = noti.getString("titulo");
            String mensaje = noti.getString("mensaje");

            //Contenedor vertical principal de las notificaciones
            LinearLayout contenedor = new LinearLayout(this);
            contenedor.setOrientation(LinearLayout.VERTICAL);
            contenedor.setPadding(20, 15, 20, 15);
            contenedor.setGravity(Gravity.CENTER);
            contenedor.setBackgroundResource(R.drawable.borde_notificacion);

            //Titulo de la notificación
            TextView tituloView = new TextView(this);
            tituloView.setText(titulo);
            tituloView.setTextSize(25);
            tituloView.setGravity(Gravity.CENTER);
            tituloView.setTextColor(getColor(android.R.color.black));
            tituloView.setPadding(0, 0, 0, 5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tituloView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }

            //Resto del texto de la notificación
            TextView mensajeView = new TextView(this);
            mensajeView.setText(mensaje);
            mensajeView.setTextSize(22);
            mensajeView.setTextColor(getColor(android.R.color.black));
            mensajeView.setGravity(Gravity.CENTER);

            //Agregar elementos al contenedor
            contenedor.addView(tituloView);
            contenedor.addView(mensajeView);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = 1200;
            params.width = GridLayout.LayoutParams.MATCH_PARENT;
            params.setMargins(100, 10, 100, 0);
            contenedor.setLayoutParams(params);

            //Agregar animaciones
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            contenedor.startAnimation(anim);

            gridNotificaciones.addView(contenedor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
