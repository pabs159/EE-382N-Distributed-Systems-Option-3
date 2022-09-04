import java.io.IOException;
import java.net.*;

public class Server implements Runnable {

  // Constructor
  public Server(Inventory i, int port, String filePath) {
  }

  public void run() {
  }

  public void startServer() {
  }
}

class TCPServer extends Server {
  public int tcpPort;
  private Boolean runTcp = true;
  Inventory inv;

  public TCPServer(Inventory i, int tcpPort, String filePath) {
    super(i, tcpPort, filePath);
    this.tcpPort = tcpPort;
    this.inv = i;
  }

  public void run() {
    System.out.println("Inside start server");
    try (ServerSocket serverSocket = new ServerSocket(this.tcpPort)) {

      System.out.println("TCP Server is listening on port " + tcpPort);

      do {
        System.out.println("HERE");
        Socket socket = serverSocket.accept();
        System.out.println("New client connected");
        TCPServerThread tcp = new TCPServerThread(this.inv, socket, null);
        new Thread(tcp).start();
        System.out.println("run TCP: " + tcp.runTcp);
      } while (runTcp);

    } catch (IOException ex) {
      System.out.println("Server exception: " + ex.getMessage());
      ex.printStackTrace();
    }
  }
}

class UDPServer extends Server {
  public int udpPort;
  Inventory inv;

  public UDPServer(Inventory i, int udpPort, String filePath) {
    super(i, udpPort, filePath);
    this.udpPort = udpPort;
    this.inv = i;

  }

  public void run() {
    DatagramSocket socket = null;
    try {
      while (true) {
        socket = new DatagramSocket(this.udpPort);
        System.out.println("UDP server started on port: " + this.udpPort);
        UDPServerThread udp = new UDPServerThread(this.inv, null, socket);
        new Thread(udp).start();
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
}

class ServerTester {
  public static void main(String[] args) {

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

    Inventory inv = new Inventory(fileName);

    inv.readFile();
    System.out.println(inv.inventoryTable);

    Server tcpServer = new TCPServer(inv, tcpPort, fileName);
    new Thread(tcpServer).start();
    Server udpServer = new UDPServer(inv, udpPort, fileName);
    new Thread(udpServer).start();
  }
}
