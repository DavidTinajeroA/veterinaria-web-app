<?php

namespace App\Filament\Resources\DatosUsuarioResource\Pages;

use App\Filament\Resources\DatosUsuarioResource;
use Filament\Actions;
use Filament\Resources\Pages\EditRecord;

class EditDatosUsuario extends EditRecord
{
    protected static string $resource = DatosUsuarioResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\DeleteAction::make(),
        ];
    }
}
