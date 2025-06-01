<?php

namespace App\Filament\Resources\NotificacionesResource\Pages;

use App\Filament\Resources\NotificacionesResource;
use Filament\Actions;
use Filament\Resources\Pages\EditRecord;

class EditNotificaciones extends EditRecord
{
    protected static string $resource = NotificacionesResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\DeleteAction::make(),
        ];
    }
}
