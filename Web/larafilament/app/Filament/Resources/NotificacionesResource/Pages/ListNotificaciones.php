<?php

namespace App\Filament\Resources\NotificacionesResource\Pages;

use App\Filament\Resources\NotificacionesResource;
use Filament\Actions;
use Filament\Resources\Pages\ListRecords;

class ListNotificaciones extends ListRecords
{
    protected static string $resource = NotificacionesResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
