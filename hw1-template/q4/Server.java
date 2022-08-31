import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;

public class Server {
  Inventory inv;


  public Server(String filePath){
    inv = new Inventory(filePath);
  }
  
  public static void main (String[] args) {
    

    int tcpPort = 6060;
    int udpPort = 0;
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

    new Thread((new Runnable() {
      int udpPort;
      public void run() {
        System.out.println("Server is listening on UDP port " + udpPort);
        try {
            DatagramSocket datagramSocket = new DatagramSocket(udpPort);
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);
                String str = new String(buffer, 0, packet.getLength());
                String[] splitStr = str.split(" ");
                String methodStr = splitStr[0];

                if (methodStr.equals("purchase")) {  // todo this needs to interact with inventory here
                    System.out.println("Purchasing for user __ product __ quantity __");
                } else if (methodStr.equals("cancel")) {
                    System.out.println("Cancel order with id of ____");
                } else if (methodStr.equals("search")) {
                    System.out.println("Searching all orders for user _____");
                } else if (methodStr.equals("list")) {
                    System.out.println("Listing available products with their quantities");
                } else {  // todo add command to exit loop
                    System.out.println("wut?");
                }
            }
        } catch (IOException e) {
          System.err.println(e.getMessage());
        }
      }
      public Runnable pass(int udpPort) {
        this.udpPort = udpPort;
        return this;
      }
    }).pass(udpPort)).start();


    Server s = new Server(fileName);

    // parse the inventory file
//    readFile(fileName);
//    System.out.println(inventory);


    // TODO: handle request from clients`
    try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {

      System.out.println("Server is listening on TCP port " + tcpPort);
      while (true) {
          Socket socket = serverSocket.accept();
          System.out.println("New client connected");

          new ServerThread(s.inv, socket).start();
      }

  } catch (IOException ex) {
      System.out.println("Server exception: " + ex.getMessage());
      ex.printStackTrace();
  }

  }


}
