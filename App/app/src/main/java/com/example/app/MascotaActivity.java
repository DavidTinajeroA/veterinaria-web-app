package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MascotaActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mascota_activity);

        //Botones y redirecciones a distintas vistas
        Button btnCatalogo = findViewById(R.id.catalogo);
        Button btnCita = findViewById(R.id.cita);
        Button btnNotificacion = findViewById(R.id.notificacion);
        Button btnUsuario = findViewById(R.id.usuario);

        btnCatalogo.setOnClickListener(view -> startActivity(new Intent(MascotaActivity.this, CatalogoActivity.class)));
        btnCita.setOnClickListener(view -> startActivity(new Intent(MascotaActivity.this, CitaActivity.class)));
        btnNotificacion.setOnClickListener(view -> startActivity(new Intent(MascotaActivity.this, NotificacionActivity.class)));
        btnUsuario.setOnClickListener(view -> startActivity(new Intent(MascotaActivity.this, DatosActivity.class)));
    }
}
