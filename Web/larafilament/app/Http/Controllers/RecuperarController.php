<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Password;
use Illuminate\Support\Facades\Mail;
use App\Mail\Recuperar;
use App\Models\User; 
class RecuperarController extends Controller
{
    public function enviarCorreo(Request $request)
    {
        $request->validate([
            'email' => 'required|email|exists:usuarios,email',
        ]);

        $email = $request->input('email');

        //Buscar el usuario con el email
        $user = User::where('email', $email)->first();

        if (!$user) {
            return response()->json([
                'message' => 'No se encontró un usuario con ese correo.',
            ], 404);
        }

        //Crear token para resetear password
        $token = Password::broker()->createToken($user);

        //Enviar correo con el token
        Mail::to($email)->send(new Recuperar($token, $email));

        return response()->json([
            'message' => 'Correo de recuperación enviado',
        ]);
    }
}
