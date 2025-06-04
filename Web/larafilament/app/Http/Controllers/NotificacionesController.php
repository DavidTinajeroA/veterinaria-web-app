<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Notificaciones;

class NotificacionesController extends Controller
{
    //Retorna las notificaciones relacionadas al usuario logeado (usuarios y veterinarios)
    public function index()
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idUsuario = $usuario->id_usuario;

        //Recupera todos los registros donde el id del usuario esté relacionado
        return Notificaciones::where('id_usuario', $idUsuario)->get();
    }
}
