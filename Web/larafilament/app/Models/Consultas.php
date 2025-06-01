<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Consultas extends Model
{ //Modelo para tabla consultas
    protected $table = 'consultas';
    protected $primaryKey = 'id_consulta';
    public $timestamps = false;//No se guardan datos de tiempo
    protected $fillable = [
        'id_usuario',
        'fecha',
        'motivo',
        'diagnostico',
        'tratamiento',
        'id_mascota'
    ];
    public function usuario() 
    {//Relación con tabla usuarios
        return $this->belongsTo(User::class, 'id_usuario');
    }
    public function mascota()
    {//Relación con tabla mascotas
        return $this->belongsTo(Mascotas::class, 'id_mascota');
    }

}
