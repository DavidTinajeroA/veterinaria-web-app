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
        Schema::create('datosUsuario', function(Blueprint $table){
            $table->id('id_datosUsuario');
            $table->unsignedBigInteger('id_usuario');
            $table->string('direccion');
            $table->text('num_telefonico')->check('length(num_telefonico) = 10');
            $table->foreign('id_usuario')->references('id_usuario')->on('usuarios')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {//Borra si se hace rollback
        Schema::dropIfExists('datosUsuario');
    }
};
