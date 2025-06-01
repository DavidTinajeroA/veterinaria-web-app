<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {//Definición de la tabla con sus constraints
        Schema::create('consultas', function(Blueprint $table){
            $table->id('id_consulta');
            $table->unsignedBigInteger('id_usuario');
            $table->dateTime('fecha')->default( DB::raw('CURRENT_TIMESTAMP')); //DB::raw para sql puro que permita la funcion
            $table->string('motivo');
            $table->string('diagnostico');
            $table->string('tratamiento');
            $table->unsignedBigInteger('id_mascota');
            $table->foreign('id_usuario')->references('id_usuario')->on('usuarios')->onDelete('cascade');
            $table->foreign('id_mascota')->references('id_mascota')->on('mascotas')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {//Borra si se hace rollback
        Schema::dropIfExists('consultas');
    }
};
