<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Consultas;

class ConsultasController extends Controller
{
    //Retorna las consultas en las que el id del usuario logeado está relacionado
    public function index()
    {
        //Variables recuperadas del suuario logeado
        $usuario = auth()->user();
        $idRol = $usuario->id_rol;
        $idUsuario = $usuario->id_usuario;
        
        //Si el usuario logeado es de rol veterinario se muestran las consultas relacionadas
        if($idRol === 2) {   
            return Consultas::with('usuario','mascota')->where('id_usuario',$idUsuario)->get();
        }else{//Si no es veterinario no permite el visualizarlas
            return response()->json(['error' => 'Acceso no permitido'], 403);
        };
    }
    public function store(Request $request)
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idRol = $usuario->id_rol;
        $idUsuario = $usuario->id_usuario;
        
        //Si el rol del usuario logeado es veterinario permite crear una nueva consulta
        if ($idRol === 2) {
            $validate = $request->validate([
                'fecha' => 'required|date',
                'motivo' => 'required|string',
                'diagnostico' => 'required|string',
                'tratamiento' => 'required|string',
                'id_mascota' => 'required|exists:mascotas,id_mascota',
            ]);
            $consulta = Consultas::create([
                'id_usuario' => $idUsuario,
                'motivo' => $validate['motivo'],
                'fecha' => $validate['fecha'],
                'diagnostico' => $validate['diagnostico'],
                'tratamiento' => $validate['tratamiento'],
                'id_mascota' => $validate['id_mascota'],
            ]);
            return response()->json($consulta, 201);
        }else{//Si no es veterinario no permite el crear una nueva
            return response()->json(['error' => 'Acceso no permitido'], 403);
        };
    }
    public function update(Request $request, string $id)
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idUsuario = $usuario->id_usuario;
        $idRol = $usuario->id_rol;
        $consulta = Consultas::find($id);

        //Si el usuario es veterinario permite el actualizar una consulta seleccionada
        if($idRol === 2){
            $validate = $request->validate([
                'fecha' => 'required|date',
                'motivo' => 'required|string',
                'diagnostico' => 'required|string',
                'tratamiento' => 'required|string',
                'id_mascota' => 'required|exists:mascotas,id_mascota'
            ]);
            $consulta->update(array_merge($validate, ['id_usuario' => $idUsuario]));
            return response()->json($consulta);
        }else{//Si no es evterinario no permite actualizarlas
            return response()->json(['error' => 'Acceso no permitido'], 403);
        };    
    }
    public function destroy(string $id)
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idRol = $usuario->id_rol;
        $consulta = Consultas::find($id);

        //Si el usuario es veterinario permite el eliminar Consultas 
        if($idRol === 2){
            $consulta->delete();
            return response()->json(['mensaje' => 'Consulta eliminada']);
        }else{//Si no es veterinario no permite eliminarlas
            return response()->json(['error' => 'Acceso no permitido'], 403);
        };
    }
}
