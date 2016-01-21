/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rubii.remote.server;

import com.jpl.games.model.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author gbern
 */
public class RemoteImpl extends UnicastRemoteObject implements RemoteMinion {

    private static final String ARCHIVO_RETOS = "/srv/retos.ja";
    HashMap<Integer, LinkedList<Puntuacion>> puntuaciones;
    ArrayList<String> retos;
    int jugadores = 0;
    long ultimaSincronizacion; //Ultima vez que se sincronizaron los archivos con los datos en memoria
    private String last = "V", get = "V", reto;

    public RemoteImpl() throws RemoteException {
        super();
        puntuaciones = new HashMap<>();
        LinkedList<Puntuacion> tmp;
        for (int i = 1; i < 101; i++) {
            tmp = new LinkedList<>();
            puntuaciones.put(i, tmp);
        }
        retos = new ArrayList<>();
        sincronizar();
        
    }

    @Override
    public LinkedList<Puntuacion> getTablero(int reto)  throws RemoteException {
        return puntuaciones.get(reto);
    }

    @Override
    public String getReto(int reto)  throws RemoteException {
        return retos.get(reto);
    }

    @Override
    public boolean guardarPuntuacion(Puntuacion p, int reto) throws RemoteException  {
        if (reto > 100 || reto < 1) {
            //TODO lanzar exception
            System.out.println("Puntacion " + p + " no guardada, reto invalido.");
            return false;
        }
        puntuaciones.get(reto).add(p);
        if (System.currentTimeMillis() - ultimaSincronizacion > 900000 || jugadores < 5) {
            sincronizar();
        }
        return true;
    }

    @Override
    public int getCantidadJugadores() throws RemoteException  {
        return jugadores;
    }

    private void sincronizar() {
        if (retos.isEmpty()) {
            cargar();
        }
        if (!puntuaciones.isEmpty() && puntuaciones.values().stream().anyMatch(puntos -> !puntos.isEmpty())) {
            guardar();
        }
    }

    private void cargar() {
        FileReader fr;
        BufferedReader br;
        String linea;
        String datos[];
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
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Archivo con mal formato");
        } catch (FileNotFoundException ex) {
            System.out.println("El archivo no existe, generando uno nuevo...");
            generar();
        } catch (IOException ex) {
            System.out.println("Error de lectura de archivo!!");
        }
        cargarPuntuaciones();
    }

    private void guardar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void generar() {
        retos.clear();
        for (int i = 0; i < 100; i++) {
            reto = doScramble();
            if(retos.size() >0 && retos.stream().anyMatch(reto1 -> reto1.equals(reto))){
                while(retos.stream().anyMatch(reto1 -> reto1.equals(reto))){
                    reto = doScramble();
                }
            }
            retos.add(0, reto);
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
        File folder = new File("srv/puntuaciones/");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles.length == 0) {
            return;
        }
        for (File file : listOfFiles) {
            FileReader fr;
            BufferedReader br;
            String linea;
            String datos[];
            int retotmp;
            Puntuacion p;
            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                retotmp = Integer.parseInt(br.readLine());
                while ((linea = br.readLine()) != null) {
                    datos = linea.split(":");
                    p = new Puntuacion();
                    p.setUsuario(datos[0]);
                    p.setPuntuacion(Long.parseLong(datos[1]));
                    puntuaciones.get(retotmp).add(p);
                }
                br.close();
                fr.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(RemoteImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | NumberFormatException ex) {
                Logger.getLogger(RemoteImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public String getRetoAleatorio()  throws RemoteException {
        return retos.get(ThreadLocalRandom.current().nextInt(0, 101));
    }

}
