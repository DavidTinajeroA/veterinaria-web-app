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
        Schema::create('catalogo', function(Blueprint $table){
            $table->id('id_producto');
            $table->string('nombre')->unique();
            $table->string('tipo');
            $table->integer('cantidad')->check('cantidad >= 0');
            $table->float('precio')->check('precio >= 0');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {//Borra si se hace rollback
        Schema::dropIfExists('catalogo');
    }
};
