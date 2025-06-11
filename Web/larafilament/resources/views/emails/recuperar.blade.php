<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Recuperar contraseña</title>
</head>
<body>
    <p>Tú solicitud para recuperar tu contraseña.</p>
    <p>Haz clic en el siguiente enlace para restablecer tu contraseña:</p>
    <p>
        @php
            $baseUrl = 'http://127.0.0.1:8000';
            //URL completa concatenando la ruta y parámetros
            $resetUrl = $baseUrl . '/password/reset/' . $token . '?email=' . urlencode($email);
        @endphp
        <a href="{{ $resetUrl }}">Restablecer contraseña</a>
    </p>
    <br>
</body>
</html>
