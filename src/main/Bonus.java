package main;

import helper.Validator;
import model.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Bonus {
    /*
     * Insert data tb_backup_history
     * create a temp_table${i} then sec function insert data from PRODUCTS
     *
     * */
    //main call
    public static void backUpDatabase() {
        if (Data.checkWhetherStatementTableHasValue() > 0 || Data.checkWhetherTempTableHasValue()) {
            Complementary.tabler("Please Save Before Backing Up Data");
            return;
        }
        InsertBackUpHistoryToTable();
    }

    //1B
    private static void InsertBackUpHistoryToTable() {
        //String str = "  INSERT INTO tb_backup_history( db_name, status) VALUES ('',1) ";
        try {
            int i = GetConnection.lastIndexOfDb;
            String tb_Name = "temp_table" + i;
            Manipulator.updater("Insert into tb_backup_history (db_name,status) values ('" + tb_Name + "',1);");
            //temp_table[i] -> backup function()
            createTempTable(tb_Name);
            getDataBackUp(tb_Name);
            GetConnection.lastIndexOfDb++;

        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }

    //2B
    private static void createTempTable(String name) {
        GetConnection.openConnection();
        try {
            Statement statement = GetConnection.connection.createStatement();
            String sqlCreateTable = "CREATE TABLE " + name + " ( id serial PRIMARY KEY, name VARCHAR (50) , unitPrice float8, stockQty INT,importedDate VARCHAR (50),status INT);";
            statement.executeUpdate(sqlCreateTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        GetConnection.closeConnection();
    }

    //3B
    private static void getDataBackUp(String name) {
        GetConnection.openConnection();
        try {
            Statement statement = GetConnection.connection.createStatement();
            String sql = "INSERT INTO " + name + " (name, unitPrice, stockQty , importedDate, status) SELECT name, unitPrice, stockQty, importedDate, status FROM products order by id asc; ";
            statement.executeUpdate(sql);
            Complementary.tabler("Backed Up Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            GetConnection.closeConnection();
        }

    }/*end of backingup process*/



    /*
     * restore start
     *
     * */


    //main call
    public static void restoreDatabase() {
        ArrayList<DatabaseHistory> arrayList = BackupHistoryReader();
        if (arrayList.size() == 0) {
            Complementary.tabler("Nothing To Restore");
            return;
        }

        arrayList.forEach((value)->{
            Complementary.tabler(""+(arrayList.indexOf(value)+1),value.dbName,value.timeStamp);

        });
        int numberToRestore = Validator.readInt("Your decision");
        try {
            arrayList.get(numberToRestore-1) ;
        }catch (Exception sql){
            Complementary.tabler("DB NOT FOUND OR INPUT MISTAKE ");
            return;
        }

        restore(arrayList.get(numberToRestore-1).dbName);

    }

    private static ArrayList<DatabaseHistory> BackupHistoryReader() {
        GetConnection.openConnection();
        PreparedStatement preparedStatement;
        ArrayList<DatabaseHistory> arrayList = new ArrayList();
        try {
            preparedStatement = GetConnection.connection.prepareStatement("select * from tb_backup_history");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                arrayList.add(new DatabaseHistory(resultSet.getString(2), resultSet.getTimestamp(3).toString()));
            }
        } catch (Exception ex) {

        } finally {
            GetConnection.closeConnection();
        }

        return arrayList;
    }

    //put name of restoring table
    private static void restore(String tableName) {
        GetConnection.openConnection();
        try {
            Statement statement = GetConnection.connection.createStatement();
            String deleteTable = "DROP TABLE products";
            statement.executeUpdate(deleteTable);
            GetConnection.openConnection();
            statement = GetConnection.connection.createStatement();
            createTempTable("products");
            String sql = "INSERT INTO products (name, unitprice, stockqty , importeddate, status) SELECT name, unitprice, stockqty, importeddate, status FROM  " + tableName;
            statement.executeUpdate(sql);
            Complementary.tabler("Successfully Restored");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            GetConnection.closeConnection();
        }


    }
}
class DatabaseHistory{
    public String dbName;
    public String timeStamp;

    public DatabaseHistory(){

    }

    public DatabaseHistory(String dbName, String timeStamp) {
        this.dbName = dbName;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "dbName='" + dbName + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '\n';
    }
}

