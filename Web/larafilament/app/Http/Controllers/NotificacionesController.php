<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Carbon\Carbon;
use App\Models\Notificaciones;

class NotificacionesController extends Controller
{
    //Retorna las notificaciones relacionadas al usuario logeado (usuarios y veterinarios)
    public function index()
    {
        //Variables recuperadas del usuario logeado
        $usuario = auth()->user();
        $idUsuario = $usuario->id_usuario;
        $idRol = $usuario->id_rol;

        // Eliminar notificaciones de citas que ya pasaron
        Notificaciones::whereHas('cita', function ($query) use ($idUsuario) {
            $query->where('id_usuario', $idUsuario)
                ->where('fecha', '<', now());
        })->delete();
        
        //Variables para mostrar notifiaciones relevantes basado en fecha 
        $hoy = Carbon::now()->startOfDay();
        //Plazo definido de notifiaciones a mostrar en este caso 7 días
        $plazo = Carbon::now()->addDays(7)->endOfDay();

        //Recupera todos los registros donde el id del usuario esté relacionado con una cita relacionada a la notifiación
        $notificaciones = Notificaciones::whereHas('cita', function ($query) use ($idUsuario, $hoy, $plazo) {
        //Se muestran las notificaciones cuya cita sucedera entre hoy y el plazo dado 
        $query->where('id_usuario', $idUsuario)->orWhere('id_veterinario', $idUsuario)
        ->whereBetween('fecha', [$hoy, $plazo]);
        })->with('cita.mascota', 'cita.veterinario', 'cita.usuario')->get();   

        if($idRol === 3){
            //Retorna los datos a mostrar en la app y el texto que los contiene para el usuario
            return $notificaciones->map(function ($notificacion) {
                return [
                'id' => $notificacion->id_notificacion,
                'titulo' => "Cita con: \n"  . $notificacion->cita->veterinario->nombre,
                'mensaje' => "\nTienes una cita el: \n\n"  . $notificacion->cita->fecha . 
                            "\n\nPara tú mascota: \n\n" . $notificacion->cita->mascota->nombre,               
                ];
            });
        }else{
            //Retorna los datos a mostrar en la app para el veterinario
            return $notificaciones->map(function ($notificacion) {
                return [
                'id' => $notificacion->id_notificacion,
                'titulo' => "Cita con: \n"  . $notificacion->cita->usuario->nombre,
                'mensaje' => "\nTienes una cita el: \n\n"  . $notificacion->cita->fecha . 
                            "\n\nPara la mascota: \n\n" . $notificacion->cita->mascota->nombre,               
                ];
            });
        }
    }
}
