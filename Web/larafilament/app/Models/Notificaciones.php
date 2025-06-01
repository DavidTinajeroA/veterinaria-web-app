<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Notificaciones extends Model
{ //Modelo para tabla notificaciones
    protected $table = 'notificaciones';
    protected $primaryKey = 'id_notificacion';
    public $timestamps = false;//No se guardan datos de tiempo
    protected $fillable = ['id_cita'];
    public function cita()
    {//Relación con tabla citas
        return $this -> belongsTo(Citas::class, 'id_cita'); 
    }
}
