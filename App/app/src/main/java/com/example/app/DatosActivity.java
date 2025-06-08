package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class DatosActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtener el id_rol del usuario de la bd SQLITE
        DBHelper dbHelper = new DBHelper(this);
        int idRol = dbHelper.obtenerRol();
        dbHelper.close();

        //Cargar el layout correspondiente según el rol del usuario logeado recuperado de la bd
        if (idRol == 2) {
            setContentView(R.layout.datos_veterinario_activity); //Si el rol es de veterinario mandar al xml de veterinario

            //Botones y redirecciones a distintas vistas
            Button btnConsulta = findViewById(R.id.consulta);

            btnConsulta.setOnClickListener(view -> startActivity(new Intent(DatosActivity.this, ConsultaActivity.class)));

        } else if (idRol == 3) {
            setContentView(R.layout.datos_usuario_activity); //Si el rol es de usuario mandar al xml de usuario

            //Botones y redirecciones a distintas vistas
            Button btnCatalogo = findViewById(R.id.catalogo);
            Button btnMascota = findViewById(R.id.mascota);

            btnCatalogo.setOnClickListener(view -> startActivity(new Intent(DatosActivity.this, CatalogoActivity.class)));
            btnMascota.setOnClickListener(view -> startActivity(new Intent(DatosActivity.this, MascotaActivity.class)));

        }
        //Botones y redirecciones a distintas vistas
        Button btnCita = findViewById(R.id.cita);
        Button btnNotificacion = findViewById(R.id.notificacion);

        btnCita.setOnClickListener(view -> startActivity(new Intent(DatosActivity.this, CitaActivity.class)));
        btnNotificacion.setOnClickListener(view -> startActivity(new Intent(DatosActivity.this, NotificacionActivity.class)));
    }
}
