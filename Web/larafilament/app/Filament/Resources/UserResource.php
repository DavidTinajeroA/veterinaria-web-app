<?php

namespace App\Filament\Resources;

use App\Filament\Resources\UserResource\Pages;
use App\Filament\Resources\UserResource\RelationManagers;
use App\Models\User;
use Filament\Forms;
use Filament\Forms\Form;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\Select;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Table;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\SoftDeletingScope;

class UserResource extends Resource
{
    protected static ?string $model = User::class;

    protected static ?string $navigationIcon = 'heroicon-o-rectangle-stack';

    public static function form(Form $form): Form
    {//Valores modificables al crear un nuevo dato o al modificar uno existente
        return $form
            ->schema([
                TextInput::make('nombre')->required(),
                TextInput::make('email')->email()->required()->unique(ignoreRecord:true),
                TextInput::make('password')->password()->required()
                ->dehydrateStateUsing(fn($state) => filled($state) ? bcrypt($state) : null)
                ->required(fn(string $context) => $context === 'create')
                ->dehydrated(fn($state) => filled($state)),
                Select::make('id_rol')->label('Rol')->relationship('rol','nombre')->required(),
            ]);
    }

    public static function table(Table $table): Table
    {//Valores mostrados recuperados de la base de datos
        return $table
            ->columns([
                TextColumn::make('id_usuario')->label( 'ID'),
                TextColumn::make('nombre')->label( 'Nombre'),
                TextColumn::make('email')->label('Email'),
                TextColumn::make('rol.nombre')->label('Rol')
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
            'index' => Pages\ListUsers::route('/'),
            'create' => Pages\CreateUser::route('/create'),
            'edit' => Pages\EditUser::route('/{record}/edit'),
        ];
    }
}
