import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Inventory {

    public String filePath;
    private static String reg = "\\s+";
    public TreeMap<String, Integer> inventoryTable = new TreeMap<String, Integer>();
    public ArrayList<Order> orders = new ArrayList<>();

    public Inventory(String fp) {
        this.filePath = fp;
    }

    public synchronized String cancel(int orderId) {
        Order orderToCancel = null;
        boolean orderFound = false;
        for (Order order : orders) {
            if (order.orderID == orderId) {
                orderFound = true;
                orderToCancel = order;
                orders.remove(orderToCancel);
                break;
            }
        }
        if (!orderFound)
            return orderId + "not found, no such order";
        inventoryTable.put(
                orderToCancel.productName,
                inventoryTable.get(orderToCancel.productName) + orderToCancel.quantity);
        return "Order " + orderToCancel.orderID + " is canceled";
    }

    public String search(String userName) {
        ArrayList<Order> userOrders = new ArrayList<Order>();
        
        for (Order order : orders) {
            if (order.userName.equals(userName)) {
                userOrders.add(order);
            }
        }
        if (userOrders.size() == 0)
            return "No order found for " + userName;

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format("%15s%15s%15s\n", "Order ID", "Product Name", "Quantity");
        userOrders.forEach(order -> {
            formatter.format("%15s%15s%15s\n", order.orderID, order.productName, order.quantity);
        });
        formatter.close();
        return sb.toString();
    }

    public synchronized String purchase(String username, String productName, int quantity) {
        if (!inventoryTable.containsKey(productName))
            return "Not Available - We do not sell this product";
        if (inventoryTable.get(productName) < quantity)
            return "Not Available - Not enough items";

        inventoryTable.put(productName, inventoryTable.get(productName) - quantity);
        Order order = new Order(username, productName, quantity);
        orders.add(order);

        return "Your order has been placed, " + order.orderID + " " + username + " " + productName + " " + quantity;
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format("%15s%15s\n", "Product Name", "Quantity");
        inventoryTable.forEach((k, v) -> {
            formatter.format("%15s%15s\n", k, v);
        });
        formatter.close();
        return sb.toString();
    }

    public void readFile() {
        System.out.println(this.filePath);
        try {
            File myFile = new File(filePath);
            Scanner myFileScanner = new Scanner(myFile);
            while (myFileScanner.hasNextLine()) {
                String data = myFileScanner.nextLine();
                if (data.isEmpty()) {
                    System.out.println("Empty line in file!");
                    break;
                }
                String[] s = data.split(reg);
                inventoryTable.put(s[0], Integer.parseInt(s[1]));

            }
            myFileScanner.close();
            System.out.println("Inventory initialized");
        } catch (FileNotFoundException e) {
            System.out.println("Error reading input file: ");
            e.printStackTrace();
        }
    }

    class Order {
        public String userName;
        public String productName;
        public int quantity;
        public int orderID;
        public static int count = 1;

        public Order(String userName, String productName, String quantity) {
            this.userName = userName;
            this.productName = productName;
            this.quantity = Integer.parseInt(quantity);
            this.orderID = count++;
        }

        public Order(String userName, String productName, int quantity) {
            this.userName = userName;
            this.productName = productName;
            this.quantity = quantity;
            this.orderID = count++;
        }
    }
}