import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class Inventory {


    private static String defaultFilePath = "./input/inventory.txt";
    public String filePath;
    private static String reg = "\\s+";
    public static Hashtable< String, Integer> inventory = new Hashtable< String, Integer>();

    public Inventory(String fp){
      this.filePath = fp;
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
          inventory.put(s[0], Integer.parseInt(s[1]));

        }
        myFileScanner.close();
      } catch (FileNotFoundException e){
        System.out.println("Error reading input file: ");
        e.printStackTrace();
      }

    }

  private synchronized static void writeFile(String filePath){


  }
}
