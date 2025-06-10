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
import android.widget.ImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MascotaActivity extends Activity {
    GridLayout layoutMascotas;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener token desde la base de datos local SQLite
        DBHelper dbHelper = new DBHelper(this);
        token = dbHelper.obtenerToken();
        dbHelper.close();

        setContentView(R.layout.mascota_activity);

        //Inicializar el GridLayout que contendrá las mascotas y establecer 1 columna
        layoutMascotas = findViewById(R.id.layoutMascotas);
        layoutMascotas.setColumnCount(1);

        //Cargar las mascotas desde la API
        cargarMascotas();

        //Inicializar botones de navegación y configurar sus acciones
        Button btnCatalogo = findViewById(R.id.catalogo);
        Button btnCita = findViewById(R.id.cita);
        Button btnNotificacion = findViewById(R.id.notificacion);
        Button btnUsuario = findViewById(R.id.usuario);

        btnCatalogo.setOnClickListener(view -> startActivity(new Intent(MascotaActivity.this, CatalogoActivity.class)));
        btnCita.setOnClickListener(view -> startActivity(new Intent(MascotaActivity.this, CitaActivity.class)));
        btnNotificacion.setOnClickListener(view -> startActivity(new Intent(MascotaActivity.this, NotificacionActivity.class)));
        btnUsuario.setOnClickListener(view -> startActivity(new Intent(MascotaActivity.this, DatosActivity.class)));

        //Botón para agregar nueva mascota y su redirección a la actividad correspondiente
        Button btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MascotaActivity.this, MascotaAgregarActivity.class);
            startActivity(intent);
        });
    }

    //Realiza petición GET para obtener la lista de mascotas desde la API
    private void cargarMascotas() {
        String url = "http://10.0.2.2:8000/api/mascotas";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> mostrarMascotas(response),
                Throwable::printStackTrace
        ) {
            //Agregar encabezado para autenticar petición
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }

    //Procesa el JSONArray recibido y agrega cada mascota al layout
    private void mostrarMascotas(JSONArray mascotas){

        layoutMascotas.removeAllViews();

        try {
            //Recorrer todas las mascotas del JSONArray
            for (int i = 0; i < mascotas.length(); i++){
                JSONObject mascota = mascotas.getJSONObject(i);
                agregarMascotaAlLayout(mascota);  //Agregar cada mascota al layout
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //Crea y agrega una vista con la información de una mascota al layout principal
    private void agregarMascotaAlLayout(JSONObject mascota){
        try{
            //Extraer datos individuales de la mascota
            String nombre = mascota.getString("nombre");
            String especie = mascota.getString("especie");
            String raza = mascota.getString("raza");
            Integer edad = mascota.getInt("edad");
            Double peso = mascota.getDouble("peso");

            //Crear un LinearLayout vertical para contener la información de la mascota
            LinearLayout contenedor = new LinearLayout(this);
            contenedor.setOrientation(LinearLayout.VERTICAL);
            contenedor.setPadding(40, 40, 40, 40);
            contenedor.setBackgroundResource(R.drawable.borde_catalogo);
            contenedor.setGravity(Gravity.CENTER_HORIZONTAL);

            //Definir parámetros de layout para el contenedor con márgenes
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
            layoutParams.setMargins(30, 30, 30, 30);
            contenedor.setLayoutParams(layoutParams);

            //Crear TextView para mostrar el nombre de la mascota con estilo
            TextView nombreView = new TextView(this);
            nombreView.setText(nombre);
            nombreView.setTextSize(25);
            nombreView.setGravity(Gravity.CENTER);
            nombreView.setTextColor(getColor(android.R.color.black));
            nombreView.setPadding(0, 0, 0, 15);

            //Ajustar tamaño automático del texto en versiones recientes de Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nombreView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }

            ImageView imagenView = new ImageView(this);
            imagenView.setImageResource(R.drawable.imagen);
            imagenView.setLayoutParams(new LinearLayout.LayoutParams(500, 400));
            imagenView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imagenView.setPadding(0, 0, 0, 20);

            TextView detallesView = new TextView(this);
            detallesView.setText("Especie: " + especie + "\nRaza: " + raza + "\nEdad: " + edad + " años\nPeso: " + peso + " kg");
            detallesView.setTextSize(22);
            detallesView.setTextColor(getColor(android.R.color.black));
            detallesView.setGravity(Gravity.CENTER);

            //Crear botón para editar la mascota dentro del contenedor
            Button btnEditar = new Button(this);
            btnEditar.setBackgroundResource(R.drawable.editar);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(150, 150);
            btnParams.topMargin = 20;
            btnParams.gravity = Gravity.CENTER;
            btnEditar.setLayoutParams(btnParams);

            btnEditar.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MascotaActivity.this, MascotaEditarActivity.class);
                    intent.putExtra("id_mascota", mascota.getInt("id_mascota"));
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("especie", especie);
                    intent.putExtra("raza", raza);
                    intent.putExtra("edad", edad);
                    intent.putExtra("peso", peso);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            //Agregar las vistas creadas al contenedor principal
            contenedor.addView(nombreView);
            contenedor.addView(imagenView);
            contenedor.addView(detallesView);
            contenedor.addView(btnEditar);

            //Aplicar animaciones
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            contenedor.startAnimation(anim);

            layoutMascotas.addView(contenedor);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
