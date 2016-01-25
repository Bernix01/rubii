/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpl.games;

import com.rubii.objects.ConfiguracionGlobal;
import com.rubii.remote.server.RemoteMinion;
import java.io.IOException;
import java.net.URL;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrador
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    Button btstart, btinstruc, btRanking, btconnect;    //@FXML SIRVE PARA IMPORTAR CUALQUIER OBJETO DEL SCENE BUILDER

    private static Registry registry;
    public static RemoteMinion service;
    public static ConfiguracionGlobal config;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        config = new ConfiguracionGlobal();
        connect();
        //TAREA A REALIZAR AL HACER CLIC DEL BOTON
        btstart.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/com/jpl/games/GameModes.fxml"));

                    Scene scnInstruc = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scnInstruc);
                    stage.getIcons().add(new Image("/imagenes/rubik_s_cube.png"));
                    stage.setTitle("Rubik´s Cube - Select Gamemode");
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.println("NO SE PUDO HCAFASDC");
                }

            }
        });

        btinstruc.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                try {
                    Parent root = FXMLLoader.load(getClass().getResource("Instrucciones.fxml"));

                    Scene scnInstruc = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scnInstruc);
                    stage.getIcons().add(new Image("/imagenes/rubik_s_cube.png"));
                    stage.setTitle("Rubik´s Cube - Instrucctions");
                    stage.show();
                } catch (IOException ex) {
                    System.out.println("NO SE PUDO HCAFASDC");
                }
            }
        });

        btRanking.setDisable(service == null);
        btRanking.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Leaderboard.fxml"));
                    Parent root = (Parent) fxmlLoader.load();
                    LeaderboardController controller = fxmlLoader.<LeaderboardController>getController();
                    controller.load(new LinkedList<>(), service);
                    Scene scnInstruc = new Scene(root);
                    Stage stage1 = new Stage();
                    stage1.setScene(scnInstruc);
                    stage1.getIcons().add(new Image("/imagenes/rubik_s_cube.png"));
                    stage1.setTitle("Rubik´s Cube - Ranking");
                    stage1.show();
                } catch (IOException ex) {
                    System.out.println("NO SE PUDO HCAFASDC");
                }

            }
        });
        btconnect.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                config.ip = JOptionPane.showInputDialog("Ingrese ip del servidor maestro:");
                connect();
                btRanking.setDisable(service == null);
            }
        });
    }

    private void connect() {
        try {
            registry = LocateRegistry.getRegistry(config.ip, config.PORT);
            service = (RemoteMinion) registry.lookup("Minion");
            System.out.println("Conectado!");

        } catch (ConnectException ex){
            System.err.println("Servidor no encontrado, iniciando en modo fuera de linea...");
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(RubikFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
