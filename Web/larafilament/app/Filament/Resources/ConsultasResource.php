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
use Filament\Facades\Filament;
use Filament\Forms\Components\Hidden;
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
        
        //Recuperar el panel actual
        $panel = Filament::getCurrentPanel()?->getId();
        //Recuperar el id del usuario logeado
        $idUsuario = Filament::auth()->user()?->id_usuario;

        //Si el panel es de admin se da acceso a todos los datos que hay
        if($panel === 'admin'){
            return $form
                ->schema([
                    Select::make('id_usuario')->label('Veterinario')->relationship('usuario', 'nombre', modifyQueryUsing: fn($query)=>
                    //Solo se muestran los usuarios cuyo rol es de "veterinario" es decir su id_rol es 2
                    $query->where('id_rol',2))->required()->reactive(),
                    
                    Select::make('id_mascota')->relationship('mascota','nombre')->label('Mascota')->required(),
                    DateTimePicker::make('fecha')->label('Fecha')->default(now()),
                    TextInput::make('motivo')->required(),
                    TextInput::make('diagnostico')->required(),
                    TextInput::make('tratamiento')->required(),
                    
                ]);
        }else{//Si el panel no es de admin solo muestran los datos que corresponden al veterinario logeado
            return $form
                ->schema([
                    Hidden::make('id_usuario')->default($idUsuario)->dehydrated(),
                    Select::make('id_mascota')->relationship('mascota','nombre')->label('Mascota')->required(),
                    DateTimePicker::make('fecha')->label('Fecha')->default(now()),
                    TextInput::make('motivo')->required(),
                    TextInput::make('diagnostico')->required(),
                    TextInput::make('tratamiento')
                ]);
        };
    }

    public static function table(Table $table): Table
    {//Valores mostrados recuperados de la base de datos
        
        //Recuperar el panel actual
        $panel = Filament::getCurrentPanel()?->getId();
        //Recuperar el id del usuario logeado
        $idUsuario = Filament::auth()->user()?->id_usuario;
        
        //Si el panel es de admin se da acceso a todos los datos que hay
        if($panel === 'admin'){
            return $table
                ->columns([
                    TextColumn::make('id_consulta')->label('ID'),
                    TextColumn::make(name: 'mascota.nombre')->label('Mascota'),
                    TextColumn::make('usuario.nombre')->label('Veterinario'),
                    TextColumn::make('fecha')->label('Fecha'),
                    TextColumn::make('motivo')->label('Motivo'),
                    TextColumn::make('diagnostico')->label('Diagnóstico'),
                    TextColumn::make('tratamiento')->label('Tratamiento'),
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
        }else{//Si el panel no es de admin sse ocultan el ID y el veterinario
            return $table
                ->columns([
                    TextColumn::make(name: 'mascota.nombre')->label('Mascota'),
                    TextColumn::make('fecha')->label('Fecha'),
                    TextColumn::make('motivo')->label('Motivo'),
                    TextColumn::make('diagnostico')->label('Diagnóstico'),
                    TextColumn::make('tratamiento')->label('Tratamiento'),
                ])
                ->query(Consultas::query()->where('id_usuario',$idUsuario))
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
