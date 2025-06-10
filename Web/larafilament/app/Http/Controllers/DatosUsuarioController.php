<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\DatosUsuario;

class DatosUsuarioController extends Controller
{
    public function index()
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idUsuario = $usuario->id_usuario;
        $nombreUsuario = $usuario->nombre;
        //Recupera el registro que pertenece al usuario logeado
        return DatosUsuario::with('usuario')->where('id_usuario', $idUsuario)->get();
    }
    public function store(Request $request)
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idUsuario = $usuario->id_usuario;

        //Datos a cargar al crear un nuevo registro
        $validate = $request->validate([
            'direccion' => 'required|string',
            'num_telefonico' => 'required|digits:10'
        ]);
        $datosUsuario = DatosUsuario::create([
            'id_usuario' => $idUsuario,
            'direccion' => $validate['direccion'],
            'num_telefonico' => $validate['num_telefonico'] 
        ]);
        return response()->json($datosUsuario, 201);
    }
    public function update(Request $request, string $id)
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idUsuario = $usuario->id_usuario;
        $datosUsuario = DatosUsuario::find($id);

        //Si el usuario intenta modificar datos que no correspondan a el no se le permite
        if ($datosUsuario->id_usuario !== $idUsuario) {
            return response()->json(['error' => 'Acción no permitida'], 403);
        }

        //Datos modificables por el usuario logeado
        $validate = $request->validate([
            'direccion' => 'required|string',
            'num_telefonico' => 'required|digits:10'
        ]);
        $datosUsuario->update(array_merge($validate, ['id_usuario' => $idUsuario]));
        return response()->json($datosUsuario);
    }
}
