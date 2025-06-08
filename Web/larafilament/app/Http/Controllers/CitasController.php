<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Filament\facades\Filament;
use App\Models\Citas;
use App\Models\Notificaciones;

class CitasController extends Controller
{
    //Retorna las citas en las que el id del usuario logeado está relacionado
    public function index()
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idRol = $usuario->id_rol;
        $idUsuario = $usuario->id_usuario;
        
        //Si el usuario logeado es de rol usuario se muestran las citas relacionadas
        if($idRol === 3) {   
            return Citas::with('usuario', 'veterinario', 'mascota')->where('id_usuario',$idUsuario)->get();
        }else{//Si no y por ende es veterinario se muuestran las citas relacionadas
            return Citas::with('usuario', 'veterinario', 'mascota')->where('id_veterinario', $idUsuario)->get();
        };
    }
    public function store(Request $request)
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idRol = $usuario->id_rol;
        $idUsuario = $usuario->id_usuario;
        
        //Si el rol del usuario logeado es veterinario permite crear una nueva cita
        if ($idRol === 2) {
            $validate = $request->validate([
                'id_usuario' => 'required|exists:usuarios,id_usuario',
                'id_mascota' => 'required|exists:mascotas,id_mascota',
                'fecha' => 'required|date',
            ]);
            $cita = Citas::create([
                'id_veterinario' => $idUsuario,
                'id_usuario' => $validate['id_usuario'],
                'id_mascota' => $validate['id_mascota'],
                'fecha' => $validate['fecha'],
            ]);
            //Crear notificación de la cita generada
            Notificaciones::create([
                'id_cita' => $cita->id_cita,
            ]);
            return response()->json($cita, 201);
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
        $cita = Citas::find($id);

        //Si el usuario es veterinario permite el actualizar una cita seleccionada
        if($idRol === 2){
            $validate = $request->validate([
                'id_usuario' => 'exists:usuarios,id_usuario',
                'id_mascota' => 'exists:mascotas,id_mascota',
                'fecha' => 'date',
            ]);
            $cita->update(array_merge($validate, ['id_veterinario' => $idUsuario]));
            return response()->json($cita);
        }else{//Si no es evterinario no permite actualizarlas
            return response()->json(['error' => 'Acceso no permitido'], 403);
        };    
    }
    public function destroy(string $id)
    {
        //Variables recuperadas del suuario logeado
        $usuario = auth()->user();
        $idRol = $usuario->id_rol;
        $cita = Citas::find($id);

        //Si el usuario es veterinario permite el eliminar citas 
        if($idRol === 2){
            $cita->delete();
            return response()->json(['mensaje' => 'Cita eliminada']);
        }else{//Si no es veterinario no permite eliminarlas
            return response()->json(['error' => 'Acceso no permitido'], 403);
        };
    }
}
