package com.jpl.games;

import com.jpl.games.model.Move;
import com.jpl.games.model.Moves;
import com.jpl.games.model.Rubik;
import com.rubii.objects.ConfiguracionGlobal;
import com.rubii.remote.server.Puntuacion;
import com.rubii.remote.server.RemoteImpl;
import com.rubii.remote.server.RemoteMinion;
import com.rubii.remote.server.Server;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author jpereda, April 2014 - @JPeredaDnr
 */
public class RubikFX extends Application {

    private final BorderPane pane = new BorderPane();
    private Rubik rubik;

    private LocalTime time = LocalTime.now();
    private Timeline timer;

    private final StringProperty clock = new SimpleStringProperty("00:00:00");
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    private Button btnHover;

    private Moves moves = new Moves();

    private ConfiguracionGlobal config;
    private RemoteMinion service;

    RubikFX(ConfiguracionGlobal configuracionGlobal, RemoteMinion service) {
        this.config = configuracionGlobal;
        this.service = service;
    }

    @Override
    public void start(Stage stage) {
        /*
         Import Rubik's Cube
         */
        rubik = new Rubik();

        /*
         Toolbars with buttons
         */
        ToolBar tbTop = new ToolBar(new Button("Y"), new Button("Yi"), new Button("Z"),
                new Button("Zi"), new Separator(), new Button("U"),
                new Button("Ui"), new Button("F"), new Button("Fi"), new Separator());

        // cambiar botones por imagenes  UP < 64px
        Button bty = (Button) tbTop.getItems().get(0);
        bty.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bty.setGraphic(new ImageView(new Image("/imagenes/Axis-verde-horario.png")));

        Button btyi = (Button) tbTop.getItems().get(1);
        btyi.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        btyi.setGraphic(new ImageView(new Image("/imagenes/Axis-verde-antihorario.png")));

        Button btz = (Button) tbTop.getItems().get(2);
        btz.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        btz.setGraphic(new ImageView(new Image("/imagenes/Axis-azul-horario.png")));

        Button btzi = (Button) tbTop.getItems().get(3);
        btzi.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        btzi.setGraphic(new ImageView(new Image("/imagenes/Axis-azul-antihorario.png")));

        Button bup = (Button) tbTop.getItems().get(5);
        bup.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");

        tbTop.setId("tool-bar");
        bup.setGraphic(new ImageView(new Image("/imagenes/bolita-blanca-horaria.gif")));

        /*tbTop.getStylesheets().add("estilos.css");*/
        Button bupi = (Button) tbTop.getItems().get(6);
        bupi.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bupi.setGraphic(new ImageView(new Image("/imagenes/bolita-blanca-antihorarian.gif")));

        Button bFront = (Button) tbTop.getItems().get(7);
        bFront.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bFront.setGraphic(new ImageView(new Image("/imagenes/bolita-azul-horaria.gif")));

        Button bFronti = (Button) tbTop.getItems().get(8);
        bFronti.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bFronti.setGraphic(new ImageView(new Image("/imagenes/bolita-azul-antihoraria.gif")));

        //--------------------------------------------------------------
        Button bReset = new Button("Restart");
        bReset.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bReset.setDisable(true);
        bReset.setOnAction(e -> {
            if (moves.getNumMoves() > 0) {

                moves.getMoves().clear();
                rubik.doReset();
            }
        });
        ChangeListener<Number> clockLis = (ov, l, l1) -> clock.set(LocalTime.ofNanoOfDay(l1.longValue()).format(fmt));
        Button bReplay = new Button("Replay");
        bReplay.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bReplay.setDisable(true);
        rubik.isOnReplaying().addListener((ov, b, b1) -> {
            if (b && !b1) {
                rubik.getTimestamp().removeListener(clockLis);
                if (!rubik.isSolved().get()) {
                    timer.play();
                }
            }
        });
        bReplay.setOnAction(e -> {
            timer.stop();
            rubik.getTimestamp().addListener(clockLis);
            doReplay();
        });
        Button bSeq = new Button("Sequence");
        bSeq.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bSeq.setOnAction(e -> {
            String response;
            if (moves.getNumMoves() > 0) {
                response = Dialogs.create()
                        .owner(stage)
                        .title("Warning Dialog")
                        .masthead("Loading a Sequence").lightweight()
                        .message("Add a valid sequence of movements:\n(previous movements will be discarded)")
                        .showTextInput(moves.getSequence());

            } else {
                response = Dialogs.create()
                        .owner(stage)
                        .title("Information Dialog")
                        .masthead("Loading a Sequence").lightweight()
                        .message("Add a valid sequence of movements")
                        .showTextInput();
            }
            System.out.println("r: " + response);
            if (response != null && !response.isEmpty()) {
                rubik.doReset();
                rubik.doSequence(response.trim());
            }
        });
        Label lSolved = new Label("Solved");
        lSolved.setVisible(false);
        Label lSimulated = new Label();
        lSimulated.textProperty().bind(rubik.getPreviewFace());

        Button bSc = new Button("Scramble");
        bSc.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bSc.setOnAction(e -> {
            if (moves.getNumMoves() > 0) {
                Action response = Dialogs.create()
                        .owner(stage)
                        .title("Warning Dialog")
                        .masthead("Scramble Cube")
                        .message("You will lose all your previous movements. Do you want to continue?")
                        .showConfirm();
                if (response == Dialog.Actions.YES) {
                    rubik.doReset();
                    doScramble();
                }
            } else {
                doScramble();
            }
        });

        Button bSolve = new Button("Solve");
        bSolve.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bSolve.setDisable(true);
        bSolve.setOnAction(e -> {
            doSolve();

        });

        if (!config.multijugador) {

            tbTop.getItems().addAll(new Separator(), bReset, bSc, bReplay, bSeq, lSolved,
                    new Separator(), bSolve, new Separator(), lSimulated);

        } else {

            tbTop.getItems().addAll(new Separator(), bReset, bReplay, bSeq, lSolved,
                    new Separator(), new Separator(), lSimulated);
            
            try {
                rubik.doSequence(service.getReto(1));
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(null, "Error de conexion con el servidor maestro");
            }

        }
        pane.setTop(tbTop);

        //Panel de abajo
        ToolBar tbBottom = new ToolBar(new Button("B"), new Button("Bi"), new Button("D"),
                new Button("Di"));//new Button("E"),new Button("Ei"));
        //****************
        tbBottom.setId("tool-bar");
        //Botones panel de abajo
        Button btBack = (Button) tbBottom.getItems().get(0);

        btBack.setGraphic(new ImageView(new Image("/imagenes/bolita-verde-horaria.gif")));
        //*******************CAMBIAR COLOR LETRA
        btBack.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        //*************************************************************

        /*btbBack.getStyleClass().addAll("first");*/
        Button btBacki = (Button) tbBottom.getItems().get(1);
        btBacki.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        btBacki.setGraphic(new ImageView(new Image("/imagenes/bolita-verde-antihoraria.gif")));

        Button btDown = (Button) tbBottom.getItems().get(2);
        btDown.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        btDown.setGraphic(new ImageView(new Image("/imagenes/bolita-amarilla-horaria.gif")));

        Button btDowni = (Button) tbBottom.getItems().get(3);
        btDowni.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        btDowni.setGraphic(new ImageView(new Image("/imagenes/bolita-amarilla-antihoraria.gif")));

        //Contador Movimientos
        Label lMov = new Label();
        rubik.getCount().addListener((ov, v, v1) -> {
            bReset.setDisable(moves.getNumMoves() == 0);
            bReplay.setDisable(moves.getNumMoves() == 0);
            if (!config.multijugador) {
                bSolve.setDisable(moves.getNumMoves() == 0);
            }
            lMov.setText("Movements: " + (v1.intValue() + 1));
        });
        rubik.getLastRotation().addListener((ov, v, v1) -> {
            if (!rubik.isOnReplaying().get() && !v1.isEmpty()) {
                moves.addMove(new Move(v1, LocalTime.now().minusNanos(time.toNanoOfDay()).toNanoOfDay()));
            }
        });
        //Contador hora
        Label lTime = new Label();
        lTime.textProperty().bind(clock);
        tbBottom.getItems().addAll(new Separator(), lMov, new Separator(), lTime);
        pane.setBottom(tbBottom);
        tbBottom.prefWidthProperty().bind(pane.heightProperty());

        //Panel derecho
        ToolBar tbRight = new ToolBar(new Button("R"), new Button("Ri"), new Separator(),
                new Button("X"), new Button("Xi"));

        //ToolBar tbRight= new ToolBar(new Separator(),new Button("R"),new Button("Ri"),new Separator());
        tbRight.setOrientation(Orientation.VERTICAL);
        tbRight.setId("my-toolbar");

        pane.setRight(tbRight);

        //botones panel derecho
        Button btRight = (Button) tbRight.getItems().get(0);
        btRight.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        btRight.setGraphic(new ImageView(new Image("/imagenes/bolita-naranja-horaria.gif")));

        Button btRighti = (Button) tbRight.getItems().get(1);
        btRighti.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        btRighti.setGraphic(new ImageView(new Image("/imagenes/bolita-naranja-antihoraria.gif")));

        Button bx = (Button) tbRight.getItems().get(3);
        bx.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bx.setGraphic(new ImageView(new Image("/imagenes/Axis-rojo-horario.png")));

        Button bxi = (Button) tbRight.getItems().get(4);
        bxi.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        bxi.setGraphic(new ImageView(new Image("/imagenes/Axis-rojo-antihorario.gif")));

        //Panel izquierdo
        /*ToolBar tbLeft=new ToolBar(new Button("L"),new Button("Li"),new Button("M"),
         new Button("Mi"),new Button("S"),new Button("Si"));
         */
        ToolBar tbLeft = new ToolBar(new Button("L"), new Button("Li"));
        tbLeft.setOrientation(Orientation.VERTICAL);
        tbLeft.setId("my-toolbar");

        pane.setLeft(tbLeft);

        //Botones panel izquierdo
        Button btLeft = (Button) tbLeft.getItems().get(0);
        btLeft.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");

        btLeft.setGraphic(new ImageView(new Image("/imagenes/bolita-roja-horaria.gif")));

        Button btLefti = (Button) tbLeft.getItems().get(1);
        btLefti.setStyle("-fx-text-fill: white; -fx-font-family: \"Helvetica\";");
        btLefti.setGraphic(new ImageView(new Image("/imagenes/bolita-roja-antihoraria.gif")));

        pane.setCenter(rubik.getSubScene());

        // PONER FONDO AL CUBO
        pane.setStyle("-fx-background-image: url(\"/imagenes/fondo-vectorial12.jpg\");-fx-background-size: 1500, 1000;-fx-background-repeat: no-repeat;");
//******************************
        pane.getChildren().stream()
                .filter(withToolbars())
                .forEach(tb -> {
                    ((ToolBar) tb).getItems().stream()
                    .filter(withMoveButtons())
                    .forEach(n -> {
                        Button b = (Button) n;
                        b.setOnAction(e -> rotateFace(b.getText()));
                        b.hoverProperty().addListener((ov, b0, b1) -> updateArrow(b.getText(), b1));
                    });
                });

        rubik.isOnRotation().addListener((b0, b1, b2) -> {
            if (b2) {
                // store the button hovered 
                pane.getChildren().stream().filter(withToolbars())
                        .forEach(tb -> {
                            ((ToolBar) tb).getItems().stream().filter(withMoveButtons().and(isButtonHovered()))
                            .findFirst().ifPresent(n -> btnHover = (Button) n);
                        });
            } else {
                if (rubik.getPreviewFace().get().isEmpty()) {
                    btnHover = null;
                } else {
                    // after rotation
                    if (btnHover != null && !btnHover.isHover()) {
                        updateArrow(btnHover.getText(), false);
                    }
                }
            }
        });

        // disable rest of buttons to avoid new hover events
        rubik.isOnPreview().addListener((b0, b1, b2) -> {
            final String face = rubik.getPreviewFace().get();
            pane.getChildren().stream().filter(withToolbars())
                    .forEach(tb -> {
                        ((ToolBar) tb).getItems().stream().filter(withMoveButtons())
                        .forEach((b) -> {
                            b.setDisable(!b2 || face.isEmpty() || face.equals("V") ? false
                                            : !face.equals(((Button) b).getText()));
                        });
                    });
        });

        timer = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            clock.set(LocalTime.now().minusNanos(time.toNanoOfDay()).format(fmt));
        }), new KeyFrame(Duration.seconds(1)));
        timer.setCycleCount(Animation.INDEFINITE);
        rubik.isSolved().addListener((ov, b, b1) -> {
            if (b1) {
                lSolved.setTextFill(Color.WHITE);
                lSolved.setVisible(true);
                timer.stop();
                moves.setTimePlay(LocalTime.now().minusNanos(time.toNanoOfDay()).toNanoOfDay());
                JOptionPane.showMessageDialog(null, moves);
                if (config.multijugador) {
                    Puntuacion p = new Puntuacion(moves.getTimePlay(), JOptionPane.showInputDialog(null, "Nombre de jugador: "));
                    System.out.println(p);
                    try {
                        LinkedList<Puntuacion> tablero = service.guardarPuntuacion(p, config.reto);
                        System.out.println(tablero);

                        try {

                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Leaderboard.fxml"));
                            Parent root = (Parent)fxmlLoader.load(); 
                            LeaderboardController controller = fxmlLoader.<LeaderboardController>getController();
                           controller.load(tablero,service);
                            Scene scnInstruc = new Scene(root);
                            Stage stage1 = new Stage();
                            stage1.setScene(scnInstruc);
                            stage1.getIcons().add(new Image("/imagenes/rubik_s_cube.png"));
                            stage1.setTitle("Rubik´s Cube - Ranking");
                            stage1.show();
                        } catch (IOException ex) {
                            System.out.println("NO SE PUDO HCAFASDC");
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(null, "No se pudo guardar la puntuacion.");
                    }
                }

            } else {
                lSolved.setVisible(false);
            }
        });

        final Scene scene = new Scene(pane, 1200, 780, true);
        pane.prefWidthProperty().bind(scene.widthProperty());
        pane.prefWidthProperty().bind(scene.heightProperty());

        scene.addEventHandler(MouseEvent.ANY, rubik.eventHandler);
        scene.cursorProperty().bind(rubik.getCursor());
        scene.setFill(Color.DARKSLATEGRAY);

        /**
         * ********AGREGAR ESTILO A SCENE
         */
        scene.getStylesheets().addAll(getClass().getResource("toolbar.css").toString());
        pane.setId("pane");
        stage.setTitle("Rubik's Cube - Animation");
        stage.setScene(scene);
        stage.getIcons().add(new Image("/imagenes/icono.png"));
        stage.show();
    }

    // called on button click
    private void rotateFace(final String btRot) {
        pane.getChildren().stream()
                .filter(withToolbars())
                .forEach(tb -> {
                    ((ToolBar) tb).getItems().stream()
                    .filter(withMoveButtons().and(withButtonTextName(btRot)))
                    .findFirst().ifPresent(n -> rubik.isHoveredOnClick().set(((Button) n).isHover()));
                });
        rubik.rotateFace(btRot);
    }

    // called on button hover
    private void updateArrow(String face, boolean hover) {
        rubik.updateArrow(face, hover);
    }

    // called from button Scramble
    private void doScramble() {
        pane.getChildren().stream().filter(withToolbars()).forEach(setDisable(true));
        rubik.doScramble();
        rubik.isOnScrambling().addListener((ov, v, v1) -> {
            if (v && !v1) {
                System.out.println("scrambled!");
                pane.getChildren().stream().filter(withToolbars()).forEach(setDisable(false));
                moves = new Moves();
                time = LocalTime.now();
                timer.playFromStart();
            }
        });
    }

    //called from button Solve
    private void doSolve() {
        pane.getChildren().stream().filter(withToolbars()).forEach(setDisable(true));
        rubik.doSolve();
        rubik.isOnSolving().addListener((ov, v, v1) -> {
            if (v && !v1) {
                System.out.println("Solved!");
                pane.getChildren().stream().filter(withToolbars()).forEach(setDisable(false));

            }
        });

    }

    // called from button Replay
    private void doReplay() {
        pane.getChildren().stream().filter(withToolbars()).forEach(setDisable(true));
        rubik.doReplay(moves.getMoves());
        rubik.isOnReplaying().addListener((ov, v, v1) -> {
            if (v && !v1) {
                System.out.println("replayed!");
                pane.getChildren().stream().filter(withToolbars()).forEach(setDisable(false));
            }
        });
    }

    // some predicates for readability
    private static Predicate<Node> withToolbars() {
        return n -> (n instanceof ToolBar);
    }

    private static Predicate<Node> withMoveButtons() {
        return n -> (n instanceof Button) && ((Button) n).getText().length() <= 2;
    }

    private static Predicate<Node> withButtonTextName(String text) {
        return n -> ((Button) n).getText().equals(text);
    }

    private static Predicate<Node> isButtonHovered() {
        return n -> ((Button) n).isHover();
    }

    private static Consumer<Node> setDisable(boolean disable) {
        return n -> n.setDisable(disable);
    }
}
