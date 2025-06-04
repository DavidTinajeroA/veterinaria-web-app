<?php

namespace App\Models;

use Laravel\Sanctum\HasApiTokens;
use Filament\Models\Contracts\HasName;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Filament\Models\Contracts\FilamentUser;
use Filament\Panel;

class User extends Authenticatable implements FilamentUser, HasName
{
    /** @use HasFactory<\Database\Factories\UserFactory> */
    use HasApiTokens, HasFactory, Notifiable;

    /**
     * The attributes that are mass assignable.
     *
     * @var list<string>
     */
    protected $table = 'usuarios';
    protected $primaryKey = 'id_usuario';
    public $timestamps = false;//No se guardan datos de tiempo
    protected $fillable = [
        'nombre',
        'email',
        'password',
        'id_rol'
    ];

    /**
     * The attributes that should be hidden for serialization.
     *
     * @var list<string>
     */
    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * Get the attributes that should be cast.
     *
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'password' => 'hashed',
        ];
    }
    public function rol()//Relacion de tabla usuarios a rol
    {
        return $this->belongsTo(Roles::class, 'id_rol');
    }
    public function datosUsuario()//Relacion de tabla usuarios a datos
    {
        return $this->hasOne(DatosUsuario::class, 'id_usuario','id_usuario');
    }
    public function getFilamentName(): string 
    {//Funcion para recuperar el nombre del usuario 
        return $this->nombre;
    }
    public function canAccessPanel(Panel $panel): bool
    { //Validación
        if ($panel->getId() === 'admin') {
            return $this->id_rol === 1;//Si el id_rol es 1 se da acceso al panel admin
        }elseif($panel->getId() === 'veterinario'){
            return $this->id_rol === 2;//Si el id_rol es 2 entonces da acceso al panel veterinario
        }elseif($panel->getId() === 'usuario'){
            return $this->id_rol === 3;//Si el id_rol es 3 entonces da acceso al panel usuario
        }
        return false;
    }
}
