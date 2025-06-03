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
        //Se recupera el valor del panel y si no es usuario no se muestra el botón de crear
        $panel = \Filament\Facades\Filament::getCurrentPanel()?->getId();
        if($panel !== 'admin'){
            return [];
        }
            return [
                Actions\CreateAction::make(),
            ];
    }
}
