import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
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

    Server s = new Server(fileName);

    // parse the inventory file
    //readFile(fileName);
    //System.out.println(inventory);


    // TODO: handle request from clients`
    try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {
 
      System.out.println("Server is listening on port " + tcpPort);

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
