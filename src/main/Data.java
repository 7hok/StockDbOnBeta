package main;

import java.lang.reflect.GenericArrayType;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Data {
    public static Statement statement=null;
    public static ArrayList<String> read() {
        GetConnection.openConnection();
        ArrayList<String> arrayList=null;
        try {
            statement=GetConnection.connection.createStatement();
            //for read data
            String sqlRead="select * from products where status = 1";
            arrayList=new ArrayList<>();
            ResultSet resultSet=statement.executeQuery(sqlRead);
            while (resultSet.next()){
                arrayList.add(resultSet.getInt(1)+GetConnection.separator+resultSet.getString(2)
                        +GetConnection.separator+resultSet.getDouble(3)+GetConnection.separator+resultSet.getInt(4)
                        +GetConnection.separator+resultSet.getString(5));
            }
//            Iterator<String> iter
//                    = arrayList.iterator();
//            while (iter.hasNext()) {
//                System.out.println(iter.next());
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        GetConnection.closeConnection();
        return arrayList;
    }

    public static ArrayList<String>  write(String pro_name,double pro_price,int qty,String importDate,int status){
        ArrayList<String> product=null;
        GetConnection.openConnection();
        try {

            statement = GetConnection.connection.createStatement();
            DatabaseMetaData dbm = GetConnection.connection.getMetaData();
            System.out.println("here");
            ResultSet tables = dbm.getTables(null, null, "tb_temp", null);
            String sqlCreateTable="CREATE TABLE tb_temp( id serial PRIMARY KEY, name VARCHAR (50) , unitprice float8, stockQty INT,importedDate VARCHAR (50),status INT);";
            String sqlInsertData="INSERT INTO tb_temp (name, unitPrice, stockQty, importedDate,status) VALUES ("+'"'+pro_name+'"'+","+pro_price+","+qty+","+'"'+importDate+'"'+",1)";
            System.out.println(sqlInsertData);
            if (checkWhetherTempTableHasValue())
                statement.executeUpdate(sqlInsertData);
            else {

                statement.executeUpdate(sqlCreateTable);
                statement.executeUpdate(sqlInsertData);
            }
            product = new ArrayList<>();
            int lastIndex = GetConnection.lastIndexOfRecord;
            GetConnection.lastIndexOfRecord++;
            product.add(lastIndex+GetConnection.separator+pro_name+GetConnection.separator+pro_price+GetConnection.separator+qty+GetConnection.separator+importDate);
        } catch (SQLException e) {
            System.out.println("exception here");
            e.printStackTrace();
        }
        GetConnection.closeConnection();
        return  product;
    }
    public static void generatData(){
        GetConnection.openConnection();
        try {

            statement = GetConnection.connection.createStatement();
            for(int i=1;i<=100;i++){
                String sql="INSERT INTO products (name, unitprice, stockqty, importeddate) VALUES ('Angkor', 8, 8, '2019-04-30')";
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        GetConnection.closeConnection();
    }
    public static void savAndRecovery(){
        GetConnection.openConnection();
        try {
            statement = GetConnection.connection.createStatement();
            String queryData="select * from tb_temp";
            ResultSet resultSet=statement.executeQuery(queryData);
            while (resultSet.next()){
                statement = GetConnection.connection.createStatement();
                String sql="INSERT INTO products (name, unitprice, stockQty, importeddate,status) VALUES ("+"'"+resultSet.getString(2)+"'"+","+resultSet.getDouble(3)+","+resultSet.getInt(4)+","+"'"+resultSet.getString(5)+"'"+ ","+resultSet.getInt(6) +");";
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        GetConnection.closeConnection();
    }

    public static void main(String[] args) {
        savAndRecovery();
    }
    /*
    * if(Data.checkWhetherStatementTableHasValue()>0){
    *   not work
    * }else{
    *     executeDataFromStatementTable();
    * }
    *
    *
    * */


    //direct execute so all conditional is checked
    public static void executeDataFromStatementTable(){
           ArrayList arrayList = new ArrayList();
            try {
             arrayList = Manipulator.statementQueryer("select * from tb_statements");
             if(arrayList.size()>0){
                 System.out.println(arrayList);
             }
            }catch (SQLException sql){

            }
    }

    public static int checkWhetherStatementTableHasValue(){

        GetConnection.openConnection();
        PreparedStatement preparedStatement ;
        int i =0;
        try {
            preparedStatement = GetConnection.connection.prepareStatement("select count(*) from tb_statements ");
            ResultSet resultSet  =  preparedStatement.executeQuery();

            while (resultSet.next()){
                i = resultSet.getInt(1);
            }

            if (i>0){
//                System.out.println("found");
                System.out.println(i);
                return i;
            }else{
                System.out.println("not found");
                return i;
            }
        }catch (SQLException sql){
            sql.printStackTrace();

        }finally {
            GetConnection.closeConnection();
        }

        return 0;
    }

    public static boolean checkWhetherTempTableHasValue(){
        boolean boo= false;
        GetConnection.openConnection();
        try {
            PreparedStatement preparedStatement = GetConnection.connection.prepareStatement("select * from tb_temp");
            preparedStatement.executeQuery();
            boo = true;
        }catch (SQLException sql){
            boo =false;
        }finally {
            GetConnection.closeConnection();
        }
        return boo;
    }
}
