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
        Schema::create('usuarios', function (Blueprint $table){
            $table->id('id_usuario');
            $table->string('nombre');
            $table->string('email')->unique();
            $table->string('password');
            $table->unsignedBigInteger('id_rol');
            $table->rememberToken();
            $table->foreign('id_rol')->references('id_rol')->on('roles')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {//Borra si se hace rollback
        Schema::dropIfExists('usuarios');
    }
};
