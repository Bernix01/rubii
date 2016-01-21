/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rubii.remote.server;

import java.io.Serializable;

/**
 *
 * @author gbern
 */
public class Puntuacion implements Serializable{
    private long puntuacion;
    private String usuario;

    protected Puntuacion() {
    }

    public Puntuacion(long puntuacion, String usuario) {
        this.puntuacion = puntuacion;
        this.usuario = usuario;
    }

    public long getPuntuacion() {
        return puntuacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    protected void setPuntuacion(long puntuacion) {
        this.puntuacion = puntuacion;
    }

    
    
}
