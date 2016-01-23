/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpl.games;

import com.rubii.objects.ConfiguracionGlobal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;

/**
 *
 * @author gbern
 */
public class GameModesController implements Initializable {

    @FXML
    Button btstartsolo, btstartchallenge;

    @FXML
    AnchorPane anchorpane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btstartsolo.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                RubikFX rfx = new RubikFX(new ConfiguracionGlobal(), FXMLDocumentController.service);
                rfx.start(new Stage());
            }
        });

        btstartchallenge.setDisable(FXMLDocumentController.service == null);
        btstartchallenge.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
//                KeyValue xValue = new KeyValue(anchorpane.scaleXProperty(), 100);
//                KeyValue yValue = new KeyValue(anchorpane.scaleYProperty(), 100);
//
//                KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), xValue, yValue);
//                Timeline timeline  = new Timeline(); 
//timeline.setCycleCount(Timeline.INDEFINITE); 
//timeline.setAutoReverse(true); 
//timeline.getKeyFrames().addAll(keyFrame); 
//timeline.play();
                ConfiguracionGlobal config = new ConfiguracionGlobal();
                config.multijugador = true;
                try {
                    String ip = JOptionPane.showInputDialog(null, "ip");
                    if (ip.contains(":")) {
                        config.ip = ip.split(":")[0];
                        config.PORT = Integer.parseInt(ip.split(":")[1]);
                    } else {
                        config.ip = ip;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Puerto no valido, usando puerto por defecto (199)");
                }
                RubikFX rfx = new RubikFX(FXMLDocumentController.config,FXMLDocumentController.service);
                rfx.start(new Stage());
            }
        });
    }

}
