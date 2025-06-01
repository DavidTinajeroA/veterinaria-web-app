<?php

namespace App\Filament\Resources\ConsultasResource\Pages;

use App\Filament\Resources\ConsultasResource;
use Filament\Actions;
use Filament\Resources\Pages\ListRecords;

class ListConsultas extends ListRecords
{
    protected static string $resource = ConsultasResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
