<?php

namespace App\Filament\Resources;

use App\Filament\Resources\CatalogoResource\Pages;
use App\Filament\Resources\CatalogoResource\RelationManagers;
use App\Models\Catalogo;
use Filament\Forms;
use Filament\Forms\Components\TextInput;
use Filament\Tables\Columns\TextColumn;
use Filament\Forms\Components\Select;
use Filament\Forms\Form;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Table;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\SoftDeletingScope;

class CatalogoResource extends Resource
{
    protected static ?string $model = Catalogo::class;

    protected static ?string $navigationIcon = 'heroicon-o-rectangle-stack';

    public static function form(Form $form): Form
    {//Valores modificables al crear un nuevo dato o al modificar uno existente
        return $form
            ->schema([
                TextInput::make('nombre')->required()->unique(ignoreRecord:true)->label('Producto'),
                Select::make('tipo')->required()->options(['Medicamento' => 'Medicamento','Accesorio' => 'Accesorio',])->label('Tipo'),
                TextInput::make('cantidad')->numeric()->required()->minValue(0),
                TextInput::make('precio')->numeric()->required()->minValue(0)
            ]);
    }

    public static function table(Table $table): Table
    {//Valores mostrados recuperados de la base de datos
        return $table
            ->columns([
                TextColumn::make('id_producto')->label('ID'),
                TextColumn::make('nombre')->label('Producto'),
                TextColumn::make('tipo')->label('Tipo'),
                TextColumn::make('cantidad')->label('Cantidad'),
                TextColumn::make('precio')->label('Precio')
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
            'index' => Pages\ListCatalogo::route('/'),
            'create' => Pages\CreateCatalogo::route('/create'),
            'edit' => Pages\EditCatalogo::route('/{record}/edit'),
        ];
    }
}
