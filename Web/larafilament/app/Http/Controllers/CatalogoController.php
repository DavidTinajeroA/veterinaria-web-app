<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Catalogo;

class CatalogoController extends Controller
{
    //Retornar todos los productos para ser mostrados
    public function index()
    {
        return Catalogo::all();
    }
}
