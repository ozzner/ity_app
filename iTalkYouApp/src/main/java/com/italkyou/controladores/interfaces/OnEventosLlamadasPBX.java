package com.italkyou.controladores.interfaces;

/**
 * Created by Moises on 09/02/2015.
 */
public interface OnEventosLlamadasPBX {

     void llamadaColgadaUsuario();
     void llamadaEntrante(String nroEntrante);
     void llamadaContestada();
     void llamadaColgada();
     void anexoRegistrado();
     void anexoRegistrando();
     void anexoDesregistrado();
}
