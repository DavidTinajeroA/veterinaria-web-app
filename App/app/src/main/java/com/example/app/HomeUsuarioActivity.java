package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeUsuarioActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_usuario_activity);

        //Botones y redirecciones a distintas vistas
        Button btnCatalogo = findViewById(R.id.catalogo);
        Button btnCita = findViewById(R.id.cita);
        Button btnMascota = findViewById(R.id.mascota);
        Button btnNotificacion = findViewById(R.id.notificacion);
        Button btnUsuario = findViewById(R.id.usuario);

        btnCatalogo.setOnClickListener(view -> startActivity(new Intent(HomeUsuarioActivity.this, CatalogoActivity.class)));
        btnCita.setOnClickListener(view -> startActivity(new Intent(HomeUsuarioActivity.this, CitaActivity.class)));
        btnMascota.setOnClickListener(view -> startActivity(new Intent(HomeUsuarioActivity.this, MascotaActivity.class)));
        btnNotificacion.setOnClickListener(view -> startActivity(new Intent(HomeUsuarioActivity.this, NotificacionActivity.class)));
        btnUsuario.setOnClickListener(view -> startActivity(new Intent(HomeUsuarioActivity.this, DatosActivity.class)));
    }
}
