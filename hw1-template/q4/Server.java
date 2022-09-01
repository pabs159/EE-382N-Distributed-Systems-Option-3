//import java.io.File;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;
//import java.util.Hashtable;
//import java.util.Scanner;

public class Server {
  // Create inventory object 
  Inventory inv;

  // Constructor 
  public Server(int port, Inventory inventory){
    //inv = new Inventory(filePath);
    this.inv = inventory;
  }

  public void run(){
    System.out.println("here?");
  }

  public void startServer() {
  }

  private void parseCommand(){
    // ToDo
  }
}

class TCPServer extends Server {
  public int tcpPort;
  
  public TCPServer(int tcpPort, Inventory inventory) {
    super(tcpPort, inventory);
    this.tcpPort = tcpPort;
    
  }

  public void startServer(){
    System.out.println("Inside start server");
    try (ServerSocket serverSocket = new ServerSocket(this.tcpPort)) {
  
      System.out.println("TCP Server is listening on port " + tcpPort);

      //while (true) {
          Socket socket = serverSocket.accept();
          System.out.println("New client connected");
          TCPServerThread tcp1 = new TCPServerThread(super.inv, socket, null);
          new Thread(tcp1).start();
      //}

    } catch (IOException ex) {
        System.out.println("Server exception: " + ex.getMessage());
        ex.printStackTrace();
    }
  }
}

class UDPServer extends Server {
  public int udpPort;

  public UDPServer(int udpPort, Inventory inventory) {
    super(udpPort, inventory);
    this.udpPort = udpPort;
  }

  public void startServer(){
    DatagramSocket socket = null;
    try{
      socket = new DatagramSocket(this.udpPort);
      System.out.println("UDP server started on port: " + this.udpPort);
      UDPServerThread udp1 = new UDPServerThread(super.inv, null, socket);
      new Thread(udp1).start();
    }
    catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
}


class ServerTester{
  public static void main (String[] args) {
    int tcpPort = 6060;
    int udpPort = 6061;

    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <udpPort>: the port number for UDP connection");
      System.out.println("\t(3) <file>: the file of inventory");
      System.exit(-1);
    }

    tcpPort = Integer.parseInt(args[0]);
    udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];

    Inventory inventory = new Inventory(fileName);
    inventory.readFile();
    System.out.println(inventory.inventoryTable.toString());
//    Server tcpServer = new TCPServer(tcpPort, fileName);
//    tcpServer.startServer();

//     Will need to either spin these off as their own threads since the while loop in the
//     individual threads block the program or multiple "tester" instances to have both UDP and TCP runing

    Server udpServer = new UDPServer(udpPort, inventory);
    udpServer.startServer();

  }
}


