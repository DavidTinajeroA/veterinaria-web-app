<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 flex items-center justify-center min-h-screen">
    <div class="bg-white p-6 rounded shadow-md w-full max-w-md">
        <h2 class="text-2xl font-semibold mb-4">Iniciar sesión</h2>

        @if ($errors->any())
            <div class="mb-4 text-red-600">{{ $errors->first() }}</div>
        @endif

        <form method="POST" action="/login" class="space-y-4">
            @csrf
            <div>
                <label class="block text-sm font-medium">Correo electrónico</label>
                <input type="email" name="email" required class="w-full border p-2 rounded" />
            </div>

            <div>
                <label class="block text-sm font-medium">Contraseña</label>
                <input type="password" name="password" required class="w-full border p-2 rounded" />
            </div>

            <button type="submit" class="w-full bg-amber-500 text-white py-2 rounded hover:bg-amber-600 transition">
                Entrar
            </button>
        </form>
    </div>
</body>
</html>
