<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class DatosUsuario extends Model 
{ //Modelo para tabla datosUsuario
    protected $table = 'datosUsuario';
    protected $primaryKey = 'id_datosUsuario';
    public $timestamps = false;//No se guardan datos de tiempo
    protected $fillable = [
        'direccion',
        'num_telefonico',
        'id_usuario'
    ];

    public function usuario()
    {//Relación con tabla usuarios
        return $this->belongsTo(User::class, 'id_usuario');
    }
}

