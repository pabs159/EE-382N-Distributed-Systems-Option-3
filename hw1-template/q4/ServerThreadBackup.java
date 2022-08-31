import java.io.*;
import java.net.*;
 
/*
 * This thread is responsible to handle client connection.
 */
public class ServerThreadBackup extends Thread {
    private Socket socket;
    private String currentCmd;
    public String serverRsp;
    public String protocol;

    Inventory inv; // holds all things inventory related including r/w 

   
    public ServerThreadBackup(Inventory i, String protocol, Socket socket) {
        this.inv = i;
        this.socket = socket;
        this.protocol = protocol;
    }

    private void getFile(){
        System.out.println(this.inv.inventoryTable);
        this.inv.readFile();
        this.inv.inventoryTable.put("phone", 5);
        System.out.println(this.inv.inventoryTable);
    }

    private void parseCommand(String command){
        this.currentCmd = command;
        getFile();
        this.serverRsp = "a response from the server";
    }
 
    
    public void run() {
        
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
 
            do {
                currentCmd = reader.readLine();
                parseCommand(currentCmd);
                //String reverseText = new StringBuilder(text).reverse().toString();
                writer.println("Server: " + this.serverRsp);
            } while (!currentCmd.equals("bye"));

            System.out.println("Closing Server side socket!");
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }  
    }

    public void runUDP(){

        System.out.println("UDP SERVER");
    }

}
