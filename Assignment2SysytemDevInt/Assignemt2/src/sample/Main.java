package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Main extends Application {
    //Director of serverResources and clientResources
    private File serverDirectory = new File("resources/serverResource");
    private String clickedFile = "";
    private static ChatServerClient app;
    private VBox clientVbox;
    private VBox serverVbox;
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("CSCI2020U - Midterm");
        VBox grid = new VBox();
        grid.setAlignment(Pos.TOP_LEFT);

        HBox menu = new HBox();
//      Creating the menu buttons
         Button download = new Button("Download");
         Button upload = new Button("Upload");
         menu.getChildren().add(download);
         menu.getChildren().add(upload);


        //setting the Event handlers for each menu buttons
        download.setOnAction(e->{
            System.out.println(clickedFile);
            String downString = "";
            app.networkOut.println("DOWNLOAD,"+clickedFile);

            try {
                downString = app.networkIn.readLine();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.println(downString);
            String content[]= downString.split(",");
            String filename = content[1];
            String filecontent = content[0];
            File output = new File("resources/clientResource/"+ filename);
            try{
                PrintWriter outputFile = new PrintWriter(output);
                outputFile.write(filecontent);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            clientVbox.getChildren().clear();
            List filesInClient = getClientFiles();
            for(int i = 0; i< filesInClient.size(); i++){
                clientVbox.getChildren().add((Hyperlink)filesInClient.get(i));
            }

        });


        // functionality for the upload button
        upload.setOnAction(ev ->{
                String outputString = "";

                FileReader reader = null;
                try {
                    reader = new FileReader("resources/clientResource/"+clickedFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                BufferedReader input = new BufferedReader(reader);

                String line = null;
                try {
                    line = input.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while(line != null){
                    outputString += line;
                    try {
                        line = input.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (line != null){
                        outputString+="\n";
                    }
                }

                outputString+=(","+ clickedFile);
                app.networkOut.println("UPLOAD,"+outputString);

                // update the sever pane
                serverVbox.getChildren().clear();
                List filesInServer = getServerFiles();
                for(int i = 0; i< filesInServer.size(); i++){
                    serverVbox.getChildren().add((Hyperlink)filesInServer.get(i));
                }


            });

         // Grid pane to house both server and client view
        GridPane overGrid = new GridPane();
        overGrid.setAlignment(Pos.TOP_LEFT);
        overGrid.setHgap(10);
        overGrid.setVgap(10);


//      Creating the user view and server view
        clientVbox = new VBox();
        clientVbox.setPrefWidth(300);
        serverVbox = new VBox();
        serverVbox.setPrefWidth(300);


//      Testing functionality before making client socket
        List filesInServer = getServerFiles();
        List filesInClient = getClientFiles();

        for(int i = 0; i< filesInClient.size(); i++){
            clientVbox.getChildren().add((Hyperlink)filesInClient.get(i));
        }
        for(int i = 0; i< filesInServer.size(); i++){
            serverVbox.getChildren().add((Hyperlink)filesInServer.get(i));
        }

        overGrid.setMaxWidth(600);
        overGrid.add(clientVbox, 0, 0);
        overGrid.add(serverVbox, 1, 0);
        overGrid.setGridLinesVisible(true);



//      Add the menu buttons to the grid
        grid.getChildren().add(menu);
        grid.getChildren().add(overGrid);


        // main App Scene
        Scene mainScene = new Scene(grid, 600, 750);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private List<Hyperlink> getClientFiles() {
        List<Hyperlink> filesInClient = new ArrayList<>();
        //parse through the files
        if (app.clientDirectory.isDirectory()){
            File [] files = app.clientDirectory.listFiles();
            for(File file: files){
                Hyperlink link = new Hyperlink(file.getName());
                link.setOnAction(e->{
                    clickedFile = file.getName();
                    Font courierNewFontBold = Font.font("Courier New", FontWeight.BOLD, link.getFont().getSize());
                    link.setFont(courierNewFontBold);
                });
                filesInClient.add(link);
            }
        }
        return filesInClient;
    }

    // helper function to get file name from serverResources
    public List<Hyperlink> getServerFiles(){
        List<Hyperlink> filesInServer = new ArrayList<>();
        //parse through the files
        if (serverDirectory.isDirectory()){
            File [] files = serverDirectory.listFiles();
            for(File file: files){
                Hyperlink link = new Hyperlink(file.getName());
                link.setOnAction(e->{
                    clickedFile = file.getName();
                    Font courierNewFontBold = Font.font("Courier New", FontWeight.BOLD, link.getFont().getSize());
                    link.setFont(courierNewFontBold);
                });
                filesInServer.add(link);
            }
        }
        return filesInServer;
    }


    public static void main(String[] args) throws IOException {
        app = new ChatServerClient("user", "resources/clientResource");
        launch(args);
    }


}
