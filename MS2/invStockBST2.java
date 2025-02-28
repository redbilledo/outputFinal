// package that handles fundamental input and output operations in Java
import java.io.*;
//more specific packages
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class invStockBST2 {
    // node class for the binary search tree (BST)
    static class treeNode {
        //initializing stock variables
        String date;
        String stockLabel;
        String brand;
        String engineNumber;
        String status;
        // used for the hash code of the engine number
        int key;
        // binary search tree nodes all have left and right
        treeNode left, right;

        treeNode(String date, String stockLabel, String brand, String engineNumber, String status, int key) {
            this.date = date;
            this.stockLabel = stockLabel;
            this.brand = brand;
            this.engineNumber = engineNumber;
            this.status = status;
            this.key = key;
            this.left = this.right = null;
        }

        @Override
        public String toString() {
            return "Date: " + date + ", Stock Label: " + stockLabel + ", Brand: " + brand + ", Engine Number: " + engineNumber + ", Status: " + status;
        }
    }

    // root of the BST
    private treeNode root;

    // method to convert engine number to a numeric key using hashCode function
    // BSTs are typically number-based, so the strings are converted to numbers
    public int convertKey(String engineNumber) {
        return engineNumber.hashCode();
    }

    // method to load data from CSV into the BST
    public void loadFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                lineCount++;

                // skips the first two header lines
                if (lineCount <= 2) {
                    continue;
                }

                //splits each line by comma
                String[] values = line.split(",");
                //checks if there are enough columns (there are always five)
                if (values.length < 5) {
                    System.out.println("Skipping invalid row: " + line);
                    continue;
                }

                String date = values[0].trim();
                String stockLabel = values[1].trim();
                String brand = values[2].trim();
                String engineNumber = values[3].trim();
                String status = values[4].trim();

                // adds the item to the BST (specifically for CSV)
                addItemCSV(date, stockLabel, brand, engineNumber, status);
            }

            System.out.println("CSV file loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    // method to add an item from the CSV file with all five categories
    // separated from user-side method because they are only expected to input brand and engine number
    public void addItemCSV(String date, String stockLabel, String brand, String engineNumber, String status) {
        int key = convertKey(engineNumber);
        treeNode newNode = new treeNode(date, stockLabel, brand, engineNumber, status, key);

        if (root == null) {
            root = newNode;
        } else {
            addNode(root, newNode);
        }
    }

    // method to add an item to the BST from the user side
    public void addItem(String brand, String engineNumber) {
        // gets the current date in M/D/YYYY format specifically
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        String date = LocalDate.now().format(formatter);
        // newly added stock is always new and on-hand
        String stockLabel = "New";
        String status = "On-hand";

        // assigns a hash code key to the engine number
        int key = convertKey(engineNumber);
        treeNode newNode = new treeNode(date, stockLabel, brand, engineNumber, status, key);

        // if root is empty, the new node becomes the root
        if (root == null) {
            root = newNode;
        // otherwise, the node is added to the BST using the addNode method
        } else {
            addNode(root, newNode);
        }
    }

    // helper method to add a node to the BST
    private void addNode(treeNode root, treeNode newNode) {
        // BST is separated into left and right nodes
        // if the new node's key is less than the root's key, it goes to the left
        // if the new node's key is greater than the root's key, it goes to the right
        if (newNode.key < root.key) {
            if (root.left == null) {
                root.left = newNode;
            } else {
                addNode(root.left, newNode);
            }
        } else if (newNode.key > root.key) {
            if (root.right == null) {
                root.right = newNode;
            } else {
                addNode(root.right, newNode);
            }
        } else {
            System.out.println("Duplicate engine number detected: (" + newNode.engineNumber + "). Entry not added.");
        }
    }

    // method to delete an item based on engine number
    public void deleteItem(String engineNumber) {
        int key = convertKey(engineNumber);
        root = deleteRec(root, key);
        System.out.println("Item with engine number " + engineNumber + " deleted.");
    }

    // recursive function to delete an item from the BST based on key
    private treeNode deleteRec(treeNode root, int key) {
        if (root == null) {
            return null;
        }
    
        if (key < root.key) {
            root.left = deleteRec(root.left, key);
        } else if (key > root.key) {
            root.right = deleteRec(root.right, key);
        } else {
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;
    
            treeNode minNode = findMin(root.right);
            root.date = minNode.date;
            root.stockLabel = minNode.stockLabel;
            root.brand = minNode.brand;
            root.engineNumber = minNode.engineNumber;
            root.status = minNode.status;
            root.key = minNode.key;
            root.right = deleteRec(root.right, minNode.key);
        }
    
        return root;
    }

    // helper function to find the minimum node, which is used to find the in-order successor
    // the in-order successor is the smallest node in the right subtree
    private treeNode findMin(treeNode root) {
        // traverses the left side of the tree because it is smaller, and it always has a smaller value
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    // method to search inventory based on user-defined criteria
    public void searchInventory() {
        Scanner scanner = new Scanner(System.in);

        // asks to select search criteria
        System.out.println("Search Inventory by:");
        System.out.println("1. Date");
        System.out.println("2. Stock Label");
        System.out.println("3. Brand");
        System.out.println("4. Engine Number");
        System.out.println("5. Status");
        System.out.println("6. Key (engineNumber-based)");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        // get any matching entries based on criteria selected
        String searchValue = "";
        switch (choice) {
            case 1:
                System.out.print("Enter Date to search (M/D/YYYY): ");
                searchValue = scanner.nextLine();
                break;
            case 2:
                System.out.print("Enter Stock Label to search: ");
                searchValue = scanner.nextLine();
                break;
            case 3:
                System.out.print("Enter Brand to search: ");
                searchValue = scanner.nextLine();
                break;
            case 4:
                System.out.print("Enter Engine Number to search: ");
                searchValue = scanner.nextLine();
                break;
            case 5:
                System.out.print("Enter Status to search: ");
                searchValue = scanner.nextLine();
                break;
            case 6:
                System.out.print("Enter Key to search (provided in displayed inventory): ");
                searchValue = scanner.nextLine();
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        // calls the searchInOrder method to search the inventory
        boolean found = searchInOrder(root, choice, searchValue);

        // if no match was found
        if (!found) {
            System.out.println("No items found for the given search criteria.");
        }
    }

    // helper method for traversal (in-order) to search inventory
    private boolean searchInOrder(treeNode node, int choice, String searchValue) {
        boolean found = false;
        if (node != null) {
            found = searchInOrder(node.left, choice, searchValue) || found;
            boolean match = false;

            switch (choice) {
                case 1: match = node.date.equals(searchValue); break;
                case 2: match = node.stockLabel.equals(searchValue); break;
                case 3: match = node.brand.equals(searchValue); break;
                case 4: match = node.engineNumber.equals(searchValue); break;
                case 5: match = node.status.equals(searchValue); break;
                case 6: match = String.valueOf(node.key).equals(searchValue); break;
            }

            if (match) {
                System.out.println("- " + node);
                found = true;  // Track that at least one item was found
            }

            found = searchInOrder(node.right, choice, searchValue) || found;
        }
        return found;
    }

    // method to sort the inventory by brand using Merge Sort
    public void sortbyBrand() {
        root = mergeSort(root);
        System.out.println("Inventory sorted by brand.");
    }

    // Merge Sort for BST (sorting by brand)
    private treeNode mergeSort(treeNode root) {
        // base case: if the tree is empty or has only one node
        if (root == null || root.left == null && root.right == null) {
            return root;
        }

        // converts BST to sorted linkedList
        // this has to be done because the Merge Sort algorithm is designed for linkedLists and not BSTs
        treeNode sortedList = treeToList(root);

        // Merge Sort the linkedList
        return mergeSortList(sortedList);
    }

    // converts BST to sorted linkedList
    private treeNode treeToList(treeNode node) {
        // base case: if the tree is empty
        if (node == null) return null;

        // recursively convert the left and right subtrees to linkedLists
        treeNode leftList = treeToList(node.left);
        treeNode rightList = treeToList(node.right);

        // convert the node to a linkedList
        node.left = null;
        node.right = rightList;

        // if leftList is null, the node is the head of the linkedList
        if (leftList == null) return node;

        // otherwise, find the last node of the leftList and add the node to it
        treeNode temp = leftList;
        while (temp.right != null) {
            temp = temp.right;
        }
        temp.right = node;
        return leftList;
    }

    // Merge Sort for linkedList
    private treeNode mergeSortList(treeNode head) {
        // base case: if the linkedList is empty or has only one node
        if (head == null || head.right == null) {
            return head;
        }

        // calls the getMiddle method to find the middle of the linkedList
        treeNode middle = getMiddle(head);
        treeNode nextOfMiddle = middle.right;
        middle.right = null;

        // recursively sort the left and right halves of the linkedList
        treeNode left = mergeSortList(head);
        treeNode right = mergeSortList(nextOfMiddle);

        // merge the sorted left and right halves
        return merge(left, right);
    }

    // method to find the middle of the linkedList
    private treeNode getMiddle(treeNode head) {
        if (head == null) return null;

        // uses the slow and fast pointer technique to find the middle
        treeNode slow = head, fast = head;
        while (fast.right != null && fast.right.right != null) {
            slow = slow.right;
            fast = fast.right.right;
        }

        // slow is the middle of the linkedList
        return slow;
    }

    // merge method to combine two sorted linked lists
    private treeNode merge(treeNode left, treeNode right) {
        // base cases: if either linkedList is empty
        if (left == null) return right;
        if (right == null) return left;

        // compares the brand of the left and right nodes
        if (left.brand.compareTo(right.brand) <= 0) {
            left.right = merge(left.right, right);
            return left;
        } else {
            // if the right node's brand is smaller, it becomes the new head
            right.right = merge(left, right.right);
            return right;
        }
    }

    // method to display the inventory along with the key for each item
    // key is not written to the CSV file, but it is used for searching
    public void displayInventory() {
        if (root == null) {
            System.out.println("Inventory is empty.");
            return;
        }

        System.out.println("Current Inventory:");
        displayOrdered(root);
    }

    // helper method for traversal (in-order) to display the inventory
    private void displayOrdered(treeNode node) {
        if (node != null) {
            displayOrdered(node.left);
            String key = String.valueOf(node.key);  // Convert the engine number to the key
            System.out.println("- Date: " + node.date + " | Stock: " + node.stockLabel +
                    " | Brand: " + node.brand + " | Engine No.: " + node.engineNumber +
                    " | Status: " + node.status + " | Key: " + key);
            displayOrdered(node.right);
        }
    }

    // method to write the inventory to a CSV file
    public void writeToCSV(String filename) {
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
        List<String> headers = new ArrayList<>();
        List<String> dataLines = new ArrayList<>();

        String line;
        int lineCount = 0;

        // reads the existing file while keeping the headers
        while ((line = br.readLine()) != null) {
            lineCount++;
            if (lineCount <= 2) {
                headers.add(line); // Store header lines
            } else {
                dataLines.add(line); // Store data lines
            }
        }

        // displays warning before overwriting
        // useful because people may forget to load the CSV file first
        System.out.println("Warning: The CSV file will be overwritten with current inventory data.");
        System.out.print("Do you want to proceed? (yes/no): ");
        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes")) {
            System.out.println("Operation canceled. CSV file was not modified.");
            return;
        }

        // opens file for writing
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Write headers back
            for (String header : headers) {
                bw.write(header);
                bw.newLine();
            }

            // calls writeInventoryToCSV method 
            writeInventoryToCSV(bw, root);
        }

        System.out.println("CSV file updated successfully.");

        } catch (IOException e) {
            System.err.println("Error updating CSV file: " + e.getMessage());
        }
    }

    // helper method to perform in-order traversal and write nodes to CSV
    private void writeInventoryToCSV(BufferedWriter bw, treeNode node) throws IOException {
        if (node != null) {
            writeInventoryToCSV(bw, node.left);
            bw.write(String.format("%s,%s,%s,%s,%s", node.date, node.stockLabel, node.brand, node.engineNumber, node.status));
            bw.newLine();
            writeInventoryToCSV(bw, node.right);
        }
    }

    //main method to run the program
    public static void main(String[] args) {
    invStockBST2 inventory = new invStockBST2();
    Scanner scanner = new Scanner(System.in);

    while (true) {
        System.out.println("MotorPH Stock Card System");
        System.out.println("1. Load Inventory from CSV File");
        System.out.println("2. Add Item Manually");
        System.out.println("3. Delete Item Manually");
        System.out.println("4. Sort by Brand");
        System.out.println("5. Search Inventory");
        System.out.println("6. Display Inventory");
        System.out.println("7. Write Inventory to CSV File");
        System.out.println("8. Exit");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter CSV file path: ");
                String csvFilePath = scanner.nextLine();
                inventory.loadFromCSV(csvFilePath);
                break;
            case 2:
                System.out.print("Enter Brand: ");
                String brand = scanner.nextLine();
                System.out.print("Enter Engine Number: ");
                String engineNumber = scanner.nextLine();
                inventory.addItem(brand, engineNumber);
                break;
            case 3:
                System.out.print("Enter Engine Number to delete: ");
                String engineNumberToDelete = scanner.nextLine();
                inventory.deleteItem(engineNumberToDelete);
                break;
            case 4:
                inventory.sortbyBrand();
                break;
            case 5:
                inventory.searchInventory();
                break;
            case 6:
                inventory.displayInventory();
                break;
            case 7:
                System.out.print("Enter CSV file path to write: ");
                String writeCsvFilePath = scanner.nextLine();
                inventory.writeToCSV(writeCsvFilePath);
                break;
            case 8:
                System.out.println("Exiting program.");
                scanner.close();
                return;
            default:
                System.out.println("Invalid choice.");
            }
        }
    }
}

