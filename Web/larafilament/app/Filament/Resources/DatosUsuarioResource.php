<?php

namespace App\Filament\Resources;

use App\Filament\Resources\DatosUsuarioResource\Pages;
use App\Filament\Resources\DatosUsuarioResource\RelationManagers;
use App\Models\DatosUsuario;
use Filament\Forms;
use Filament\Forms\Components\Hidden;
use Filament\Forms\Form;
use Filament\Forms\Components\TextInput;
use Filament\Tables\Columns\TextColumn;
use Filament\Resources\Resource;
use Filament\Forms\Components\Select;
use Filament\Tables;
use Filament\Facades\Filament;
use Filament\Tables\Table;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\SoftDeletingScope;

class DatosUsuarioResource extends Resource
{
    protected static ?string $model = DatosUsuario::class;

    protected static ?string $navigationIcon = 'heroicon-o-rectangle-stack';

    public static function form(Form $form): Form
    {//Valores modificables al crear un nuevo dato o al modificar uno existente
        
        //Recuperar el panel actual
        $panel = Filament::getCurrentPanel()?->getId();
        //Recuperar el id del usuario logeado
        $idUsuario = Filament::auth()->user()?->id_usuario;
        $direccion = Filament::auth()->user()?->direccion;
        $num_telefonico = Filament::auth()->user()?->num_telefonico;

        //Si el panel es de admin se da acceso a todos los datos que hay
        if($panel === 'admin'){
            return $form
                ->schema([
                    Select::make('id_usuario')->relationship('usuario', 'nombre', modifyQueryUsing: fn($query)=> 
                    //Solo se muestran los usuarios cuyo rol es usuario o veterinario.
                    $query->where('id_rol', '!=', 1))->required(),
                    
                    TextInput::make('direccion')->required(),
                    TextInput::make('num_telefonico')->required()->numeric()->minLength(10)->maxLength(10)
                ]);
        }else{//Si el panel no es de admin solo muestran los datos que corresponden al usuario logeado
            return $form
                ->schema([
                    //Valor id oculto que corresponde al usuario
                    Hidden::make('id_usuario')->default($idUsuario)->dehydrated(),
                    TextInput::make('direccion')->default($direccion)->required(),
                    TextInput::make('num_telefonico')->default($num_telefonico)->required()->numeric()->minLength(10)->maxLength(10)
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
                    TextColumn::make('id_datosUsuario')->label('ID'),
                    TextColumn::make('usuario.nombre')->label('Nombre'),
                    TextColumn::make('direccion')->label('Dirección'),
                    TextColumn::make('num_telefonico')->label('Número')
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
        }else{//Si el panel no es de admin solo muestran los datos que corresponden al usuario logeado
            return $table
                ->columns([
                    TextColumn::make('usuario.nombre')->label('Nombre'),
                    TextColumn::make('direccion')->label('Dirección'),
                    TextColumn::make('num_telefonico')->label('Número')
                ])
                //Se recuperan y muestrasolo los datos del usuario logeadp
                ->query(DatosUsuario::query()->where('id_usuario', $idUsuario))
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
        };
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
            'index' => Pages\ListDatosUsuarios::route('/'),
            'create' => Pages\CreateDatosUsuario::route('/create'),
            'edit' => Pages\EditDatosUsuario::route('/{record}/edit'),
        ];
    }
}
