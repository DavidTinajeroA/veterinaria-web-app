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
        Schema::create('notificaciones', function(Blueprint $table){
            $table->id('id_notificacion');
            $table->unsignedBigInteger('id_cita');
            $table->foreign('id_cita')->references('id_cita')->on('citas')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {//Borra si se hace rollback
        Schema::dropIfExists('notificaciones');
    }
};
