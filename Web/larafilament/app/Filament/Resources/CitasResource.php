<?php

namespace App\Filament\Resources;

use App\Filament\Resources\CitasResource\Pages;
use App\Filament\Resources\CitasResource\RelationManagers;
use App\Models\Citas;
use Filament\Forms;
use Filament\Forms\Form;
use Filament\Facades\Filament;
use Filament\Forms\Components\Hidden;
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
        
        //Recuperar el panel actual
        $panel = Filament::getCurrentPanel()?->getId();
        //Recuperar el id del usuario logeado
        $idUsuario = Filament::auth()->user()?->id_usuario;
        
         //Si el panel es de admin se da acceso a todos los datos que hay
        if($panel === 'admin'){
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
        }elseif($panel === 'veterinario'){//No muestra el ID y se oculta el veterinario, dejando como default el usuario logeado
            return $form
                ->schema([
                    Hidden::make('id_veterinario')->default($idUsuario)->dehydrated(),

                    Select::make('id_usuario')->relationship('usuario', 'nombre', modifyQueryUsing: 
                    fn($query)=> $query->where('id_rol',3))->required()->reactive()->label('Dueño'),
                    
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
        }else{//El usuario no puede crear citas ni modificarlas, por eso se deja vacio
            return $form
                ->schema([
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
        }elseif($panel === 'veterinario'){//Se oculta el ID y el nombre del veterinario
            return $table
                ->columns([
                    TextColumn::make('usuario.nombre')->label('Dueño'),
                    TextColumn::make('mascota.nombre')->label('Mascota'),
                    TextColumn::make('fecha')->label('Fecha')
                ])
                ->query(Citas::query()->where('id_veterinario',$idUsuario))
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
        }else{//Se oculta el ID y el nombre del usuario
           
            //Si el panel corresponde a usuario no se muestra el boton de editar
            if($panel === 'usuario'){
                $actions = [];
            }else{//Si el panel no es de usuario se muestra
                $actions = [\Filament\Tables\Actions\EditAction::make(),];
            };
           
            return $table
                ->columns([
                    TextColumn::make('veterinario.nombre')->label('Veterinario'),
                    TextColumn::make('mascota.nombre')->label('Mascota'),
                    TextColumn::make('fecha')->label('Fecha')
                ])
                ->query(Citas::query()->where('id_usuario',$idUsuario))
                ->filters([
                    //
                ])
                ->actions($actions)
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
            'index' => Pages\ListCitas::route('/'),
            'create' => Pages\CreateCitas::route('/create'),
            'edit' => Pages\EditCitas::route('/{record}/edit'),
        ];
    }
}