<?php

namespace App\Filament\Resources\CitasResource\Pages;

use App\Filament\Resources\CitasResource;
use Filament\Actions;
use Filament\Resources\Pages\ListRecords;

class ListCitas extends ListRecords
{
    protected static string $resource = CitasResource::class;

    protected function getHeaderActions(): array
    {
        //Se recupera el valor del panel y si es usuario no se muestra el Create
        $panel = \Filament\Facades\Filament::getCurrentPanel()?->getId();
        if ($panel === 'usuario') {
            return [];
        }
        return [
            Actions\CreateAction::make(),
        ];
    }
}
