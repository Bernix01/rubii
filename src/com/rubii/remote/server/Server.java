/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rubii.remote.server;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author gbern
 */
public class Server {
    private static final int PORT = 199;
    private static Registry registry;
    
    public static void startRegisstry() throws RemoteException{
        registry = LocateRegistry.createRegistry(PORT);
    }
    
    public static void registerObject(String name, Remote remoteObj) throws RemoteException, AlreadyBoundException {
        registry.bind(name, remoteObj);
        System.out.println("registered " + name);
    }
    public static void main(String[] args) throws Exception {
        System.out.println("Server starting... ");
        startRegisstry();
        registerObject(RemoteImpl.class.getSimpleName(), new RemoteImpl());
        System.out.println("Server started!");
    }
}
