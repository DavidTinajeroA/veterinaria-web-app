<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use App\Models\User;

class UserController extends Controller
{
    // Método para iniciar sesión
    public function login(Request $request)
    {
        // Validación de entrada
        $credentials = $request->validate([
            'email' => ['required', 'email'],
            'password' => ['required'],
        ]);

        // Buscar al usuario por email
        $user = User::where('email', $credentials['email'])->first();

        // Verificar que exista y que la contraseña sea correcta
        if (!$user || !Hash::check($credentials['password'], $user->password)) {
            return response()->json(['error' => 'Credenciales inválidas'], 401);
        }

        // Crear token para autenticación
        $token = $user->createToken('api-token')->plainTextToken;

        // Retornar token y datos básicos del usuario
        return response()->json([
            'token' => $token,
            'user' => [
                'id_usuario' => $user->id_usuario,
                'nombre' => $user->nombre,
                'id_rol' => $user->id_rol,
                'email' => $user->email,
            ]
        ]);
    }

    // Método para cerrar sesión
    public function logout(Request $request)
    {
        // Revoca todos los tokens del usuario autenticado
        $request->user()->tokens()->delete();

        return response()->json(['mensaje' => 'Sesión cerrada correctamente']);
    }
}
