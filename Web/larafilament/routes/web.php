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

Route::get('/password/reset/{token}', function ($token) {
    return view('auth.reset', ['token' => $token]);
})->name('password.reset');

Route::post('/password/reset', function (Request $request) {
    $request->validate([
        'token' => 'required',
        'email' => 'required|email',
        'password' => 'required|min:8|confirmed',
    ]);

    $status = Password::reset(
        $request->only('email', 'password', 'password_confirmation', 'token'),
        function ($user, $password) {
            $user->forceFill([
                'password' => Hash::make($password),
            ])->save();
        }
    );

    if ($status == Password::PASSWORD_RESET) {
        return redirect('/login')->with('status', __($status));
    } else {
        return back()->withErrors(['email' => __($status)]);
    }
})->name('password.update');