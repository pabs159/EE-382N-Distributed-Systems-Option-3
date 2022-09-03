import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.commons.text.StringEscapeUtils;


public class Client {

  static String hostname = "127.0.0.1";
  static int port = 6060;
  private final String tcpProtocol = "T";
  private final String udpProtocol = "U"; 
  private Socket sock;
  private String mode;
  private OutputStream output;
  private String cmdToSend;
  private boolean TCPProtocol = true; // defualt

  public void connectToServerUDP() {
    int port = 6061;
    int len = 256;
    String[] tokens;
    String command;
    DatagramPacket packet;
    String serverMsg; 
    String r;
    Scanner sc = new Scanner(System.in);
    do {
      System.out.println("UDP connection chosen, enter your command:");
      command = sc.nextLine();
      tokens = command.split(" ");
      byte[] buffer = new byte[len];
      try {
        DatagramSocket socket = new DatagramSocket();
        buffer = command.getBytes();
        //packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
        packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
        socket.send(packet);
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        // wait for reply
        
        if(tokens.length == 2){
          if(tokens[1].toUpperCase().equals("T")){
            // close the datagram socket and switch to TCP 

            socket.receive(reply);
            this.TCPProtocol = true;
            return;
          }
        }
        socket.receive(reply);
        r = new String(buffer);
        System.out.println("Reply from server: " + r);
      } catch (IOException e) {
        System.err.println(e);
      }
    } while(!command.equals("bye"));
  }
  

  public void connectToServerTCP() throws InterruptedException{
    String cmdStr;
    String[] tokens;
    Scanner serverReader;

    try (Socket socket = new Socket(hostname, port)) {
      this.sock = socket;
      this.output = this.sock.getOutputStream();
      PrintWriter writer = new PrintWriter(this.output, true);
      
      Console console = System.console();
      do{
        cmdStr = console.readLine("Enter a server command: ");
        tokens = cmdStr.split(" ");
        // Have the server handle malformed request 
        writer.println(cmdStr);

        InputStream input = this.sock.getInputStream();
        //BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String str;
        StringBuilder reply = new StringBuilder();
        int val;
        //serverReader = new Scanner(input);
        if (tokens.length == 2){
          if(tokens[1].toUpperCase().equals("U")){
            this.TCPProtocol = false;
            while ((val = reader.read()) != 0){
              reply.append((char)val);
            }
            System.out.println(reply);
            this.sock.close();
            reader.close();
            return;
          }
        }
        while ((val = reader.read()) != 0){
          reply.append((char)val);
          
        }
        System.out.println(reply);
        /* 
        System.out.println(StringEscapeUtils.escapeJava(System.getProperty("line.separator")));
        while(serverReader.hasNextLine()){
          System.out.println(serverReader.hasNext(" "));
          System.out.println(serverReader.hasNextLine());
          str = serverReader.nextLine();
          System.out.println(str);
        }
        */
        //String resp = reader.readLine();
        // The reply from the server
        
      } while(!cmdStr.equals("bye"));
      //serverReader.close();
      this.sock.close();
    } catch (UnknownHostException ex) {
        System.out.println("Server not found: " + ex.getMessage());
    } catch (IOException ex) {
        System.out.println("I/O error: " + ex.getMessage());
    }
  }

  public void getMode(String[] t){
    if (t[0].toUpperCase().equals("SETMODE") && t.length == 2) {
      // setmode T|U
      if (!(t[1].toUpperCase().equals("T") || t[1].toUpperCase().equals("U"))){
        System.out.println("Error please specify: \"T\" or \"U\" ");
      }
      this.mode = t[1];
      if (mode.toUpperCase().equals(tcpProtocol)){
        System.out.println("TCP Selected");
        try {
          connectToServerTCP();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } else if (mode.toUpperCase().equals(udpProtocol)){
        System.out.println("UDP selected");
        connectToServerUDP();
      }
    } else{
      System.out.println("Error please use < setmode T|U > to establish connection to server");
      return;
    }
    // now alternate on the established connection
    toggleServerType();
  }

  public void toggleServerType(){
    while(true){
      if(this.TCPProtocol){
        try {
          connectToServerTCP();
        } catch (InterruptedException e) {
          e.printStackTrace();
          break; // start over
        }
      } else{
        try{
          connectToServerUDP();
        } catch (Exception ex) {
          ex.printStackTrace();
          break;
        }
      }
    }
  }


  public void runClient(String hostname, int tcpPort, int udpPort){
    System.out.println("Enter command: ");
    try (Scanner sc = new Scanner(System.in)) {
      while(sc.hasNextLine()) {
        String cmd = sc.nextLine();
        String[] tokens = cmd.split(" ");
        if (tokens[0].equals("")){
          System.out.println("ERROR: Must supply a command");
          continue;
        }

        getMode(tokens);

    }

    } catch (Exception e){
      System.out.println("Exception in parseCommand()");
      e.printStackTrace();
    }

  }
}


class Tester {

  public static void main (String[] args) {
    String hostAddress = "127.0.0.1";
    int tcpPort;
    int udpPort;

    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <hostAddress>: the address of the server");
      System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(3) <udpPort>: the port number for UDP connection");
      System.exit(-1);
    }

    hostAddress = args[0];
    tcpPort = Integer.parseInt(args[1]);
    udpPort = Integer.parseInt(args[2]);

    Client client_1 = new Client();
    client_1.runClient(hostAddress, tcpPort, udpPort);
    
  }

}  



      /* 
    try {  
      this.sock.close();
      }
      catch (SocketException ex){
        System.out.println("Error closing socket ");
        ex.printStackTrace();
      }
      catch (IOException ex){
        System.out.println("Error closing socket ");
        ex.printStackTrace();
      }*/