package sample;

import java.io.*;
import java.net.Socket;

public class ChatServerClient {
    protected File clientDirectory;
    protected Socket socket = null;
    protected BufferedReader networkIn = null;
    protected PrintWriter networkOut = null;
    public ChatServerClient(String host, String clientsPath) throws IOException {
        clientDirectory = new File(clientsPath);
        socket = new Socket("localhost", 16789);

        if(socket == null){
            System.err.println("socket is null");
        }
        try {
            //networkIn gets any stream from the port
            networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            networkOut = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e){
            System.err.println("IOEXception while opening a read/write connection");
        }

    }
}
