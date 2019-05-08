package main;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * * line 72 change part of file
 * * line 77 config url and database for using
 * * line 86 set user and password
 * search this //<<<< to find what to update
 * GetConnection.connection
 * GetConnection.openConnection () => open connection
 * GetConnection.closeConnection () => close connection
 * */


public class GetConnection {
    public static final String separator = "®";
    public static Connection connection = null;
    public static int lastIndexOfRecord;
    public static int lastIndexOfDb;
    static {
        GetConnection.checkTable();
        lastIndexOfDb =  Manipulator.lastIdOf("tb_backup_history")+1;
    }

    public static void  openConnection(){
        try {
            Class.forName("org.postgresql.Driver");//<<<<update driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //<<<< port
        String url = "jdbc:postgresql://localhost:5432/Stock Management";
        String username = "postgres";
        String password = "123123123";
        try {

            connection = DriverManager.getConnection(url,username, password);
        } catch (SQLException e) {

            e.printStackTrace();

        }


    }

    public static void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void checkTable() {
        String sql1 = "CREATE TABLE IF NOT EXISTS products (id serial PRIMARY KEY, name VARCHAR(50), unitPrice float8, stockQty INT, importedDate VARCHAR(50), status INT)";
        String sql2 = "CREATE TABLE IF NOT EXISTS tb_statements(id serial PRIMARY KEY, statement VARCHAR(255))";
        String sql3 = "CREATE TABLE IF NOT EXISTS tb_backup_history(id serial PRIMARY KEY, db_name VARCHAR(50) NOT NULL, time timestamp default current_timestamp, status int)";
        GetConnection.openConnection();
        try{
            Statement statement = GetConnection.connection.createStatement();

            statement.execute(sql1);
            statement.execute(sql2);
            statement.execute(sql3);
        }catch (SQLException e){
            System.out.println("Error");
            System.out.println(e.getMessage());
        }
        finally{
            GetConnection.closeConnection();
        }

    }

}//endofcloseConnection

