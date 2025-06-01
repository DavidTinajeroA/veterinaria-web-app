<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Roles extends Model 
{ //Modelo para tabla roles
    protected $table = 'roles'; 
    protected $primaryKey = 'id_rol';
    public $timestamps = false;//No se guardan datos de tiempo
    protected $fillable = ['nombre'];
}
