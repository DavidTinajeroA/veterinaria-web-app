package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConsultaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consulta_activity);

        //Botones y redirecciones a distintas vistas
        Button btnCita = findViewById(R.id.cita);
        Button btnNotificacion = findViewById(R.id.notificacion);
        Button btnUsuario = findViewById(R.id.usuario);

        btnCita.setOnClickListener(view -> startActivity(new Intent(ConsultaActivity.this, CitaActivity.class)));
        btnNotificacion.setOnClickListener(view -> startActivity(new Intent(ConsultaActivity.this, NotificacionActivity.class)));
        btnUsuario.setOnClickListener(view -> startActivity(new Intent(ConsultaActivity.this, DatosActivity.class)));
    }
}
