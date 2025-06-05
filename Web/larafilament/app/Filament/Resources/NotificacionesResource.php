<?php

namespace App\Filament\Resources;

use App\Filament\Resources\NotificacionesResource\Pages;
use App\Filament\Resources\NotificacionesResource\RelationManagers;
use App\Models\Notificaciones;
use Filament\Forms;
use Filament\Forms\Form;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Columns\TextColumn;
use Filament\Forms\Components\Select;
use Filament\Tables\Table;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\SoftDeletingScope;

class NotificacionesResource extends Resource
{
    protected static ?string $model = Notificaciones::class;

    protected static ?string $navigationIcon = 'heroicon-o-rectangle-stack';

    public static function form(Form $form): Form
    {//Valores modificables al crear un nuevo dato o al modificar uno existente
        return $form
            ->schema([
                Select::make('id_cita')->relationship('cita','id_cita')->required()->label('ID Cita')
                ->getOptionLabelFromRecordUsing(function ($record) {
                return $record->usuario->nombre . ' - ' . $record->mascota->nombre . ' - ' . $record->fecha;
                }),
            ]);
    }
    public static function table(Table $table): Table
    {//Valores mostrados recuperados de la base de datos
        return $table
            ->columns([
                TextColumn::make('id_notificacion')->label('ID'),
                TextColumn::make('cita.usuario.nombre')->label('Usuario'),
                TextColumn::make('cita.veterinario.nombre')->label('Veterinario'),
                TextColumn::make('cita.fecha')->label('Fecha'),
                TextColumn::make('cita.mascota.nombre')->label('Mascota')
            ])
            ->filters([
                //
            ])
            ->actions([
                Tables\Actions\EditAction::make(),
            ])
            ->bulkActions([
                Tables\Actions\BulkActionGroup::make([
                    Tables\Actions\DeleteBulkAction::make(),
                ]),
            ]);
    }

    public static function getRelations(): array
    {
        return [
            //
        ];
    }

    public static function getPages(): array
    {
        return [
            'index' => Pages\ListNotificaciones::route('/'),
            'create' => Pages\CreateNotificaciones::route('/create'),
            'edit' => Pages\EditNotificaciones::route('/{record}/edit'),
        ];
    }
}
