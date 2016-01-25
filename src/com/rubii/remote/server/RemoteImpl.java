/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rubii.remote.server;

import com.jpl.games.model.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author gbern
 */
public class RemoteImpl extends UnicastRemoteObject implements RemoteMinion {

    private static final String ARCHIVO_RETOS = "srv/retos.ja";
    HashMap<Integer, LinkedList<Puntuacion>> puntuaciones;
    ArrayList<String> retos;
    int jugadores = 0;
    long ultimaSincronizacion; //Ultima vez que se sincronizaron los archivos con los datos en memoria
    private String last = "V", get = "V", reto;
    private String ARCHIVO_PUNTUACIONES = "srv/puntuaciones.ja";

    public RemoteImpl() throws RemoteException {
        super();
        puntuaciones = new HashMap<>();
        LinkedList<Puntuacion> tmp;
        for (int i = 0; i < 100; i++) {
            tmp = new LinkedList<>();
            puntuaciones.put(i, tmp);
        }
        retos = new ArrayList<>();
        sincronizar();

    }

    @Override
    public LinkedList<Puntuacion> getTablero(int reto) throws RemoteException {
        return puntuaciones.get(reto);
    }

    @Override
    public HashMap<Integer, LinkedList<Puntuacion>> getTablero() throws RemoteException {
        return puntuaciones;
    }

    @Override
    public String getReto(int reto) throws RemoteException {
        return retos.get(reto);
    }

    @Override
    public LinkedList<Puntuacion> guardarPuntuacion(Puntuacion p, int reto) throws RemoteException {
        if (reto >= 100 || reto < 0) {
            //TODO lanzar exception
            System.out.println("Puntacion " + p + " no guardada, reto invalido.");
            return null;
        }
        puntuaciones.get(reto).add(p);
        System.out.println("puntuacion a guardar: \n"+p+ "\n  del reto: "+ reto);
        if (System.currentTimeMillis() - ultimaSincronizacion > 900000 || jugadores < 5) {
            sincronizar();
        }
        return puntuaciones.get(reto);
    }

    @Override
    public int getCantidadJugadores() throws RemoteException {
        return jugadores;
    }

    private void sincronizar() {
        if (retos.isEmpty()) {
            cargar();
        }
        guardar();
    }

    private void cargar() {
        FileReader fr;
        BufferedReader br;
        String linea;
        Puntuacion p;
        ArrayList<String> retostmp = new ArrayList<>(100);
        try {
            fr = new FileReader(ARCHIVO_RETOS);
            br = new BufferedReader(fr);
            int cantidadRetos = 0;
            while ((linea = br.readLine()) != null && cantidadRetos < 100) {
                retostmp.add(linea);
                cantidadRetos++;
            }
            retos = retostmp;
            fr.close();
            br.close();

        } catch (FileNotFoundException ex) {
            System.out.println("El archivo no existe, generando uno nuevo...");
            generar();
        } catch (IOException ex) {
            System.out.println("Error de lectura de archivo!!");
        }
        cargarPuntuaciones();
    }

    private void guardar() {
        System.out.println("Guardando...");
        guardarRetos();
        if (!puntuaciones.isEmpty() && puntuaciones.values().stream().anyMatch(puntos -> !puntos.isEmpty())) {
            guardarPuntuaciones();
        }
    }

    private void generar() {
        retos.clear();
        for (int i = 0; i < 100; i++) {
            reto = doScramble();
            if (retos.size() > 0 && retos.stream().anyMatch(reto1 -> reto1.equals(reto))) {
                while (retos.stream().anyMatch(reto1 -> reto1.equals(reto))) {
                    reto = doScramble();
                }
            }
            retos.add(reto);
        }
        
    }

    public String doScramble() {
        StringBuilder sb = new StringBuilder();
        final List<String> movements = Utils.getMovements();
        IntStream.range(0, 25).boxed().forEach(i -> {
            while (last.substring(0, 1).equals(get.substring(0, 1))) {
                // avoid repeating the same/opposite rotations
                get = movements.get((int) (Math.floor(Math.random() * movements.size())));
            }
            last = get;
            if (get.contains("2")) {
                get = get.substring(0, 1);
                sb.append(get).append(" ");
            }
            sb.append(get).append(" ");
        });

        return sb.toString();
    }

    private void cargarPuntuaciones() {
        FileReader fr;
        BufferedReader br;
        String linea;
        String datos[];
        int retotmp;
        Puntuacion p;
        try {
            fr = new FileReader(ARCHIVO_PUNTUACIONES);
            br = new BufferedReader(fr);
            while ((linea = br.readLine()) != null) {
                datos = linea.split(",");
                p = new Puntuacion();
                p.setUsuario(datos[1]);
                p.setPuntuacion(Long.parseLong(datos[0]));
                retotmp = Integer.parseInt(datos[2]);
                p.setReto(retotmp);
                puntuaciones.get(retotmp).add(p);
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException ex) {
            System.out.println("El archivo de puntuaciones no existe :p");
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(RemoteImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void guardarRetos() {
        System.out.println("Guardando retos...");
        String fileName = ARCHIVO_RETOS;

        try {
            // Assume default encoding.
            FileWriter fileWriter
                    = new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter
                    = new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            retos.stream().forEachOrdered(reto1 -> {
                try {
                    bufferedWriter.write(reto1);
                    bufferedWriter.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(RemoteImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println(
                    "Error writing to file '"
                    + fileName + "'");
            // Or we could just do this:
            ex.printStackTrace();
        } finally {

        }
    }

    private void guardarPuntuaciones() {
        System.out.println("Guardando puntuaciones...");
        String fileName = ARCHIVO_PUNTUACIONES;

        try {
            // Assume default encoding.
            FileWriter fileWriter
                    = new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter
                    = new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
                puntuaciones.values().forEach(puntuacion1 -> {
                    
                    puntuacion1.forEach(puntuacion -> {
                        
                    try {
                        bufferedWriter.write(puntuacion.paGuardar());
                        bufferedWriter.newLine();
                    } catch (IOException ex) {
                        Logger.getLogger(RemoteImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    });
                });
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println(
                    "Error writing to file '"
                    + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        } finally {

        }
    }

    @Override
    public LinkedList<Puntuacion> getTablero(String usuario) throws RemoteException {
        LinkedList<Puntuacion> resultados = new LinkedList<>();
        puntuaciones.values().stream().filter(listaPuntuaciones -> listaPuntuaciones.stream().anyMatch(p -> p.getUsuario().equals(usuario))).forEach(lista ->{
            lista.stream().filter(p -> p.getUsuario().equals(usuario)).forEach(puntuacion ->{
                resultados.add(puntuacion);
            });
        });
        return resultados;
    }

    @Override
    public LinkedList<Puntuacion> getTableroTiempo(int seconds) throws RemoteException {
        long tiempoT = seconds*(1000);
          LinkedList<Puntuacion> resultados = new LinkedList<>();
        puntuaciones.values().stream().filter(listaPuntuaciones -> listaPuntuaciones.stream().anyMatch(p -> p.getPuntuacion() < tiempoT)).forEach(lista ->{
            lista.stream().filter(p -> p.getPuntuacion() < tiempoT).forEach(puntuacion ->{
                resultados.add(puntuacion);
            });
        });
        return resultados;  
    }

}
