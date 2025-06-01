<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Citas extends Model
{ //Modelo para tabla citas
    protected $table = 'citas';
    protected $primaryKey = 'id_cita';
    public $timestamps = false;//No se guardan datos de tiempo
    protected $fillable = [
        'id_usuario',
        'id_veterinario',
        'fecha',
        'id_mascota',
    ];
    public function usuario()
    {//Relación con tabla usuarios para el dueño
        return $this -> belongsTo(User::class, 'id_usuario');
    }
    public function veterinario()
    {//Relación con tabla usuarios para el veterinario
        return $this -> belongsTo(User::class, 'id_veterinario');
    }
    public function mascota()
    {//Relación con tabla mascotas
        return $this -> belongsTo(Mascotas::class, 'id_mascota');
    }
}
