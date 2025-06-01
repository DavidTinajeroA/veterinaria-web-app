<?php

namespace App\Filament\Resources\MascotasResource\Pages;

use App\Filament\Resources\MascotasResource;
use Filament\Actions;
use Filament\Resources\Pages\ListRecords;

class ListMascotas extends ListRecords
{
    protected static string $resource = MascotasResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
