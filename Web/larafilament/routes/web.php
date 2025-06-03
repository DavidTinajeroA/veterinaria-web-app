<?php

use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\Auth;
use Illuminate\Http\Request;

//Redirige al archivo donde se creo el login global.
Route::get('/login', function () {
    return view('auth.login');
})->name('login');

//Refirige al panel que corresponde segun el tipo de rol que contenga el usuario tras ingresar sus credenciales
Route::post('/login', function (Request $request) {
    $credenciales = $request->only('email', 'password');

    if (Auth::attempt($credenciales)) {
        $user = Auth::user();

        //Rtorna la redirección al panel correspondiente según el rol
        return match ($user->id_rol) {
            1 => redirect()->to('/admin'),
            2 => redirect()->to('/veterinario'),
            3 => redirect()->to('/usuario'),
        };
    }

    return back()->withErrors([
        'email' => 'Credenciales incorrectas.',
    ]);
});

Route::get('/', function () {
    return redirect('/login');
});
