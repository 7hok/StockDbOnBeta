package main;
import helper.Validator;
import main.Complementary;
import main.GetConnection;
import main.RecordComplement;
import model.Product;

import java.lang.reflect.GenericArrayType;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
* Manipulator.productQueryer(sqlStatement ) return arraylist;
* Manipulator.updater(sqlStatement) return int whether update or not;
* */



public class Manipulator {


    public static void main(String[] args) throws SQLException{

//        Data.executeDataFromStatementTable();
       Data.checkWhetherStatementTableHasValue();

    }

    public static int lastIdOfProduct(){
        int i = 0;
        try {
            GetConnection.openConnection();
            String sqlStatement = "select * from products order by id  desc limit 1";
            PreparedStatement preparedStatement = GetConnection.connection.prepareStatement(sqlStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                i = resultSet.getInt(1);
            }
          //  System.out.println(i);
            return i;
        }catch (SQLException sql){
           // System.out.println("data Empty");
            sql.printStackTrace();
        }finally {
            GetConnection.closeConnection();
        }
        return -1;
    }

    public static ArrayList<Product> productQueryer(String sqlStatement) throws SQLException {

        ArrayList arrayList = new ArrayList();

        GetConnection.openConnection();

        PreparedStatement preparedStatement = GetConnection.connection.prepareStatement(sqlStatement);
        ResultSet resultSet = preparedStatement.executeQuery();
        //<<<<< change query ;
       while (resultSet.next()) {
            arrayList.add(
                 new Product(resultSet.getInt(1), resultSet.getString(2),resultSet.getFloat(3),resultSet.getInt(4), resultSet.getString(5))//<<<<< feild of constructor
            );
           //System.out.println(resultSet.getInt(1));
        }
        resultSet.close();
        preparedStatement.close();
        GetConnection.closeConnection();
        return arrayList;
    }

    public static int updater(String sqlStatement ) throws SQLException{

        GetConnection.openConnection();

        PreparedStatement preparedStatement = GetConnection.connection.prepareStatement(sqlStatement);
        int i = preparedStatement.executeUpdate();

        preparedStatement.close();
        GetConnection.closeConnection();

        //effected or not
        return i;
    }

    //***** generate sql statement
    public static String generateSQLstatementFromProduct(Product product){
        return "update products set name = '"+product.getName()+"',unitPrice = "+product.getUnitPrice()+",stockQty="+product.getStockQty()+",importedDate='"+product.getImportedDate()+"' where id ="+product.getId() ;
    }

    //***** String of Product overloading
    public static String generateSQLstatementFromProduct(String []product){
        return "update products set name = '"+product[1]+"',unitPrice = "+product[2]+",stockQty="+product[3]+",importedDate='"+product[4]+"' where id ="+product[0] ;
    }

    //***** String of Product overloading
    public static String generateSQLstatementStatus(String []product){
        return "update products set status = 0 where id = "+product[0] ;
    }

    public static String generateSQLstatementStatus(Product product){
        return "update products set status = 0 where id = "+product.getId() ;
    }

    public static boolean saveSqlStatementToTbStatements(String sql){
        try {
            //<<<<< testing
            System.out.println(updater(sql));
        }catch (SQLException sqlError){
            sqlError.printStackTrace();
        }
        return true;
    }

    //***** generate sql statement
    public static String generateUpdateStatement(String updateQuery){
        System.out.println('"'+updateQuery+'"');
        try {
           int i = updater(  "insert into tb_statements (statement) values ("+'"'+updateQuery+'"'+")");
        }catch (SQLException sql ){
            sql.printStackTrace();
        }
        return "";
    }

//    //***** String of Product overloading
//    public static String generateSQLstatementFromProduct(String []product){
//        return "update products set name = '"+product[1]+"',unitPrice = "+product[2]+",stockQty="+product[3]+",importedDate='"+product[4]+"' where id ="+product[0] ;
//    }

    public static Product insertNewRecord() {
        System.out.print("Name:");
        String name = new Scanner(System.in).nextLine();
        double price = Validator.readDouble("Price : ");
        int qty = Validator.readInt("Qty :");
        System.out.print("Date-Imported : ");
        String dateTime = new Scanner(System.in).nextLine();
        return new Product(Integer.parseInt(Complementary.subString(App.products.get(App.products.size()-1))[0])+1, name, price, qty, dateTime);
    }

    public static ArrayList<String> statementQueryer(String sqlStatement) throws SQLException {

        ArrayList<String> arrayList = new ArrayList();

        GetConnection.openConnection();

        PreparedStatement preparedStatement = GetConnection.connection.prepareStatement(sqlStatement);
        ResultSet resultSet = preparedStatement.executeQuery();
        //<<<<< change query ;
        while (resultSet.next()) {

           arrayList.add(resultSet.getString(2));

        }
        resultSet.close();
        preparedStatement.close();
        GetConnection.closeConnection();
        // Manipulator.updater( resultSet.getString(2));

        if (arrayList.size()>0){
            arrayList.forEach((statement)->{
                try {
                    Manipulator.updater(statement);
                }catch (SQLException sql){
                    System.out.println("problem with sql statement update");
                }
            });
            Manipulator.updater("delete from tb_statements");
            return arrayList;
        }else {
            return null;
        }

    }
}

