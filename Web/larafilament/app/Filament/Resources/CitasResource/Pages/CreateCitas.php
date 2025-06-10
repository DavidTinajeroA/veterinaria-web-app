<?php

namespace App\Filament\Resources\CitasResource\Pages;

use App\Filament\Resources\CitasResource;
use Filament\Actions;
use Filament\Resources\Pages\CreateRecord;
use App\Models\Notificaciones;

class CreateCitas extends CreateRecord
{
    protected static string $resource = CitasResource::class;

        protected function afterCreate(): void {
        // Crear automáticamente la notificación que corresponde a la cita
        Notificaciones::create([
            'id_cita' => $this->record->id_cita
        ]);
    }
}
