import java.io.*;
import java.net.*;
import java.lang.*;
import org.apache.commons.text.StringEscapeUtils;

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
        this.socket = socket;
        this.dsSocket = dsSocket;
        this.inv = i;
    }

    public void getFile(){
        System.out.println(this.inv.inventoryTable);
    }

    private void parseCommand(String command){
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
        System.out.println(super.inv.inventoryTable);
    }

    public void getFile(){
        //this.inv = super.inv;
        //return super.inv;
        System.out.println(this.inv.inventoryTable);
    }

    private String parseCommand(String command){
        if(command.equals(" ")){
            return "Not a valid command!";
        }
        System.out.println(this.inv.inventoryTable);
        String[] splitStr = command.split(" ");
        String methodStr = splitStr[0];
        System.out.println("Method string: " + methodStr);
        if(splitStr.length > 2){
            if(splitStr[1].toUpperCase().equals("U")){
                return "closing connection";
            }
        }
        System.out.println(methodStr);

        switch (methodStr) {
            case "purchase" -> {
                return this.inv.purchase(splitStr[1], splitStr[2], Integer.parseInt(splitStr[3]));
            }
            case "cancel" -> {
                return this.inv.cancel(Integer.parseInt(splitStr[1]));
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
        String n = null;
        try {
            InputStream input = this.socket.getInputStream();
            OutputStream output = this.socket.getOutputStream();

            do {
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter writer = new PrintWriter(output, true);
                currentCmd = reader.readLine();
                this.serverRsp = parseCommand(currentCmd) + Character.toString((char) 0) ;
                System.out.println(StringEscapeUtils.escapeJava(this.serverRsp));
                writer.println(this.serverRsp);
                if(currentCmd == null || currentCmd.equals("setmode u")){
                    // client closed socket
                    System.out.println("Client closed socket!");
                    return;
                }
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

    public void getFile(){
        System.out.println(this.inv.inventoryTable);
    }


    private String parseCommand(String command){
        if(command.equals(" ")){
            return "Not a valid command!";
        }
        System.out.println(this.inv.inventoryTable);
        String[] splitStr = command.split(" ");
        String methodStr = splitStr[0].replace("\u0000", "");
        System.out.println(StringEscapeUtils.escapeJava(methodStr));
        System.out.println("Method string: " + methodStr);

        switch (methodStr) {
            case "purchase" -> {
                return this.inv.purchase(splitStr[1], splitStr[2], Integer.parseInt(splitStr[3]));
            }
            case "cancel" -> {
                return this.inv.cancel(Integer.parseInt(splitStr[1]));
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
        String clientMsg;
        String str;
        try {

            byte[] word = new byte[256];
            byte[] r = new byte[256];
            do{
                // Get client request
                DatagramPacket request = new DatagramPacket(word, word.length);
                this.dsSocket.receive(request);
                clientMsg = new String(request.getData());
                System.out.println(StringEscapeUtils.escapeJava("Client Msg: " + clientMsg));
                //String str = "reply from UDP server";
                str = parseCommand(clientMsg) + Character.toString((char) 0);
                r = str.getBytes();
                System.out.println(StringEscapeUtils.escapeJava(str));
                System.out.println(r.length);
                // Put reply into packet, send packet to client
                DatagramPacket reply = new DatagramPacket(r, r.length, request.getAddress(), request.getPort());
                this.dsSocket.send(reply);
            }while(clientMsg != "bye");

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            System.out.println("Closing UDP socket");
            this.dsSocket.close();
        }  
    }
}