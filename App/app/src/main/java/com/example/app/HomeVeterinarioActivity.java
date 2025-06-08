package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeVeterinarioActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_veterinario_activity);

        //Botones y redirecciones a distintas vistas
        Button btnCita = findViewById(R.id.cita);
        Button btnConsulta = findViewById(R.id.consulta);
        Button btnNotificacion = findViewById(R.id.notificacion);
        Button btnUsuario = findViewById(R.id.usuario);

        btnCita.setOnClickListener(view -> startActivity(new Intent(HomeVeterinarioActivity.this, CitaActivity.class)));
        btnConsulta.setOnClickListener(view -> startActivity(new Intent(HomeVeterinarioActivity.this, ConsultaActivity.class)));
        btnNotificacion.setOnClickListener(view -> startActivity(new Intent(HomeVeterinarioActivity.this, NotificacionActivity.class)));
        btnUsuario.setOnClickListener(view -> startActivity(new Intent(HomeVeterinarioActivity.this, DatosActivity.class)));
    }
}
