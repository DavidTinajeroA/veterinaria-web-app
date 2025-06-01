<?php

namespace App\Filament\Resources;

use App\Filament\Resources\ConsultasResource\Pages;
use App\Filament\Resources\ConsultasResource\RelationManagers;
use App\Models\Consultas;
use Filament\Forms;
use Filament\Forms\Form;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Columns\TextColumn;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\DateTimePicker;
use Filament\Forms\Components\TextInput;
use Filament\Tables\Table;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\SoftDeletingScope;
use IntlCalendar;

class ConsultasResource extends Resource
{
    protected static ?string $model = Consultas::class;

    protected static ?string $navigationIcon = 'heroicon-o-rectangle-stack';

    public static function form(Form $form): Form
    {//Valores modificables al crear un nuevo dato o al modificar uno existente
        return $form
            ->schema([
                Select::make('id_usuario')->label('Dueño')->relationship('usuario', 'nombre', modifyQueryUsing: fn($query)=>
                //Solo se muestran los usuarios cuyo rol es de "usuario" es decir su id_rol es 3
                $query->where('id_rol',3))->required()->reactive(),
                
                DateTimePicker::make('fecha')->label('Fecha')->default(now()),
                TextInput::make('motivo')->required(),
                TextInput::make('diagnostico')->required(),
                TextInput::make('tratamiento')->required(),
                
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
                TextColumn::make('id_consulta')->label('ID'),
                TextColumn::make('usuario.nombre')->label('Dueño'),
                TextColumn::make('fecha')->label('Fecha'),
                TextColumn::make('motivo')->label('Motivo'),
                TextColumn::make('diagnostico')->label('Diagnóstico'),
                TextColumn::make('tratamiento')->label('Tratamiento'),
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
            'index' => Pages\ListConsultas::route('/'),
            'create' => Pages\CreateConsultas::route('/create'),
            'edit' => Pages\EditConsultas::route('/{record}/edit'),
        ];
    }
}
