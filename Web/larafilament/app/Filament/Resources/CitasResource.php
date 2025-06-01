<?php

namespace App\Filament\Resources;

use App\Filament\Resources\CitasResource\Pages;
use App\Filament\Resources\CitasResource\RelationManagers;
use App\Models\Citas;
use Filament\Forms;
use Filament\Forms\Form;
use Filament\Resources\Resource;
use Filament\Forms\Components\DateTimePicker;
use Filament\Forms\Components\Select;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables;
use Filament\Tables\Table;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\SoftDeletingScope;

class CitasResource extends Resource
{
    protected static ?string $model = Citas::class;

    protected static ?string $navigationIcon = 'heroicon-o-rectangle-stack';

    public static function form(Form $form): Form
    {//Valores modificables al crear un nuevo dato o al modificar uno existente
        return $form
            ->schema([
                Select::make('id_usuario' )->relationship('usuario', 'nombre', modifyQueryUsing: fn ($query) => 
                //Solo se muestran los usuarios cuyo rol es de "usuario" es decir su id_rol es 3
                $query->where('id_rol', 3))->required()->label('Dueño')->reactive(),

                Select::make('id_veterinario')->relationship('veterinario', 'nombre', modifyQueryUsing: fn($query)=>
                //Solo se muestra nlos usuarios cuyo rol es de "veterinario" es decir su id_rol es 2
                $query->where('id_rol', 2))->required()->label('Veterinario'),
                
                DateTimePicker::make('fecha')->label('Fecha')->default(now())->minDate(now()),
                
                Select::make('id_mascota')->label('Mascota')
                    ->options(function (callable $get) { 
                        //Se definen las opciones a aparecer en el select de manera dinámica
                        $usuarioId = $get('id_usuario');//Recupera el valor actual del campo 'id_usuario'
                        if (!$usuarioId) {//Si no se ha seleccionado algún usuario no muestra nada
                            return [];
                        }
                        //Consulta y filtra los datos de la tabla mascotas donde el id_usuario coincida con el seleccionado
                        return \App\Models\Mascotas::where('id_usuario', $usuarioId)
                            //Crea las opciones para mostrar los nombres de las mascotas
                            ->pluck('nombre', 'id_mascota');})->required()
            ]);
    }

    public static function table(Table $table): Table
    {//Valores mostrados recuperados de la base de datos
        return $table
            ->columns([
                TextColumn::make('id_cita')->label('ID'),
                TextColumn::make('usuario.nombre')->label('Dueño'),
                TextColumn::make('veterinario.nombre')->label('Veterinario'),
                TextColumn::make('fecha')->label('Fecha'),
                TextColumn::make('mascota.nombre')->label('Mascota')
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
            'index' => Pages\ListCitas::route('/'),
            'create' => Pages\CreateCitas::route('/create'),
            'edit' => Pages\EditCitas::route('/{record}/edit'),
        ];
    }
}
