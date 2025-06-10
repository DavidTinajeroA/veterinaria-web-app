<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Mascotas;

class MascotasController extends Controller
{
    //Retorna las mascotas registradas por el usuario logeado
    public function index()
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idUsuario = $usuario->id_usuario;

        //Recupera todos los registros donde el id del usuario esté relacionado
        return Mascotas::with('usuario')->where('id_usuario', $idUsuario)->get();
    }

    public function store(Request $request)
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idUsuario = $usuario->id_usuario;

        //Datos a cargar al crear una nueva mascota
        $validate = $request->validate([
            'nombre' => 'required|string',
            'especie' => 'required|string',
            'raza' => 'required|string',
            'edad' => 'required|numeric|min:0|max:50',
            'peso' => 'required|numeric|min:0|max:1000'
        ]);

        $mascota = Mascotas::create([
            'id_usuario' => $idUsuario,
            'nombre' => $validate['nombre'],
            'especie' => $validate['especie'],
            'raza' => $validate['raza'],
            'edad' => $validate['edad'],
            'peso' => $validate['peso'],
        ]);

        return response()->json($mascota, 201);
    }

    public function update(Request $request, string $id)
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idUsuario = $usuario->id_usuario;
        $mascota = Mascotas::find($id);

        //Si el usuario intenta modificar una mascota que no le pertenece no se le permite
        if (!$mascota || $mascota->id_usuario !== $idUsuario) {
            return response()->json(['error' => 'Acción no permitida'], 403);
        }

        //Datos modificables por el usuario logeado
        $validate = $request->validate([
            'nombre' => 'required|string',
            'especie' => 'required|string',
            'raza' => 'required|string',
            'edad' => 'required|numeric|min:0',
            'peso' => 'required|numeric|min:0|max:200'
        ]);

        $mascota->update($validate);
        return response()->json(data: $mascota);
    }
}
