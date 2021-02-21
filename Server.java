/**
 * Upama Uprety, Bishal BOgati
 * CIS 427-002
 * Program 1
 * prof. Dr. John Baugh
 * This program interacts between client and server sharing same server port and
 * performs certain operations like add, delete the information in the address book as specified by the client
 * * */
package project1;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import static project1.Server.bookSize;
import static project1.Server.count;
import static project1.Server.recordId;

public class Server {

    public static final int SERVER_PORT = 5432;

    AddressBook book;

    public static int count = 0;
    public static int recordId = 1001;
    public static int bookSize = 5;
    public static boolean recordExists = false;

    public static void main(String args[]) {

        AddressBook book = new AddressBook();

        ServerSocket myServerice = null;
        String line;
        BufferedReader is;
        BufferedReader clientInput;
        PrintStream os;
        Socket serviceSocket = null;

        String[] stringArray = new String[bookSize];
        String[] deleteArray = new String[bookSize];

        try {
            myServerice = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            System.out.println(e);
        }

        while (true) {
            try {
                serviceSocket = myServerice.accept();
                is = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()));
                os = new PrintStream(serviceSocket.getOutputStream());
                clientInput = new BufferedReader(new InputStreamReader(System.in));

                //while there is an input from the client
                while ((line = is.readLine()) != null) {

                    StringTokenizer token = new StringTokenizer(line);

                    // Adding the info into the book if the space is avilable and is in correcgt format
                    if (line.startsWith("ADD") && count < bookSize && (token.countTokens() == 4)) {

                        stringArray[count] = line;

                        String[] split = stringArray[count].split(" ");

                        String firstName = split[1];
                        String lastName = split[2];

                        String number = split[3];

                        //storing the info if the first name, last name and phone number 
                        //is within the specified character strings
                        if (firstName.length() <= 8 && lastName.length() <= 8 && number.length() <= 12) {

                            book.storeNum(firstName, lastName, number);
                            recordExists = true;
 
                            System.out.println(" 200 OK ");
                            System.out.println(" The new record is " + recordId);
                            os.println(line);
                            recordId++;
                            count++;

                            // printing error message if first name, last name and phone number's length exceeds
                        } else {
                            System.out.println(" Too many characaters ");
                            os.println();
                        }

                        //printing out the eroor message when the client wants to add more info then the space provided.
                    } else if (line.startsWith("ADD") && count >= bookSize) {
                        System.out.println(" Maximum people exceeded ");
                        os.println();

                        // listing the address book when user types "LIST"
                    } else if (line.equals("LIST")) {
                        System.out.println("200 OK");
                        book.listBook();
                        os.println();

                    } //deletes the id if it exists and the input is in correct format.
                    else if (line.startsWith("DELETE") && (token.countTokens() == 2) && recordExists == true) {

                        deleteArray[0] = line;
                        String[] splt = deleteArray[0].split(" ");
                        int deleteId = Integer.parseInt(splt[1]);

                        System.out.println(" 200 OK ");
                        book.deleteNum(deleteId);

                        os.println(line);

                    } //Close the client when the client types this input.
                    else if (line.equals("QUIT")) {

                        System.out.println(" 200 OK ");
                        
                        os.println("Close yourself");

                    } // close all open sockets and files and then terminates
                    else if (line.equals("SHUTDOWN")) {
                        
                        System.out.println(" 200 OK ");
                        os.println("Close yourself");
                        myServerice.close();
                        serviceSocket.close();
                        System.exit(0);
                        
                    } //output for wrong format
                    else {
                        System.out.println("301!! Message format error.");
                        os.println();
                    }

                }

            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}

class AddressBook {

    public AddressBook() {
    }

    String[] fName = new String[bookSize];
    String[] lName = new String[bookSize];
    String[] fNumber = new String[bookSize];
    int[] idCount = new int[bookSize];

    int id = 1001;
    int storeCounter = 0;

    //A method to store the first name, last name and phone number from every input line.
    public void storeNum(String firstName, String lastName, String phoneNumber) {

        //adding the numbesr in the aaray
        if (storeCounter == count) {

            fName[storeCounter] = firstName;
            lName[storeCounter] = lastName;
            fNumber[storeCounter] = phoneNumber;
            idCount[storeCounter] = id;
        }

        id++;
        storeCounter++;

    }
   

    // This method deletes the information of the user as per the id number provided.
    // The user types "DELETE" and the id number that needs to be deleted.
    public void deleteNum(int dId) {

        int indexFound = 0;
        int len = idCount.length;
        int i = 0;

        //when the recors id does not exists
        if (dId > (recordId - 1) || dId < 1001) {
            System.out.println(" 403 !! The Record Id does not exist.");

        } else {

            while (i < len) {

                if (idCount[i] == dId) {
                    indexFound = i;
                }
                i++;
            }
            //updating the array of information
            for (int a = indexFound; a < idCount.length - 1; a++) {

                fName[a] = fName[a + 1];
                lName[a] = lName[a + 1];
                fNumber[a] = fNumber[a + 1];
                idCount[a] = idCount[a + 1];
                idCount[a] = idCount[a] - 1;

            }
            count--;
            storeCounter--;
            id--;
            recordId--;

        }
    }

    // This method lists the firstname, last name and address of the no. of entries stored by the user 
    // after the user writes "LIST".
    public void listBook() {

        System.out.println(" The list of records in the book ");

        for (int a = 0; a <= (count - 1); a++) {
            System.out.println(idCount[a] + "\t" + fName[a] + "\t" + lName[a] + "\t" + fNumber[a]);

        }
    }

}
