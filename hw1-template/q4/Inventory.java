import com.sun.source.tree.Tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class Inventory {
    class Order {
        public String userName;
        public String productName;
        public int quantity;

        public Order(String userName, String productName, String quantity) {
            this.userName = userName;
            this.productName = productName;
            this.quantity = Integer.parseInt(quantity);
        }

        public Order(String userName, String productName, int quantity) {
            this.userName = userName;
            this.productName = productName;
            this.quantity = quantity;
        }
    }


    private static String defaultFilePath = "./input/inventory.txt";
    public String filePath;
    private static String reg = "\\s+";
    public TreeMap< String, Integer> inventoryTable = new TreeMap< String, Integer>();

    public Inventory(String fp){
      this.filePath = fp;
    }

    public String purchase(String username, String productName, String quantity) {
        return "";
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format("%15s%15s\n", "Product Name", "Quantity");
        inventoryTable.forEach((k, v) -> {
            formatter.format("%15s%15s\n", k, v);
        });
        return sb.toString();
    }

    public void readFile(){
      System.out.println(this.filePath);
      try{
        File myFile = new File(filePath);
        Scanner myFileScanner = new Scanner(myFile);
        while (myFileScanner.hasNextLine()) {
          String data = myFileScanner.nextLine();
          if(data.isEmpty()) {
            System.out.println("Empty line in file!");
            break;
          }
          String[] s = data.split(reg);
          inventoryTable.put(s[0], Integer.parseInt(s[1]));

        }
        myFileScanner.close();
        System.out.println("Inventory initialized");
      } catch (FileNotFoundException e){
        System.out.println("Error reading input file: ");
        e.printStackTrace();
      }

    }

  private synchronized static void writeFile(String filePath){


  }
}
