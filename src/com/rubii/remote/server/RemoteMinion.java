/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rubii.remote.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author gbern
 */
public interface RemoteMinion extends Remote {

    public LinkedList<Puntuacion> getTablero(int reto) throws RemoteException;

    public String getReto(int reto) throws RemoteException;

    public boolean guardarPuntuacion(Puntuacion p, int reto) throws RemoteException;

    public int getCantidadJugadores() throws RemoteException;

    public String getRetoAleatorio() throws RemoteException;
}
