/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author rmalot
 */
public class MapCraft extends Application {

    
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Start Begin");
        
        Parent fxmlRoot = FXMLLoader.load(getClass().getResource("/fxml/FXMLDefault.fxml"));
        Scene scene = new Scene(fxmlRoot, 1024, 768, true);        
        scene.getStylesheets().add("/styles/Styles.css");
        
        primaryStage.setTitle("MapCraft");
        primaryStage.setScene(scene);
        primaryStage.show();
               
        System.out.println("Start Complete");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
