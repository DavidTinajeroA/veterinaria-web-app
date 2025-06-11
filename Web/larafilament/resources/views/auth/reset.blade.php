<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <title>Restablecer contraseña</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-900 flex items-center justify-center min-h-screen px-4">
    <div class="bg-black shadow-lg rounded-xl p-8 w-full max-w-md border border-white">
        <h2 class="text-3xl font-bold text-center text-yellow-300 mb-6">Restablecer contraseña</h2>

        @if (session('status'))
            <div class="bg-green-100 text-green-700 px-4 py-2 rounded mb-4 text-center">
                {{ session('status') }}
            </div>
        @endif

        @if ($errors->any())
            <div class="bg-red-100 text-red-700 px-4 py-2 rounded mb-4">
                {{ $errors->first() }}
            </div>
        @endif

        <form method="POST" action="{{ route('password.update') }}" class="space-y-5">
            @csrf
            <input type="hidden" name="token" value="{{ $token }}" />

            <div>
                <label for="email" class="block text-sm font-medium text-gray-300">Correo electrónico</label>
                <input 
                    type="email" 
                    name="email" 
                    id="email" 
                    value="{{ old('email') }}" 
                    required 
                    autofocus
                    class="mt-1 block w-full border border-gray-600 bg-gray-900 text-white rounded-md shadow-sm p-2 focus:outline-none focus:ring-yellow-300 focus:border-yellow-300" 
                />
            </div>

            <div>
                <label for="password" class="block text-sm font-medium text-gray-300">Nueva contraseña</label>
                <input 
                    type="password" 
                    name="password" 
                    id="password" 
                    required
                    class="mt-1 block w-full border border-gray-600 bg-gray-900 text-white rounded-md shadow-sm p-2 focus:outline-none focus:ring-yellow-300 focus:border-yellow-300" 
                />
            </div>

            <div>
                <label for="password_confirmation" class="block text-sm font-medium text-gray-300">Confirmar contraseña</label>
                <input 
                    type="password" 
                    name="password_confirmation" 
                    id="password_confirmation" 
                    required
                    class="mt-1 block w-full border border-gray-600 bg-gray-900 text-white rounded-md shadow-sm p-2 focus:outline-none focus:ring-yellow-300 focus:border-yellow-300" 
                />
            </div>

            <button 
                type="submit" 
                class="w-full bg-yellow-300 text-black font-medium py-2 rounded-md hover:bg-yellow-400 transition"
            >
                Cambiar contraseña
            </button>
        </form>
    </div>
</body>
</html>
