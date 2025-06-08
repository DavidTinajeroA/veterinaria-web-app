package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CatalogoActivity extends Activity {

    //Elementos visuales y datos
    GridLayout gridCatalogo; //Contenedor para mostrar productos en formato grid
    EditText busqueda; //Campo de texto para filtrar productos
    String token; //Token de autenticación del usuario
    JSONArray productosGlobal = new JSONArray(); //Lista completa de productos cargados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalogo_activity);

        //Obtener el rol y token desde SQLite
        DBHelper dbHelper = new DBHelper(this);
        int rol = dbHelper.obtenerRol();
        token = dbHelper.obtenerToken();

        //Verificar que haya sesión válida
        if ((rol != 2 && rol != 3) || token == null || token.isEmpty()) {
            startActivity(new Intent(CatalogoActivity.this, MainActivity.class));
            finish();
            return;
        }

        //Inicializar elementos visuales
        gridCatalogo = findViewById(R.id.gridCatalogo);
        busqueda = findViewById(R.id.busqueda);

        //Cargar el catálogo desde la API
        cargarCatalogo();

        //Filtrar catálogo al escribir
        busqueda.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProductos(s.toString().trim());
            }
        });

        //Botones de navegación entre secciones
        findViewById(R.id.cita).setOnClickListener(v -> startActivity(new Intent(this, CitaActivity.class)));
        findViewById(R.id.mascota).setOnClickListener(v -> startActivity(new Intent(this, MascotaActivity.class)));
        findViewById(R.id.notificacion).setOnClickListener(v -> startActivity(new Intent(this, NotificacionActivity.class)));
        findViewById(R.id.usuario).setOnClickListener(v -> startActivity(new Intent(this, DatosActivity.class)));
    }

    //Llama a la API para obtener la lista de productos
    private void cargarCatalogo() {
        String url = "http://10.0.2.2:8000/api/catalogo";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    productosGlobal = response; //Guardar productos globalmente
                    mostrarProductos(response); //Mostrar productos cargados
                },
                Throwable::printStackTrace
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //Agregar encabezado de autorización con el token
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }

    //Muestra productos en el GridLayout
    private void mostrarProductos(JSONArray productos) {
        gridCatalogo.removeAllViews(); //Limpiar contenido anterior
        try {
            for (int i = 0; i < productos.length(); i++) {
                JSONObject prod = productos.getJSONObject(i);
                agregarProductoAlGrid(prod); //Agregar cada producto al grid
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Filtra productos según texto de búsqueda
    private void filtrarProductos(String texto) {
        try {
            JSONArray filtrados = new JSONArray();
            for (int i = 0; i < productosGlobal.length(); i++) {
                JSONObject prod = productosGlobal.getJSONObject(i);
                if (prod.getString("nombre").toLowerCase().contains(texto.toLowerCase())) {
                    filtrados.put(prod); //Agregar producto si coincide con búsqueda
                }
            }
            mostrarProductos(filtrados); //Mostrar productos filtrados
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Agrega visualmente un producto al grid
    private void agregarProductoAlGrid(JSONObject prod) {
        try {
            //Obtener datos del producto
            String nombre = prod.getString("nombre");
            double precio = prod.getDouble("precio");

            //Contenedor vertical principal
            LinearLayout contenedor = new LinearLayout(this);
            contenedor.setOrientation(LinearLayout.VERTICAL);
            contenedor.setPadding(20, 20, 20, 20);
            contenedor.setGravity(Gravity.CENTER);

            //Contenedor con fondo y diseño del producto
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setBackgroundResource(R.drawable.borde_catalogo);
            itemLayout.setPadding(20, 20, 20, 20);
            itemLayout.setGravity(Gravity.CENTER);

            //Texto con nombre del producto
            TextView nombreText = new TextView(this);
            nombreText.setText(nombre);
            nombreText.setTextSize(22);
            nombreText.setMaxLines(1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nombreText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }
            nombreText.setTextColor(getColor(android.R.color.black));
            nombreText.setGravity(Gravity.CENTER);
            nombreText.setPadding(0, 0, 0, 8);

            //Imagen genérica del producto
            ImageView imagen = new ImageView(this);
            imagen.setImageResource(R.drawable.imagen);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(300, 300);
            imagen.setLayoutParams(imgParams);
            imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);

            //Texto con precio del producto
            TextView precioText = new TextView(this);
            precioText.setText("$" + precio);
            precioText.setTextSize(20);
            precioText.setTextColor(getColor(android.R.color.black));
            precioText.setGravity(Gravity.CENTER);
            precioText.setPadding(0, 8, 0, 0);

            //Agregar elementos al layout del producto
            itemLayout.addView(nombreText);
            itemLayout.addView(imagen);
            itemLayout.addView(precioText);

            //Botón para agregar al carrito (diseño visual)
            Button btnAgregar = new Button(this);
            btnAgregar.setText("+");
            btnAgregar.setBackgroundColor(Color.parseColor("#FFEA00"));
            btnAgregar.setTextSize(18);
            btnAgregar.setPadding(10, 0, 10, 0);
            btnAgregar.setGravity(Gravity.CENTER);

            //Parámetros del botón
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    90
            );
            btnParams.topMargin = 35;
            btnParams.gravity = Gravity.CENTER;
            btnAgregar.setLayoutParams(btnParams);

            //Agregar layout y botón al contenedor general
            contenedor.addView(itemLayout);
            contenedor.addView(btnAgregar);

            //Parámetros del contenedor para adaptarse al grid
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = getResources().getDisplayMetrics().widthPixels / 2 -165;
            params.height = GridLayout.LayoutParams.MATCH_PARENT;
            params.setMargins(30, 0, 30, 0);
            contenedor.setLayoutParams(params);

            //Animación de entrada
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            contenedor.startAnimation(anim);

            //Agregar producto al grid
            gridCatalogo.addView(contenedor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
