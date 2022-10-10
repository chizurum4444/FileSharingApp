package sample;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class SeverFileShareThread extends Thread{
    protected Socket clientSocket = null;
    protected PrintWriter output = null;
    protected BufferedReader in = null;
    protected File dir = null;


    public SeverFileShareThread(Socket clientSocket, File directory){
        super();
        this.clientSocket = clientSocket;
        this.dir = directory;
        try {
            output = new PrintWriter(this.clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch(IOException e){
            System.err.println("IOException while opening a read/write connection");
        }
    }

    public void run(){
        boolean sessionEnd = false;
        while(!sessionEnd){
            sessionEnd = doCommand();
        }
        try {
            clientSocket.close();
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    protected boolean doCommand()  {
        String input = null;
        String command = null;
        String fileName = null;
        String fileContent = null;
        try{
            input = in.readLine();
            System.out.println(input);

        } catch (IOException e) {
            System.err.println("Error reading command");
        }

        if (input == null) {
            return true;
        }

        // get the command and file name from the client side's socket output stream
        String []list = input.split(",");
        if(list.length == 2) {
            command = list[0];
            fileName = list[1];
        }else{
            command = list[0];
            fileContent = list[1];
            fileName = list[2];
        }
        System.out.println(command+ fileContent+ fileName + "");

        // process the command
        try {
            if (command.equals("DOWNLOAD")) {
                // send a string that represents a file to the client
                synchronized (this) {
                    String dresult = performDownload(fileName) + "," + fileName;
                    output.println(dresult);
                }
                return false;
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        try {
            if (command.equals("UPLOAD")) {
                // send a string that represents a file to the client
                synchronized (this) {
                    performUpload(fileName, fileContent);
                }
                return false;
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return true;
    }
    protected void performUpload(String filename, String content) throws FileNotFoundException {
        File output = new File("resources/serverResource/"+filename);
        PrintWriter writer = new PrintWriter(output);
        writer.write(content);
    }
    protected String performDownload(String filename) throws IOException {
        String result = "";

        FileReader fileReader = new FileReader("resources/serverResource/"+filename);
        BufferedReader input = new BufferedReader(fileReader);

            // read the headers of the file
            String line = input.readLine();
            while(line != null){
               result+=line;
               line = input.readLine();
               if(line != null){
                   result+="\n";
               }
            }

        return result;
    }
}
