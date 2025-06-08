package com.example.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DBHelper extends SQLiteOpenHelper {

    //Nombre y versión de la base de datos
    private static final String DATABASE_NAME = "SesionDB";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Crear tabla de sesión con id, rol y token
        db.execSQL("CREATE TABLE sesion (id INTEGER PRIMARY KEY, id_rol INTEGER, token TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Recrear la tabla si se actualiza la base de datos
        db.execSQL("DROP TABLE IF EXISTS sesion");
        onCreate(db);
    }

    //Guarda la sesión completa con rol y token
    public void guardarSesion(int idRol, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM sesion"); //Solo una sesión activa
        ContentValues values = new ContentValues();
        values.put("id_rol", idRol);
        values.put("token", token);
        db.insert("sesion", null, values);
        db.close();
    }

    //Obtiene el rol almacenado, o -1 si no hay sesión
    public int obtenerRol() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_rol FROM sesion LIMIT 1", null);
        int idRol = -1;
        if (cursor.moveToFirst()) {
            idRol = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return idRol;
    }

    //Obtiene el token almacenado, o null si no hay
    public String obtenerToken() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT token FROM sesion LIMIT 1", null);
        String token = null;
        if (cursor.moveToFirst()) {
            token = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return token;
    }

    //Elimina la sesión guardada
    public void limpiarSesion() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM sesion");
        db.close();
    }
}
