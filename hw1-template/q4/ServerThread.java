import java.io.*;
import java.net.*;
 
/*
 * This thread is responsible to handle client connection.
 */
public class ServerThread implements Runnable{
    private Socket socket;
    private String currentCmd;
    public String serverRsp;
    private DatagramSocket dsSocket;

    Inventory inv; // holds all things inventory related including r/w  
    public ServerThread(Inventory i, Socket socket, DatagramSocket dsSocket) {
        this.inv = i;
        this.socket = socket;
        this.dsSocket = dsSocket;
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
    }
}

class TCPServerThread extends ServerThread {
    private Socket socket;
    private String currentCmd;
    public String serverRsp;
    public String protocol;

    Inventory inv; // holds all things inventory related including r/w 

   
    public TCPServerThread(Inventory i, Socket socket, DatagramSocket dsSocket) {
        super(i, socket, dsSocket);
        this.inv = i;
        this.socket = socket;
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
            InputStream input = this.socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            OutputStream output = this.socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
 
            do {
                currentCmd = reader.readLine();
                parseCommand(currentCmd);
                //String reverseText = new StringBuilder(text).reverse().toString();
                writer.println("Server: " + this.serverRsp);
            } while (!currentCmd.equals("bye"));

            System.out.println("Closing Server side socket!");
            this.socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }  
    }
}


class UDPServerThread extends ServerThread {
    private DatagramSocket dsSocket;
    private String currentCmd;
    public String serverRsp;
    public String protocol;

    Inventory inv; // holds all things inventory related including r/w 

   
    public UDPServerThread(Inventory i, Socket socket, DatagramSocket dsSocket) {
        super(i, socket, dsSocket);
        this.inv = i;
        this.dsSocket = dsSocket;
    }

//    private void getFile(){
//        System.out.println(this.inv.inventoryTable);
//        this.inv.readFile();
//        this.inv.inventoryTable.put("phone", 5);
//        System.out.println(this.inv.inventoryTable);
//    }

    private String parseCommand(String command){
        String[] splitStr = command.split(" ");
        String methodStr = splitStr[0];
        System.out.println(methodStr);

        switch (methodStr) {
            case "purchase" -> {
                return this.inv.purchase(splitStr[1], splitStr[2], Integer.parseInt(splitStr[3]));
            }
            case "cancel" -> {
                return inv.cancel(Integer.parseInt(splitStr[1]));
            }
            case "search" -> {
                return this.inv.search(splitStr[1]);
            }
            case "list" -> {
                return this.inv.list();
            }
            default -> {
                return "Invalid Command";
            }
        }
    }
 
    public void run() {   
        try {
            String msg = "";
            byte[] buffer = new byte[1024];
            do{
                // Get client request
                DatagramPacket rRequest = new DatagramPacket(buffer, buffer.length);
                this.dsSocket.receive(rRequest);
                msg = new String(buffer, 0, rRequest.getLength());

                String returnString = parseCommand(msg);

                DatagramPacket reply = new DatagramPacket(
                        returnString.getBytes(),
                        returnString.getBytes().length,
                        rRequest.getAddress(),
                        rRequest.getPort()
                );
                this.dsSocket.send(reply);
            }while(!msg.equals("bye"));

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            this.dsSocket.close();
        }  
    }
}