<?php

namespace App\Filament\Resources\DatosUsuarioResource\Pages;

use App\Filament\Resources\DatosUsuarioResource;
use Filament\Actions;
use Filament\Resources\Pages\ListRecords;

class ListDatosUsuarios extends ListRecords
{
    protected static string $resource = DatosUsuarioResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
