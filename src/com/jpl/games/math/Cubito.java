/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpl.games.math;

import java.util.LinkedList;

/**
 *
 * @author gbern
 */
public class Cubito {
    private int posicion;
    private final LinkedList<Cara> caras;

    public Cubito(int posicion, LinkedList<Cara> caras) {
        this.posicion = posicion;
        this.caras = caras;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
    
    
}
