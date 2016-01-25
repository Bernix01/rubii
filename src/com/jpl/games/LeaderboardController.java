/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpl.games;

import com.rubii.remote.server.Puntuacion;
import com.rubii.remote.server.RemoteMinion;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author gbern
 */
public class LeaderboardController implements Initializable {

    @FXML
    ListView<Puntuacion> lista;
    @FXML
    Button bport, bporusuario, bporreto;

    LinkedList<Puntuacion> tablero;
    ObservableList<Puntuacion> items;
    RemoteMinion service;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        bport.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    tablero = service.getTableroTiempo(Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el tiempo en segundos")));
                    if(tablero == null || tablero.isEmpty())
                        JOptionPane.showMessageDialog(null, "No se encontraron resultados");
                    refrescarLista();
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexion con el servidor maestro.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Valor ingresado no valido.");
                }
            }
        });
        bporusuario.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    tablero = service.getTablero(JOptionPane.showInputDialog(null, "Ingrese el usuario"));
                    if(tablero == null || tablero.isEmpty())
                        JOptionPane.showMessageDialog(null, "No se encontraron resultados");
                    refrescarLista();
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexion con el servidor maestro.");
                }
            }
        });
        bporreto.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    tablero = service.getTablero(Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el reto"))-1);
                    if(tablero == null || tablero.isEmpty())
                        JOptionPane.showMessageDialog(null, "No se encontraron resultados");
                    refrescarLista();
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexion con el servidor maestro.");
                }
            }
        });
    }

    public void load(LinkedList<Puntuacion> tablero, RemoteMinion service) {
        this.tablero = tablero;
        this.service = service;
        
        refrescarLista();
    }

    public void refrescarLista() {
        Collections.sort(tablero);
        items = FXCollections.observableArrayList(tablero);
        lista.setItems(items);
    }

}
