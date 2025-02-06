//CSV reader and writer components, and error handling
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//other components
import java.util.Scanner;
import java.time.LocalDate;

public class invStock {
    // class representing inventory stock
    static class storedStock {
        // initializing all relevant types
        String date;
        String stockLabel;
        String brand;
        String engineNumber;
        String status;
        // holds a reference to the next node
        storedStock next;

        // constructor
        storedStock(String date, String stockLabel, String brand, String engineNumber, String status) {
            this.date = date;
            this.stockLabel = stockLabel;
            this.brand = brand;
            this.engineNumber = engineNumber;
            this.status = status;
            // next is null by default; must be initialized when adding a new node regardless
            this.next = null;
        }

        // override toString method
        @Override
        public String toString() {
            return "Date: " + date + ", Stock Label: " + stockLabel + ", Brand: " + brand + ", Engine Number: " + engineNumber + ", Status: " + status;
        }
    }

    // head of the linked list
    private storedStock head;

       // method to read CSV and populate LinkedList
       public void loadFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            //tracks number of lines because the file has headers
            int lineCount = 0;
    
            // reads file line by line while it is not null
            while ((line = br.readLine()) != null) {
                //increments line count
                lineCount++;
                
                //skips the first two lines because they are headers
                if (lineCount <= 2) {  
                    continue;  //
                }
    
                // splits each line by comma
                String[] values = line.split(",");
    
                //checks if there are enough columns (there are always five)
                if (values.length < 5) {
                    System.out.println("Skipping invalid row: " + line);
                    continue;
                }
    
                // extract values based on CSV columns
                String date = values[0].trim();
                String stockLabel = values[1].trim();
                String brand = values[2].trim();
                String engineNumber = values[3].trim();
                String status = values[4].trim();
    
                // add to LinkedList
                addItem(date, stockLabel, brand, engineNumber, status);

            }
            System.out.println("CSV file loaded successfully.");
        }
        //catches any exceptions that may occur
        catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    // method to add new entries manually
    // void because the method should not return anything
    public void addItem(String date, String stockLabel, String brand, String engineNumber, String status) {
        //auto sets date, stock label, and status
        String date = LocalDate.now().toString();
        String stockLabel = "New";
        String status = "On-Hand";
        storedStock newItem = new storedStock(date, stockLabel, brand, engineNumber, status);
        
        // if the list is empty, set the head to the new item
        if (head == null) {
             //this becomes the first item in the inventory
            head = newItem;
        } else {
            //if list is not empty
            storedStock temp = head;

            // check if the head itself is a duplicate
                if (head.engineNumber.equals(engineNumber)) {
                    System.out.println("Duplicate engine number detected: (" + engineNumber + "). Entry not added.");
                    return;
                } else {
                    // while temp.next is not null, traverse to the end
                    while (temp.next != null) {
                        if (temp.engineNumber.equals(engineNumber)) {
                            System.out.println("Duplicate engine number detected: (" + engineNumber + "). Entry not added.");
                            // exits when duplicate is found
                            return;
                        }
                        //traverse to the next node because no duplicates have been found
                        temp = temp.next;
                    }
                    // checks the last node for duplication as well
                    if (temp.engineNumber.equals(engineNumber)) {
                        System.out.println("Duplicate engine number detected: (" + engineNumber + "). Entry not added.");
                        return;
                    }
                    //add item at the end when value is null
                    temp.next = newItem;
    
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.csv", true))) {
                        writer.write(date + "," + stockLabel + "," + brand + "," + engineNumber + "," + status);
                        writer.newLine();
                        System.out.println("Entry added and saved to CSV.");
                    } catch (IOException e) {
                        System.err.println("Error writing to CSV file: " + e.getMessage());
                    }
                }
                System.out.println("Item added: " + engineNumber + ". Date: " + date + ", Stock Label: " + stockLabel + ", Brand: " + brand + ", Status: " + status);
            }
        }

    // method to delete an item by engine number
    public void deleteItem(String engineNumber) {
        //exits function if inventory is empty
        if (head == null) {
        System.out.println("Inventory is empty.");
        return;
    }

    // if the head node needs to be deleted
    if (head.engineNumber.equals(engineNumber)) {
        head = head.next;
        System.out.println("Item with engine number " + engineNumber + " has been deleted.");
        return;
    }

    // searches for the node to delete
    storedStock current = head;
    storedStock previous = null;

    // while the current node is not null and the engine number does not match
    while (current != null && !current.engineNumber.equals(engineNumber)) {
        previous = current;
        current = current.next;
    }

    if (current == null) {
        System.out.println("Item with engine number " + engineNumber + " not found.");
        return;
    }

    // skip the node to delete it
    previous.next = current.next;
    System.out.println("Item with engine number " + engineNumber + " has been deleted.");
    
    // update the CSV file after deletion
    updateCSV();
    }

    // method to update the CSV file after deletion
        public void updateCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.csv"))) {
        storedStock current = head;
        
            while (current != null) {
             writer.write(current.date + "," + current.stockLabel + "," + current.brand + "," + current.engineNumber + "," + current.status);
            writer.newLine();
            current = current.next;
            }
         System.out.println("CSV file updated after deletion.");
            } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    // method to sort inventory by brand using Merge Sort
    public void sortbyBrand() {
        head = mergeSort(head);
        System.out.println("Inventory sorted by brand.");
    }

    // merge Sort function for linked list
    private storedStock mergeSort(storedStock head) {
        // base case: if the list is empty or has only one element, do nothing
        if (head == null || head.next == null) {
            return head;
        }

        // splits the list into two halves
        storedStock middle = getMiddle(head);
        storedStock nextOfMiddle = middle.next;
        middle.next = null;

        // recursively sort both halves
        storedStock left = mergeSort(head);
        storedStock right = mergeSort(nextOfMiddle);

        // merge the sorted halves
        return merge(left, right);
    }

    // helper function to get the middle node of the linked list
    private storedStock getMiddle(storedStock head) {
        //base case once again; if the list is empty, return null
        if (head == null) {
            return head;
        }

        storedStock slow = head;
        storedStock fast = head.next;

        // move fast by 2 and slow by 1
        //slow and fast pointers iterate through different sublists
        while (fast != null) {
           fast = fast.next;
           if (fast != null) {
               slow = slow.next;
              fast = fast.next;
           }
        }

        return slow;
    }

    // helper function to merge two sorted linked lists
    private storedStock merge(storedStock left, storedStock right) {
        // if either of the lists is empty, return the other list
        if (left == null) {
            return right;
        }
        if (right == null) {
           return left;
        }

        // compare the brand fields and merge accordingly
        if (left.brand.compareTo(right.brand) <= 0) {
            left.next = merge(left.next, right);
            return left;
            } else {
            right.next = merge(left, right.next);
            return right;
        }
    }
    
    // method to search inventory based on user-defined criteria
    public void searchInventory() {
    Scanner scanner = new Scanner(System.in);
    
    // ask user to select search criteria
    System.out.println("Search Inventory by:");
    System.out.println("1. Date");
    System.out.println("2. Stock Label");
    System.out.println("3. Brand");
    System.out.println("4. Engine Number");
    System.out.println("5. Status");
    System.out.print("Enter choice: ");
    int choice = scanner.nextInt();
    scanner.nextLine();

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
        default:
            System.out.println("Invalid choice.");
            return;
    }

    // search through the inventory and display matching entries
    boolean found = false;
    storedStock temp = head;
    while (temp != null) {
        boolean match = false;
        // Check for matches based on the user's choice
        switch (choice) {
            case 1: // Date
                if (temp.date.equals(searchValue)) match = true;
                break;
            case 2: // Stock Label
                if (temp.stockLabel.equals(searchValue)) match = true;
                break;
            case 3: // Brand
                if (temp.brand.equals(searchValue)) match = true;
                break;
            case 4: // Engine Number
                if (temp.engineNumber.equals(searchValue)) match = true;
                break;
            case 5: // Status
                if (temp.status.equals(searchValue)) match = true;
                break;
        }

        // display matching entries
        if (match) {
            System.out.println("- Date: " + temp.date + " | Stock: " + temp.stockLabel +
                    " | Brand: " + temp.brand + " | Engine No.: " + temp.engineNumber + " | Status: " + temp.status);
            found = true;
        }
        temp = temp.next;
    }

    // If no match was found
    if (!found) {
        System.out.println("No items found for the given search criteria.");
        }
    }

    
    // method to display the inventory
    public void displayInventory() {
        if (head == null) {
            System.out.println("Inventory is empty.");
            return;
        }

        System.out.println("Current Inventory:");
        storedStock temp = head;
        while (temp != null) {
            System.out.println("- Date: " + temp.date + " | Stock: " + temp.stockLabel +
                    " | Brand: " + temp.brand + " | Engine No.: " + temp.engineNumber + " | Status: " + temp.status);
            temp = temp.next;
        }
    }

    public static void main(String[] args) {
        invStock inventory = new invStock();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("MotorPH Stock Card System");
            System.out.println("1. Load Inventory from CSV File");
            System.out.println("2. Add Item Manually");
            System.out.println("3. Delete Item Manually");
            System.out.println("4. Sort by Brand");
            System.out.println("5. Search Inventory");
            System.out.println("6. Display Inventory");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 7.");
                continue;
            }

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
                    String engineToDelete = scanner.nextLine();
                    inventory.deleteItem(engineToDelete);
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
                    System.out.println("Exiting program.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}