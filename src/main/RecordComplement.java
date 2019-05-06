package main;

import helper.Validator;
import model.Product;

import java.util.Scanner;
import java.util.regex.Pattern;

// have to default constructor in Test
public class RecordComplement {

    /*important and usable method
     * findObjectByCharacterInName(String character,HashMap<Integer,Product> products)//<<<< move to searcherThread
     * Boolean updateObjectById(int number,ArrayList<Product products)
     * void deleteRecordById(int number , ArrayList<Product products)<<should be change to boolean
     * filterListWithPagination(int page,int recordAmountInlist)
     * */
    /* >findObject*/
    static Boolean stringHasChar(String regex, String text) {
        return Pattern.matches(".*" + regex + ".*", text);
    }

    /*> updateRecord*/

//    private String myTable(){}

    static String insertRecord(Product paramProduct) {
        Product product = new Product();
        passByValue(product, paramProduct);
        int orderNum;
        boolean loopStatus = true;
        boolean isUpdated = false;
        do {
            String InsertMenu[] = {"1.Update All", "2.Name", "3.Price", "4.Qty", "5.Exit"};
            App.myTable(5, 10, InsertMenu, "tttttttttt");

            orderNum = Validator.readInt("Option :");
            switch (orderNum) {
                case 1:
                    product = insertNewRecord();
                    product.setId(paramProduct.getId());
                    product.setImportedDate(paramProduct.getImportedDate());
                    isUpdated = true;
                    break;
                case 2:

                    String name ="";

                    while(true) {
                        System.out.print("Name:");

                        name = new Scanner(System.in).nextLine();

                        if (RecordComplement.stringHasChar("\\'",name)) {
                            Complementary.tabler("Name Can't Be Inputted with single quote(') ");
                            continue;
                        }
                        break;
                    }

                    product.setName(name);
                    isUpdated = true;
                    break;
                case 3:
                    product.setUnitPrice(Validator.readDouble("Price :"));
                    isUpdated = true;
                    break;
                case 4:
                    product.setStockQty(Validator.readInt("QTY :"));
                    isUpdated = true;
                    break;
                case 5:
                    loopStatus = false;
                    break;
                default:
                    Complementary.tabler("Input Mistake");
                    break;
            }
            if (isUpdated) {
                String checkInfo[] = Complementary.subString(product.toString());
                String PreviousInfo[] = Complementary.subString(paramProduct.toString());
                String addStringTable[] = {
                        " ", "Old", "New",
                        "ID", PreviousInfo[0], checkInfo[0],
                        "Name", PreviousInfo[1], checkInfo[1],
                        "Price", PreviousInfo[2], checkInfo[2],
                        "Qty", PreviousInfo[3], checkInfo[3]
                        , "Imported Date", PreviousInfo[4], checkInfo[4]
                };
                App.myTable(3, 19, "Check", addStringTable, "tttttttttt");
            }
        } while (loopStatus);

        if (!isUpdated) {
            return paramProduct.toString();
        }

        while (true) {

            Complementary.tabler("press 'y' to update and 'n' to cancel");
            char c = new Scanner(System.in).next().charAt(0);
            if (c == 'y' || c == 'Y') {
                return product.toString();
            } else if (c == 'n' || c == 'N') {
                return paramProduct.toString();
            }
        }
    }

    public static Product insertNewRecord() {
        String name ="";
        while(true) {
            System.out.print("Name:");

             name = new Scanner(System.in).nextLine();
            if (RecordComplement.stringHasChar("\\'",name)){
                Complementary.tabler("Name Can't Be Inputted with single quote(')");
                continue;
            }
            break;
        }
        double price = Validator.readDouble("Price : ");
        int qty = Validator.readInt("Qty :");

        return new Product(0 , name, price, qty, null);
    }

    public static void main(String[] args) {
        System.out.println(insertNewRecord());
    }

    private static void passByValue(Product product1, Product product2) {
        product1.setId(product2.getId());
        product1.setImportedDate(product2.getImportedDate());
        product1.setName(product2.getName());
        product1.setStockQty(product2.getStockQty());
        product1.setUnitPrice(product2.getUnitPrice());
    }
}
