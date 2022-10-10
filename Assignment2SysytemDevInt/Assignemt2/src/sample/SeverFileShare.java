package sample;

import java.io.File;
import java.io.IOException;
import java.net.*;

public class SeverFileShare {
    //seversocket
    protected ServerSocket serverSocket = null;
    protected Socket userSocket = null;
    protected SeverFileShareThread[] threads = null;
    protected int numUsers = 0;
    protected File dir = new File("resources\\serverResource");

    public static int SERVER_PORT = 16789;

    public SeverFileShare(){
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            // notify in the terminal that the server is running and is listening to port
            System.out.println("---------------------------");
            System.out.println("Chat Server Application is running");
            System.out.println("---------------------------");
            System.out.println("Listening to port: " + SERVER_PORT);

            threads = new SeverFileShareThread[10];
            while (true) {
                userSocket = serverSocket.accept();
                System.out.println("Client #" + (numUsers + 1) + " connected.");
                threads[numUsers] = new SeverFileShareThread(userSocket, dir);
                threads[numUsers].start();
                numUsers++;
            }
        } catch (IOException e){

        }

    }


    public static void main(String[] args){
        SeverFileShare severFileShare= new SeverFileShare();
    }
}
