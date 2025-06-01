<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Catalogo extends Model 
{ //Modelo para tabla catalogo
    protected $table = 'catalogo';
    protected $primaryKey = 'id_producto';
    public $timestamps = false;//No se guardan datos de tiempo
    protected $fillable = [
        'nombre',
        'tipo',
        'cantidad',
        'precio'
    ];
}
