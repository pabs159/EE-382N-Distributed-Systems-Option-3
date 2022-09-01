import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.io.*;
import java.net.*;


public class Client {

  static String hostname = "127.0.0.1";
  static int port = 6060;
  private static String tcpProtocol = "T";
  private static String udpProtocol = "U"; 
  private Socket sock;
  private String mode;
  private OutputStream output;
  private String cmdToSend;

  public void connectToServerUDP() {
    int port = 6061;
    int len = 1000;
    String command;
    DatagramPacket packet;
    Scanner sc = new Scanner(System.in);
    do {
      System.out.println("UDP connection chosen, enter your command:");
      command = sc.nextLine();
      byte[] buffer;
      try {
        DatagramSocket socket = new DatagramSocket();
        buffer = command.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
        socket.send(packet);

        // receive packet from server
        byte[] rBuffer = new byte[4096];
        DatagramPacket rRequest = new DatagramPacket(rBuffer, rBuffer.length);
        socket.receive(rRequest);
        String msg = new String(rBuffer, 0, rRequest.getLength());
        System.out.println(msg);

      } catch (IOException e) {
        System.err.println(e);
      }
    }while(!command.equals("bye"));
  }
  

  public void connectToServerTCP() throws InterruptedException{
    String cmdStr;
    try (Socket socket = new Socket(hostname, port)) {
      this.sock = socket;
      this.output = this.sock.getOutputStream();
      PrintWriter writer = new PrintWriter(this.output, true);

      Console console = System.console();
      do{
      cmdStr = console.readLine("Enter a server command: ");
      cmdToSend = getCommand(cmdStr);
      // If getCommand() returns Error the users input was invalid 
      if(cmdToSend.toUpperCase().equals("ERROR")){
        System.out.println("Error invalid command to send to server!!");
        continue;
      }
      writer.println(cmdToSend);

      InputStream input = this.sock.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      String resp = reader.readLine();
      // resp is the reply from the server
      System.out.println(resp);

      }while(!cmdStr.equals("bye"));

      this.sock.close();
    } catch (UnknownHostException ex) {
        System.out.println("Server not found: " + ex.getMessage());
    } catch (IOException ex) {
        System.out.println("I/O error: " + ex.getMessage());
    }
  }

  // Can be used by both protocols 
  public String getCommand(String command){
    if (command.equals("purchase")) {
      // For now send a template 
      return "purchase <username> <product-name> <quantity>";
    } else if (command.equals("cancel")) {

    } else if (command.equals("search")) {

    } else if (command.equals("list")) {
  
    } else {
      System.out.println("ERROR: No such command");
      return "Error";
    }
    return "Error";

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
        // What if they only enter a token of length 0?      
        

        try {
          if (tokens[0].equals("setmode") && tokens.length == 2) {
            // setmode T|U
            this.mode = tokens[1];
            if (mode.toUpperCase().equals(tcpProtocol)){
              connectToServerTCP();
            }
            else if (mode.toUpperCase().equals(udpProtocol)){
              connectToServerUDP();
            }
          }
        }

        catch (Exception e) {
          System.out.println("Error: ");
          e.printStackTrace();
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