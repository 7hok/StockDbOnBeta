package main;

import com.sun.prism.impl.Disposer;
import com.sun.rowset.internal.Row;
import controller.Connection;
import helper.Validator;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import model.Product;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class App<publlic> {
    public static final String separator = "®";
    private static final String FILE_NAME = "product.txt";
    public static ArrayList<String> products = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static int numOfRows = 5;
    private static int currentPage = 1;
    private static Table table;
    private static boolean noteUpdate=true,noteDelete=true,noteInsert=true;
    public static void main(String[] args) throws InterruptedException {
        myGroupname();
        //generateData();
        saveNewOption("Do you want to save the last modified? [Y/y] or [N/n] : ");
        //getData();
        products = Data.read();
        do {
            String key = printMenu();
//            key.toLowerCase();
            switch (key.toLowerCase()) {
                case "*":
                    gotoPage(currentPage);
                    break;
                case "w":
                    Product product = Manipulator.insertNewRecord();

                    //System.out.println(product);
                    ArrayList<String> temporaryList = Data.write(product.getName(), product.getUnitPrice(),product.getStockQty(),product.getImportedDate(),1);
                   if(temporaryList.size()>0){
                       products.add(temporaryList.get(0));
                        System.out.println("success added");
                    }else {
                        System.out.println("it not work");
                    }

                    //
                    break;
                case "r":
                    readData();
                    break;
                case "u":
                    update();
                    break;
                case "d": /*@Delete*/
                    delete();
                    break;
                case "f":
                    goFirst();
                    break;
                case "p":
                    goPrevious();
                    break;
                case "n":
                    goNext();
                    break;
                case "l":
                    goLast();
                    break;
                case "s":
                    Complementary.tabler("search");
                    System.out.print("Name :");

                    if (Complementary.searcher(new Scanner(System.in).nextLine(), products, numOfRows) == true) {

                    } else {
                        Complementary.tabler("Data Not Found");
                    }
                    break;
                case "g":
                    gotoPage(Validator.readInt("Input page number(1-" + getTotalPage() + ") : ", 1, getTotalPage()));
                    break;
                case "se":
                    setRow();
                    break;
                case "ba":
                    backup();
                    break;
                case "sa":
                    saveNewOption("Do you want to save it? [Y/y] or [N/n] : ");
                    //System.out.println(products);
                    break;
                case "re":
                    reStore();
                    break;
                case "h":
                    break;
                case "e":
                    System.exit(0);
                    break;
                case "a":
                    aboutus();
                    break;
                default:
                    usingSpecialExpression(key);
                    break;

                /*@Seakthong*/
            }
        } while (true);
    }

    private static void usingSpecialExpression(String str) {
        int num;
        try {
            if (str.toLowerCase().charAt(0) == '#') {
                switch (str.toLowerCase().charAt(1)) {
                    //read write delete search
                    case 'g': //@Goto_Shorthand
                        /* #g100*/ //go to page 100
                        num = 0;
                        try {
                            for (int i = 0; i < str.length(); i++) {
                                if (i > 1) num = num * 10 + Integer.parseInt(String.valueOf(str.charAt(i)));
                            }
                            gotoPage(num);
                        } catch (NumberFormatException nfe) {
                            System.err.println("Syntax: #gNumber\nExample #g100 for goto page 100");
                        }
                        break;
                    case 'd'://@Delete_Shorthand
                        /* #d100 */  //Delete id 100
                        num = 0;
                        try {
                            for (int i = 0; i < str.length(); i++) {
                                if (i > 1) num = num * 10 + Integer.parseInt(String.valueOf(str.charAt(i)));
                            }
                            delete(num);
                        } catch (NumberFormatException nfe) {
                            System.err.println("Syntax: #dNumber\nExample #d100 for delete pro_id 100");
                        }
                        break;
                    case 'u'://@Update_Shorthand
                        /* #u100*/
                        num = 0;
                        try {
                            for (int i = 0; i < str.length(); i++) {
                                if (i > 1) num = num * 10 + Integer.parseInt(String.valueOf(str.charAt(i)));
                            }
                            Complementary.updateObjectById(num, products, true);
                        } catch (NumberFormatException nfe) {
                            System.err.println("Syntax: #uNumber\nExample #u100 for update pro_id 100");
                        }

                        break;
                    case 'w'://@Write_Shorthand
                        /* #w/Items/100.5/10 */ //write data name: Items /price: 100.5 /qty: 10
                        String[] myString = subStringWrite(str);
                        if (myString[0] == "Wrong") return;
                        try {
                            writeData(myString[1], Double.valueOf(myString[2]), Integer.valueOf(myString[3]));
                        } catch (NumberFormatException e) {
                            System.err.println("Syntax: #w/ProductName/Price/Quantity\nExample #w/Reach/1.0/1");
                        }
                        break;
                    case 'r'://@Read_Shorthand
                        /* #r100 */ //read data id 100
                        num = 0;
                        try {
                            for (int i = 0; i < str.length(); i++) {
                                if (i > 1) num = num * 10 + Integer.parseInt(String.valueOf(str.charAt(i)));
                            }
                            readData(num);
                        } catch (NumberFormatException nfe) {
                            System.err.println("Syntax: #rNumber \nExample #r100 for show pro_id 100");
                        }
                        break;
                }
            }
        } catch (StringIndexOutOfBoundsException siobe) {
            System.err.println("Wrong Value ");
        }
    }

    public static String[] subStringWrite(String str) {
        try {
            int firstIndex = str.indexOf('/');
            String s0 = str.substring(0, firstIndex);

            int second = str.indexOf("/", firstIndex + 1);
            String s1 = str.substring(firstIndex + 1, second);

            int third = str.indexOf("/", second + 1);
            String s2 = str.substring(second + 1, third);

            String s3 = str.substring(third + 1);
            return new String[]{s0, s1, s2, s3};
        } catch (IndexOutOfBoundsException e) {
            return new String[]{"Wrong"};
        }

    }

    private static void update() {
        try {
            FileWriter writerChar = new FileWriter("temp\\chioce.txt", true);
            if (noteUpdate == true) {
                writerChar.write('u');
                writerChar.flush();
                writerChar.close();
            }
            noteUpdate = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        String product = Complementary.updateObjectById(Validator.readInt("Input ID : "), products, true);
        if (product != null)
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("temp\\Update.txt"))) {
                bufferedWriter.write(product);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
            }
    }

    private static void delete() {
        try {
            FileWriter writerChar = new FileWriter("temp\\chioce.txt", true);
            if (noteDelete == true) {
                writerChar.write('d');
                writerChar.flush();
                writerChar.close();
            }
            noteDelete = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        String product = Complementary.updateObjectById(Validator.readInt("Input ID : "), products, false);
        if (product != null)
            reCalculateCurrentPage();
    }

    private static void delete(int id) {
        String product = Complementary.updateObjectById(id, products, false);
        if (product != null)
            reCalculateCurrentPage();
    }

    private static void reCalculateCurrentPage() {
        if (currentPage > getTotalPage())
            currentPage = 1;
    }

    /*Done*/
    private static void help() {
        String Help[] = {
                "",
                "",
                "1.    press    *  : Display all record of products",
                "2.    press    w  : Add new products",
                "      press         #w/proname/unitprice/qty : sortcut for add new product",
                "3.    press    r  : read Content any content",
                "      press         #rID:  sortcut for read product by Id",
                "4.    press    u  : Update Data",
                "      press         #uID",
                "5.    press    d  : Delete Data",
                "      press         #dID :  sortcut for read product by Id",
                "6.    press    f  : Display First Page",
                "7.    press    p  : Display Previous Page",
                "8.    press    n  : Display Next Page",
                "9.    press    l  : Display Last Page",
                "10.   press    s  : Search product by name",
                "11.   press    g  : Goto a specific page",
                "      press         #gPageNum",
                "11.   press    sa : Save record to file",
                "12.   press    ba : Backup data",
                "13.   press    re : Restore data",
                "14.   press    h  : Help",
                "15.   press    a  : About Our Developers"

        };
        /*@Seakthong App.myTable*/
        App.myTable(1, 90, "Help", Help, "......tttt");
    }

    /*In Process*/
    private static String printMenu() {
        String[] menu = {"*)Display", "W)rite", "R)ead",
                "U)pdate", "D)elete", "F)irst", "P)revious",
                "N)ext", "L)ast", "S)earch", "G)oto", "Se)t",
                "Sa)ve", "Ba)ck up", "Re)store", "H)elp", "A)bout US", "E)xit"};
        App.myTable(9, 15, "Menu", menu, "tttttttttt");
        System.out.print("Command-->");
        String str = scanner.nextLine();
        return str;
    }

    private static void initTable() {
        BorderStyle borderStyle = new BorderStyle("╔═", "═", "═╤═", "═╗", "╟─", "─", "─┼─", "─╢", "╚═", "═", "═╧═", "═╝", "║ ", " │ ", " ║", "─┴─", "─┬─");
        table = new Table(5, borderStyle, new ShownBorders("tttttttttt"));
        int myMinWidth[] = {14, 32, 12, 11, 18};
        for (int i = 0; i < 5; i++) {
            table.setColumnWidth(i, myMinWidth[i], 27);
        }
        table.addCell("ID", new CellStyle(CellStyle.HorizontalAlign.center));
        table.addCell("Name", new CellStyle(CellStyle.HorizontalAlign.center));
        table.addCell("Unit Price", new CellStyle(CellStyle.HorizontalAlign.left));
        table.addCell("Qty", new CellStyle(CellStyle.HorizontalAlign.left));
        table.addCell("Date", new CellStyle(CellStyle.HorizontalAlign.left));
    }

    private static int selectChoice() {

        String mySearchChoice[] = {"1. Set row", "2. First", "3. previous", "4. Next", "5. Last", "6. Goto", "7. Display"};
        /*@Seakthong add select choice*/
        int choice = Validator.readInt("Please select a choice :");
        return choice;
    }

    private static void getData() {
        long startTime = System.nanoTime();
        products = new ArrayList<>();
        Connection.getProducts(products);
        long time = System.nanoTime() - startTime;
        System.out.println("Read using " + (double) time / 1000000 + " seconds");
    }

    private static void readData() {
        int id = Validator.readInt("Read by ID :");
        readData(id);
    }

    private static void readData(int id) {
        for (String product : products) {
            String[] idPro = product.split(separator);
            if (id == Integer.parseInt(idPro[0])) {
                String shown[] = {"ID", idPro[0], "Name", idPro[1], "Price", idPro[2], "Qty", idPro[3], "Imported Date", idPro[4]};
                App.myTable(2, 20, "Product Detail", shown, "tttttttttt");
            }
        }
    }

    private static void generateData() {
        new Thread(() -> {
            String message = "Please wait....";
            int i = 0;
            while (i < message.length()) {
                System.out.print(message.charAt(i++));
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        long startTime = System.nanoTime();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            int flush = 0;
            for (int i = 1; i <= 10_00; i++) {
                Product product = new Product(i, "Angkor Beer", 10d, 1000, getDate());
                bufferedWriter.write(product.toString());
                bufferedWriter.newLine();
                if (i == 5000 + flush) {
                    flush += 5000;
                    bufferedWriter.flush();
                }
            }
            System.out.println("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
        long time = System.nanoTime() - startTime;
        System.out.println("Read using " + (double) time / 1000000 + " milliseconds");

    }

    private static void setRow() {
        System.out.print("Number of row : ");
        numOfRows = scanner.nextInt();
        if (currentPage > getTotalPage())
            currentPage = 1;
        scanner.nextLine();
    }

    private static void goNext() {
        if (currentPage != getTotalPage())
            gotoPage(++currentPage);
        else gotoPage(getTotalPage());
    }

    private static void goPrevious() {
        if (currentPage != 1)
            gotoPage(--currentPage);
        else
            gotoPage(1);
    }

    private static void gotoPage(int pageNum) {
        initTable();
        currentPage = pageNum;
        int start = numOfRows * (currentPage - 1);

        if (pageNum > getTotalPage()) return;
        if (pageNum == getTotalPage()) {
            goLast();
        } else {
            String[] myProducts = new String[start + numOfRows];
            for (int i = start; i < start + numOfRows; i++) {
                addRowTable(products.get(i));
//                myProducts[i-start] = products.get(i);
            }
//            myTable(20,myProducts);
        }
        String[] myPageDetail = printPageSummary();
        table.addCell(myPageDetail[0], new CellStyle(CellStyle.HorizontalAlign.left), 2);
        table.addCell(myPageDetail[1], new CellStyle(CellStyle.HorizontalAlign.right), 3);
        System.out.println(table.render());
        printPageSummary();
    }

    private static int getTotalPage() {
        return products.size() % numOfRows == 0 ? products.size() / numOfRows : products.size() / numOfRows + 1;
    }

    private static void goFirst() {
        currentPage = 1;
        initTable();
        for (int i = 0; i < numOfRows; i++) {
            addRowTable(products.get(i));
        }
        String[] myPageDetail = printPageSummary();
        table.addCell(myPageDetail[0], new CellStyle(CellStyle.HorizontalAlign.left), 2);
        table.addCell(myPageDetail[1], new CellStyle(CellStyle.HorizontalAlign.right), 3);
        System.out.println(table.render());
    }

    private static String[] printPageSummary() {
//        System.out.print("Page : " + currentPage + " of " + getTotalPage() + "\t\t\t\t\t\t\tTotal record : " + products.size());
        //System.out.printf("%4sPage : %d of %d %64s Total Record: %d", " ", currentPage, getTotalPage(), " ", products.size());
        //System.out.println();
        return new String[]{"Page : " + currentPage + " of " + getTotalPage(), "Total Record: " + products.size()};
    }

    private static int remainRowInLastPage() {
        return products.size() % numOfRows;
    }

    public static void addRowTable(String product) {
        String[] p = product.split(separator);

            for (int i = 0; i < 5; i++){
                try {
                    table.addCell(p[i]);
                }catch (Exception e){
                   break;
                }
            }


    }

    private static void goLast() {
        initTable();
        currentPage = getTotalPage();
        int start = numOfRows * (currentPage - 1);
        for (int i = start; i < products.size(); i++) {
            addRowTable(products.get(i));
        }
        String[] myPageDetail = printPageSummary();
        table.addCell(myPageDetail[0], new CellStyle(CellStyle.HorizontalAlign.left), 2);
        table.addCell(myPageDetail[1], new CellStyle(CellStyle.HorizontalAlign.right), 3);
        System.out.println(table.render());
    }

    public static boolean containedUnsavedFiles() {
        return new File("temp\\Insert.txt").exists() || new File("temp\\Delete.txt").exists() || new File("temp\\Update.txt").exists();
    }

    private static void saveInserted() {
        try {
            long startTime2 = System.nanoTime();
            File fileInsert = new File("temp\\Insert.txt");
            BufferedReader fileTempRead = new BufferedReader(new FileReader(fileInsert));
            BufferedWriter fileSourceWrite = new BufferedWriter(new FileWriter("product.txt", true));
            String line = null;
            while ((line = fileTempRead.readLine()) != null) {
                fileSourceWrite.write(line);
                fileSourceWrite.newLine();
                fileSourceWrite.flush();
            }
            fileSourceWrite.close();
            fileTempRead.close();
            fileInsert.delete();
            long time2 = System.nanoTime() - startTime2;
            System.out.println("Read using " + (double) time2 / 1000000 + " milliseconds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveDeleted() {
        try {
            long startTime2 = System.nanoTime();
            File fileTemp = new File("deleteTempPro.txt");
            File fileSource = new File("product.txt");
            File fileDelete = new File("temp\\Delete.txt");
            BufferedReader br = new BufferedReader(new FileReader(fileSource));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileTemp));
            boolean b = false;
            String line1 = null;
            String line2 = null;
            int i = 0, j = 100;
            while ((line1 = br.readLine()) != null) {
                b = false;
                BufferedReader br2 = new BufferedReader(new FileReader(fileDelete));
                while ((line2 = br2.readLine()) != null) {
                    if (line1.split(separator)[0].equals(line2.split(separator)[0])) {
                        b = true;
                        break;
                    }
                }
                if (b == false) {
                    bufferedWriter.write(line1);
                    bufferedWriter.newLine();
                    if (i++ == j) {
                        j += 100;
                        bufferedWriter.flush();
                        ;
                    }
                }
                br2.close();
            }
            br.close();
            bufferedWriter.close();
            fileSource.delete();
            fileTemp.renameTo(new File("product.txt"));
            fileDelete.delete();
            long time2 = System.nanoTime() - startTime2;
            System.out.println("Read using " + (double) time2 / 1000000 + " milliseconds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveUpdated() {
        try {
            long startTime2 = System.nanoTime();
            File fileTemp = new File("updateTempPro.txt");
            File fileSource = new File("product.txt");
            File fileUpdate = new File("temp\\Update.txt");
            BufferedReader br = new BufferedReader(new FileReader(fileSource));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileTemp));
            BufferedReader br2 = null;
            boolean b = false;
            String line1 = null;
            String line2 = null;
            while ((line1 = br.readLine()) != null) {
                b = false;
                br2 = new BufferedReader(new FileReader(fileUpdate));
                while ((line2 = br2.readLine()) != null) {
                    if (line1.split(separator)[0].equals(line2.split(separator)[0])) {
                        bufferedWriter.write(line2);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        b = true;
                        break;
                    }
                }
                if (b == false) {
                    bufferedWriter.write(line1);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
                br2.close();
            }
            br.close();
            bufferedWriter.close();
            fileSource.delete();
            fileTemp.renameTo(new File("product.txt"));
            fileUpdate.delete();
            long time2 = System.nanoTime() - startTime2;
            System.out.println("Read using " + (double) time2 / 1000000 + " milliseconds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveUpdate() {
        long startTime = System.nanoTime();
        FileWriter fileWriter = null;
        int bufferSize = 8 * 1024;
        try {
            fileWriter = new FileWriter("Product.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter, bufferSize);
        System.out.println(products.size());
        for (int i = 0; i < products.size(); i++) {
            try {
                bufferedWriter.write(products.get(i));
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long time = System.nanoTime() - startTime;
        System.out.println("Read using " + (double) time / 1000000 + " milliseconds");
        deleteTempFiles();
    }

    private static void deleteTempFiles() {
        if (new File("temp\\Update.txt").exists())
            new File("temp\\Update.txt").delete();

        if (new File("temp\\Delete.txt").exists())
            new File("temp\\Delete.txt").delete();

        if (new File("temp\\Insert.txt").exists()) {
            new File("temp\\Insert.txt").delete();
        }

    }

    private static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
//        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        return dateFormat.format(date);
    }

    private static void writeData() {
        try {
            FileWriter writerChar = new FileWriter("temp\\chioce.txt", true);
            if (noteInsert == true) {
                writerChar.write('i');
                writerChar.flush();
                writerChar.close();
            }
            noteInsert = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] lastProduct = products.get(products.size() - 1).split(separator);
        int lastId = Integer.parseInt(lastProduct[0]);
        System.out.println("Product ID : " + (lastId + 1));
        ;
        System.out.print("Product's Name : ");
        String name = scanner.nextLine();
        if (RecordComplement.stringHasChar("\\|", name)) {
            System.out.println("Mistake");
            writeData();
            return;
        }
        double price = Validator.readDouble("Product's Price : ");
        int qty = Validator.readInt("Product's Qty : ", 1, 1_000_000);
        ;
        /*@Seakthong add App.myTable*/
        String shown[] = {"ID", "" + (lastId + 1), "Name", name, "Price", "" + price, "Qty", "" + qty, "Imported Date", getDate()};
        App.myTable(2, 20, "Result", shown, "tttttttttt");
        ;

        char answer;
        ;
        System.out.print("Are you sure to add record? [Y/y] or [N/n]:");
        answer = Character.toLowerCase(scanner.next().charAt(0));
        if (answer == 'y') {
            String product = ("" + (lastId + 1) + separator + name + separator + price + separator + qty + separator + getDate());
            products.add(product);
            try {
                BufferedWriter insertFile = new BufferedWriter(new FileWriter("temp\\Insert.txt", true));
                insertFile.write(product);
                insertFile.newLine();
                insertFile.flush();
                insertFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        scanner.nextLine();
    }

    private static void writeData(String name, double price, int qty) {
        if (RecordComplement.stringHasChar(separator, name)) {
            System.err.println("Mistake");
            return;
        }

        String[] lastProduct = products.get(products.size() - 1).split(separator);
        int lastId = Integer.parseInt(lastProduct[0]);
        System.out.println("Product ID : " + (lastId + 1));

        String shown[] = {"ID", "" + (lastId + 1), "Name", name, "Price", "" + price, "Qty", "" + qty, "Imported Date", getDate()};
        App.myTable(2, 20, "Result", shown, "tttttttttt");
        char answer;
        System.out.print("Are you sure to add record? [Y/y] or [N/n]:");
        answer = Character.toLowerCase(scanner.next().charAt(0));
        if (answer == 'y') {
            String product = ("" + (lastId + 1) + separator + name + separator + price + separator + qty + separator + getDate());
            products.add(product);
            try {
                BufferedWriter insertFile = new BufferedWriter(new FileWriter("temp\\Insert.txt", true));
                insertFile.write(product);
                insertFile.newLine();
                insertFile.flush();
                insertFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scanner.nextLine();
    }

    public static void mkDir(String fileName) {

        File file = new File(fileName);

// if the directory does not exist, create it
        if (!file.exists()) {
            boolean result = false;

            try {
                file.mkdir();
                result = true;
            } catch (SecurityException se) {
                System.err.println("Folder is not created");
            }
            /*if(result) {
                System.out.println("Folder was created");
            }*/
        }//end of if

    }

    static void backup() {
        long start = System.nanoTime();
        mkDir("backup");
        try (BufferedWriter backup = new BufferedWriter(new FileWriter("backup\\" + (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())) + ".bac"))) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
            String thisLine;
            int flush = 5000;
            int i = 0;
            //copy the file content in bytes
            while ((thisLine = bufferedReader.readLine()) != null) {
                i++;
                backup.write(thisLine);
                backup.newLine();
                if (i == flush) {
                    flush += 5000;
                    backup.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long time = System.nanoTime() - start;
        System.out.println("Backup successfully " + (double) time / 1000000 + " milliseconds");
    }

    static void reStore() {
        File[] listOfFiles;
        listOfFiles = (new File("backup")).listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println((i + 1) + ") " + listOfFiles[i].getName());
            }
        }
        int index = Validator.readInt("Enter you choice :", 1, listOfFiles.length);
        try (BufferedWriter restoreTo = new BufferedWriter(new FileWriter(FILE_NAME))) {
            BufferedReader bacRead = new BufferedReader(new FileReader("backup\\" + listOfFiles[index - 1].getName()));
            String thisLine = "";
            int flush = 5000;
            int i = 0;
            //copy the file content in bytes
            while ((thisLine = bacRead.readLine()) != null) {
                i++;
                restoreTo.write(thisLine);
                restoreTo.newLine();
                ;
                if (i == flush) {
                    flush += 5000;
                    restoreTo.flush();
                }
            }
            System.out.println("Restore success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        getData();

    }
    private static void saveNewOption(String msg){

            GetConnection.openConnection();
            DatabaseMetaData dbm = null;
            try {
                if (Data.checkWhetherTempTableHasValue()|| Data.checkWhetherStatementTableHasValue()>0){
                    if (Validator.readYesNo(msg) == 'n'){//<<<<drop statement
                        if(Data.checkWhetherTempTableHasValue()){
                            GetConnection.openConnection();
                            String check="drop table tb_temp";
                            Statement statement=GetConnection.connection.createStatement();
                            statement.executeUpdate(check);
                            GetConnection.closeConnection();
                        }
                        Manipulator.updater("delete from tb_statements");
                        return;
                    }
                    if(Data.checkWhetherTempTableHasValue()){
                        Data.savAndRecovery();
                        GetConnection.openConnection();
                        String check="drop table tb_temp";
                        Statement statement=GetConnection.connection.createStatement();
                        statement.executeUpdate(check);
                        GetConnection.closeConnection();
                    }
                    if( Data.checkWhetherStatementTableHasValue()>0){
                        Data.executeDataFromStatementTable();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println("\n\nAlready updated!!!\n");

    }
    private static void saveOption(String message) {
        if (containedUnsavedFiles()) {
            if (Validator.readYesNo(message) == 'n')
                return;
            try {
                FileReader readChoice = new FileReader("temp\\chioce.txt");
                int charTemp = 0;
                while ((charTemp = readChoice.read()) != -1) {
                    if (charTemp == 'u') {
                        if (new File("temp\\Update.txt").exists())
                            saveUpdated();
                    } else if (charTemp == 'd') {
                        if (new File("temp\\Delete.txt").exists())
                            saveDeleted();
                    } else {
                        if (new File("temp\\Insert.txt").exists())
                            saveInserted();
                    }
                }
                readChoice.close();
                (new File("temp\\chioce.txt")).delete();
            } catch (Exception e) {
            }
            System.out.println("\n\nAlready updated!!!\n");
        }
    }

    static void myTable(int colWidth, int recordAmount, String[] fullValues, boolean yess) {
        BorderStyle borderStyle = new BorderStyle("╔═", "═", "═╤═", "═╗", "╟─", "─", "─┼─", "─╢", "╚═", "═", "═╧═", "═╝", "║ ", " │ ", " ║", "─┴─", "─┬─");
        Table tbl = new Table(5, borderStyle, new ShownBorders("tttttttttt"));
        String contents[] = {"ID", "Name", "Price", "Qty", "Imported Date"};

        for (int i = 0; i < 5; i++) {
            try {
                tbl.setColumnWidth(i, colWidth, colWidth + 10);
                tbl.addCell(contents[i]);
            }catch (Exception err){
                break;
            }
        }
        for (int i = 0; i < recordAmount; i++) {
            try {
                String[] myValues = Complementary.subString(fullValues[i]);
                for (int j = 0; j < 5; j++) {
                    tbl.addCell(myValues[j]);
                }
            }catch (Exception err){
                break;
            }
        }
        System.out.println(tbl.render());
    }

    public static void myTable(int colNum, int colWidth, String[] values) {
        myTable(colNum, colWidth, "", values, "tttttttttt");
    }

    public static void myTable(int colNum, int colWidth, String[] values, String shown) {
        myTable(colNum, colWidth, "", values, shown);
    }

    public static void myTable(int colNum, int colWidth, String content, String[] Value, String shown) {
        BorderStyle borderStyle = new BorderStyle("╔═", "═", "═╤═", "═╗", "╟─", "─", "─┼─", "─╢", "╚═", "═", "═╧═", "═╝", "║ ", " │ ", " ║", "─┴─", "─┬─");
        Table tbl = new Table(colNum, borderStyle, new ShownBorders(shown));
        if (content != "")
            tbl.addCell(content, new CellStyle(CellStyle.HorizontalAlign.center), colNum);
        for (int i = 0; i < colNum; i++) {
            tbl.setColumnWidth(i, colWidth, colWidth + 10);
        }
        for (int i = 0; i < Value.length; i++) {
            tbl.addCell(Value[i]);
        }
        System.out.println(tbl.render());
    }

    public static void myTable(int colNum, int colWidth, String content[], String[] Value, String shown) {
        BorderStyle borderStyle = new BorderStyle("╔═", "═", "═╤═", "═╗", "╟─", "─", "─┼─", "─╢", "╚═", "═", "═╧═", "═╝", "║ ", " │ ", " ║", "─┴─", "─┬─");
        //        CellStyle cellStyle = new CellStyle();
        Table tbl = new Table(colNum, borderStyle, new ShownBorders(shown));
        tbl.addCell(content[0], new CellStyle(CellStyle.HorizontalAlign.center), colNum);
        for (int i = 0; i < colNum; i++) {
            tbl.setColumnWidth(i, colWidth, colWidth + 10);
        }
        for (int i = 0; i < Value.length; i++) {
            tbl.addCell(Value[i]);
        }
        System.out.println(tbl.render());
    }

    static void myGroupname() {
        mkDir("temp");
        System.out.println("\n" +
                "                                                      ______         ________         ______          ______         __    __                                                         \n" +
                "                                                     /      \\       |        \\       /      \\        /      \\       |  \\  /  \\                                                        \n" +
                "                                                    |  $$$$$$\\       \\$$$$$$$$      |  $$$$$$\\      |  $$$$$$\\      | $$ /  $$                                                        \n" +
                "                                                    | $$___\\$$         | $$         | $$  | $$      | $$   \\$$      | $$/  $$                                                         \n" +
                "                                                     \\$$    \\          | $$         | $$  | $$      | $$            | $$  $$                                                          \n" +
                "                                                     _\\$$$$$$\\         | $$         | $$  | $$      | $$   __       | $$$$$\\                                                          \n" +
                "                                                    |  \\__| $$         | $$         | $$__/ $$      | $$__/  \\      | $$ \\$$\\                                                         \n" +
                "                                                     \\$$    $$         | $$          \\$$    $$       \\$$    $$      | $$  \\$$\\                                                        \n" +
                "                                                      \\$$$$$$           \\$$           \\$$$$$$         \\$$$$$$        \\$$   \\$$                                                        \n" +
                "                                                                                                                                                                            \n" +
                "                                                                                                                                                                            \n" +
                "                                                                                                                                                                            \n" +
                "             __       __         ______         __    __         ______          ______         ________        __       __        ________        __    __        ________ \n" +
                "            |  \\     /  \\       /      \\       |  \\  |  \\       /      \\        /      \\       |        \\      |  \\     /  \\      |        \\      |  \\  |  \\      |        \\\n" +
                "            | $$\\   /  $$      |  $$$$$$\\      | $$\\ | $$      |  $$$$$$\\      |  $$$$$$\\      | $$$$$$$$      | $$\\   /  $$      | $$$$$$$$      | $$\\ | $$       \\$$$$$$$$\n" +
                "            | $$$\\ /  $$$      | $$__| $$      | $$$\\| $$      | $$__| $$      | $$ __\\$$      | $$__          | $$$\\ /  $$$      | $$__          | $$$\\| $$         | $$   \n" +
                "            | $$$$\\  $$$$      | $$    $$      | $$$$\\ $$      | $$    $$      | $$|    \\      | $$  \\         | $$$$\\  $$$$      | $$  \\         | $$$$\\ $$         | $$   \n" +
                "            | $$\\$$ $$ $$      | $$$$$$$$      | $$\\$$ $$      | $$$$$$$$      | $$ \\$$$$      | $$$$$         | $$\\$$ $$ $$      | $$$$$         | $$\\$$ $$         | $$   \n" +
                "            | $$ \\$$$| $$      | $$  | $$      | $$ \\$$$$      | $$  | $$      | $$__| $$      | $$_____       | $$ \\$$$| $$      | $$_____       | $$ \\$$$$         | $$   \n" +
                "            | $$  \\$ | $$      | $$  | $$      | $$  \\$$$      | $$  | $$       \\$$    $$      | $$     \\      | $$  \\$ | $$      | $$     \\      | $$  \\$$$         | $$   \n" +
                "             \\$$      \\$$       \\$$   \\$$       \\$$   \\$$       \\$$   \\$$        \\$$$$$$        \\$$$$$$$$       \\$$      \\$$       \\$$$$$$$$       \\$$   \\$$          \\$$   \n" +
                "                                                                                                                                                                            \n" +
                "                                                                                                                                                                            \n" +
                "                                                                                                                                                                            \n");
        System.out.println
                (
                        "\n" +
                                ".....................................................................................................................................\n" +
                                ".....................................................................................................................................\n" +
                                "...______.........._........_________..._________........_........____....____...______.........._........____.._____.....______.....\n" +
                                "..|_..._.\\......../.\\......|.._..._..|.|.._..._..|....../.\\......|_...\\../..._|.|_..._.\\......../.\\......|_...\\|_..._|...'.___..|....\n" +
                                "....|.|_).|....../._.\\.....|_/.|.|.\\_|.|_/.|.|.\\_|...../._.\\.......|...\\/...|.....|.|_).|....../._.\\.......|...\\.|.|.../..'...\\_|....\n" +
                                "....|..__'....../.___.\\........|.|.........|.|......../.___.\\......|.|\\../|.|.....|..__'....../.___.\\......|.|\\.\\|.|...|.|...____....\n" +
                                "..._|.|__).|.._/./...\\.\\_....._|.|_......._|.|_....._/./...\\.\\_..._|.|_\\/_|.|_..._|.|__).|.._/./...\\.\\_..._|.|_\\...|_..\\.`.___]..|...\n" +
                                "..|_______/..|____|.|____|...|_____|.....|_____|...|____|.|____|.|_____||_____|.|_______/..|____|.|____|.|_____|\\____|..`._____.'....\n" +
                                ".....................................................................................................................................\n" +
                                "...................................______..................................................._...._...................................\n" +
                                "..................................'.___..|.................................................|.|..|.|..................................\n" +
                                "................................/..'...\\_|..._..--......--.....__..._...._..--.....______..|.|__|.|_.................................\n" +
                                "................................|.|...____..[.`/'`\\]./..'`\\.\\.[..|.|.|..[.'/'`\\.\\.|______|.|____..._|................................\n" +
                                "................................\\.`.___]..|..|.|.....|.\\__..|..|.\\_/.|,..|.\\__/.|.............._|.|_.................................\n" +
                                ".................................`._____.'..[___].....'.__.'...'.__.'_/..|.;.__/..............|_____|................................\n" +
                                "........................................................................[__|.........................................................\n" +
                                ".....................................................................................................................................\n" +
                                ".....................................................................................................................................\n"
                );
    }

    private static void aboutus() {
        String about[] = {
                "", "",
                "Welcome To Stock Management System",
                " ",
                "Hello User, our Stock Management System is built for easy to control products in stock.",
                "Include, product id, product name, product quantity, product price, and product imported date.",
                "Moreover, we can know about product out of stock or expired date.",
                "",
                "",
                "And We also have multi work according to this Details",
                "1). Display Read Write and Search Data",
                "2). Insert Update Delete: update all or some",
                "3). Pagination from 1 to end by next Page, Previous Page or go to a specific page",
                "4). Save, Backup Restore and Recovery file",
                "",
                ""
        };
        String Developer[] = {
                "Name", "Position",
                "Yorn Sokha", "Project Leader",
                "Sok Menghok", "Project Logical",
                "Nim Sreytouch", "Developer",
                "Veth Samoeun", "Database Manager",
                "Aing Seakthong", "UI Designer"
        };

        App.myTable(1, 99, "About US", about, "......tttt");
        App.myTable(2, 48, "Stock Management System was developed by Group 4 Class BTB", Developer, "tttttttttt");
    }

}