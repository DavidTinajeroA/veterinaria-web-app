<?php

namespace App\Filament\Resources;

use App\Filament\Resources\MascotasResource\Pages;
use App\Filament\Resources\MascotasResource\RelationManagers;
use App\Models\Mascotas;
use Filament\Forms;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Form;
use Filament\Resources\Resource;
use Filament\Forms\Components\Select;
use Filament\Tables;
use Filament\Tables\Table;
use Filament\Tables\Columns\TextColumn;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\SoftDeletingScope;
use PHPUnit\Event\Code\IssueTrigger\TestTrigger;

class MascotasResource extends Resource
{
    protected static ?string $model = Mascotas::class;

    protected static ?string $navigationIcon = 'heroicon-o-rectangle-stack';

    public static function form(Form $form): Form
    {//Valores modificables al crear un nuevo dato o al modificar uno existente
        return $form
            ->schema([
                Select::make('id_usuario')->label('Dueño')->relationship('usuario', 'nombre',modifyQueryUsing: fn($query)=>
                //Solo se muestran los usuarios cuyo rol es de "usuario" es decir su id_rol es 3
                $query->where('id_rol',3)),
                TextInput::make('nombre')->required(),
                TextInput::make('especie')->required(),
                TextInput::make('raza')->required(),
                TextInput::make('edad')->required()->numeric()->rules(['gt:0']),
                TextInput::make('peso')->required()->numeric()->rules(['gt:0', 'lte:1000'])
            ]);
    }

    public static function table(Table $table): Table
    {//Valores mostrados recuperados de la base de datos
        return $table
            ->columns([
                TextColumn::make('id_mascota')->label('ID'),
                TextColumn::make('usuario.nombre')->label('Dueño'),
                TextColumn::make('nombre')->label('Nombre'),
                TextColumn::make('especie')->label('Especie'),
                TextColumn::make('raza')->label('Raza'),
                TextColumn::make('edad')->label('Edad'),
                TextColumn::make('peso')->label('Peso')
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
            'index' => Pages\ListMascotas::route('/'),
            'create' => Pages\CreateMascotas::route('/create'),
            'edit' => Pages\EditMascotas::route('/{record}/edit'),
        ];
    }
}
