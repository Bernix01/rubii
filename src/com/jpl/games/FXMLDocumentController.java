/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpl.games;

import java.io.IOException;
import java.net.URL;
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

/**
 *
 * @author Administrador
 */
public class FXMLDocumentController implements Initializable {
        @FXML Button btstart , btinstruc,btRanking;    //@FXML SIRVE PARA IMPORTAR CUALQUIER OBJETO DEL SCENE BUILDER
    
   
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TAREA A REALIZAR AL HACER CLIC DEL BOTON
        btstart.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                    
                     RubikFX rfx= new RubikFX();
                     rfx.start(new Stage());
            }
        });
        
        
       btinstruc.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                    
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("Instrucciones.fxml"));
                    
                    Scene scnInstruc = new Scene(root);
                    Stage stage=new Stage();
                    stage.setScene(scnInstruc);
                    stage.getIcons().add(new Image("/imagenes/rubik_s_cube.png"));
                    stage.setTitle("Rubik´s Cube - Instrucctions");
                    stage.show();
                } catch (IOException ex) {
                    System.out.println("NO SE PUDO HCAFASDC");
                }
            }
       });
       
       btRanking.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                    
                     try {
                    Parent root = FXMLLoader.load(getClass().getResource("Ranking.fxml"));
                    
                    Scene scnRanking = new Scene(root);
                    Stage stage=new Stage();
                    stage.setScene(scnRanking);
                    stage.getIcons().add(new Image("/imagenes/rubik_s_cube.png"));
                    stage.setTitle("Rubik´s Cube - Worldwide Ranking");
                    stage.show();
                } catch (IOException ex) {
                    System.out.println("NO SE PUDO HCAFASDC");
                }
            
                     
                     
                     
                     
            }
        });
       
       
       
    }
    
}
            
       