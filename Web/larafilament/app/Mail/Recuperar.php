<?php

namespace App\Mail;

use Illuminate\Bus\Queueable;
use Illuminate\Mail\Mailable;
use Illuminate\Queue\SerializesModels;

class Recuperar extends Mailable
{
    use Queueable, SerializesModels;

    public $token;
    public $email;
    public $resetUrl;

    /**
     * Create a new message instance.
     */
    public function __construct($token, $email)
    {
        $this->token = $token;
        $this->email = $email;

        $url = url('/password/reset/' . $this->token) . '?email=' . urlencode($this->email);
        $this->resetUrl = preg_replace('/:\/\/[^\/]+/', '://127.0.0.1:8000', $url);
    }

    /**
     * Build the message.
     */
    public function build()
    {
        return $this->from(config('mail.from.address'), config('mail.from.name'))
                    ->subject('Recuperación de contraseña')
                    ->view('emails.recuperar')
                    ->with([
                        'resetUrl' => $this->resetUrl,
                    ]);
    }
}
