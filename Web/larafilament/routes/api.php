<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\CatalogoController;
use App\Http\Controllers\CitasController;
use App\Http\Controllers\ConsultasController;
use App\Http\Controllers\DatosUsuarioController;
use App\Http\Controllers\MascotasController;
use App\Http\Controllers\NotificacionesController;
use App\Http\Controllers\UserController;

//Rutas a ser accedidas desde la app móvil y las acciones que ejecuta cada una
Route::post('/login', [UserController::class, 'login']);
Route::post('/logout', [UserController::class, 'logout']);
Route::post('/registrar', [UserController::class, 'store']);

Route::middleware('auth:sanctum')->get('/catalogo', [CatalogoController::class, 'index']);

Route::middleware('auth:sanctum')->get('/citas',[CitasController::class, 'index']);
Route::middleware('auth:sanctum')->put('/citas/{id}',[CitasController::class, 'update']);
Route::middleware('auth:sanctum')->delete('/citas/{id}',[CitasController::class, 'destroy']);

Route::middleware('auth:sanctum')->get('/consultas',[ConsultasController::class, 'index']);
Route::middleware('auth:sanctum')->put('/consultas/{id}',[ConsultasController::class, 'update']);
Route::middleware('auth:sanctum')->delete('/consultas/{id}',[ConsultasController::class, 'destroy']);

Route::middleware('auth:sanctum')->get('/datosUsuario',[DatosUsuarioController::class, 'index']);
Route::middleware('auth:sanctum')->post('/datosUsuario',[DatosUsuarioController::class, 'store']);
Route::middleware('auth:sanctum')->put('/datosUsuario/{id}',[DatosUsuarioController::class, 'update']);

Route::middleware('auth:sanctum')->get('/mascotas',[MascotasController::class, 'index']);
Route::middleware('auth:sanctum')->post('/mascotas',[MascotasController::class, 'store']);
Route::middleware('auth:sanctum')->put('/mascotas/{id}',[MascotasController::class, 'update']);

Route::middleware('auth:sanctum')->get('/notificaciones',[NotificacionesController::class, 'index']);