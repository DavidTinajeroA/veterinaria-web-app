<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Mascotas extends Model
{ //Modelo para tabla mascotas
    protected $table = 'mascotas';
    protected $primaryKey = 'id_mascota';
    public $timestamps = false;//No se guardan datos de tiempo
    protected $fillable = [
        'id_usuario',
        'nombre',
        'especie',
        'raza',
        'edad',
        'peso'
    ];
    public function usuario()
    {//Relación con tabla usuarios
        return $this -> belongsTo(User::class, 'id_usuario');
    }
}
