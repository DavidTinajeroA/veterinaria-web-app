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
        Schema::create('mascotas', function(Blueprint $table){
            $table->id('id_mascota');
            $table->unsignedBigInteger('id_usuario');
            $table->string('nombre');
            $table->string('especie');
            $table->string('raza');
            $table->integer('edad');
            $table->float('peso')->check('peso > 0 AND peso <= 999.99');
            $table->foreign('id_usuario')->references('id_usuario')->on('usuarios')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {//Borra si se hace rollback
        Schema::dropIfExists('mascotas');
    }
};
