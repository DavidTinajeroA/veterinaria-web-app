<?php

namespace App\Filament\Resources\MascotasResource\Pages;

use App\Filament\Resources\MascotasResource;
use Filament\Actions;
use Filament\Resources\Pages\EditRecord;

class EditMascotas extends EditRecord
{
    protected static string $resource = MascotasResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\DeleteAction::make(),
        ];
    }
}
