/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rubii.remote.server;

import java.io.Serializable;
import static java.lang.Math.pow;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import javafx.util.Duration;

/**
 *
 * @author gbern
 */
public class Puntuacion implements Serializable, Comparable<Puntuacion> {

    private long puntuacion;
    private String usuario;

    
    public void setReto(int reto) {
        this.reto = reto;
    }
    private int reto;

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

    public int getReto() {
        return reto;
    }

    @Override
    public String toString() {
        
        String durationString = LocalTime.ofNanoOfDay(puntuacion).format(Server.fmt);
        return usuario + "\t" + durationString + "\t" + (reto + 1);
    }

    public String paGuardar() {
        return puntuacion + "," + usuario + "," + reto;
    }

    @Override
    public int compareTo(Puntuacion o) {
        return Long.compare(puntuacion, o.puntuacion);
    }

}
