package org.lemanoman.contas.service;


import org.lemanoman.contas.utils.ArquivoUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Database {
    private Connection connection = null;


    private String databaseLocation;

    public Database(String databaseLocation) {
        this.databaseLocation = databaseLocation;
    }


    public List<Map<String, Object>> doSelect(String query){
        System.out.println("Doing select: "+query);
        LinkedList<Map<String, Object>> list = new LinkedList<>();
        try {

            Statement statement = getConnection().createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData resultSetMetaData = rs.getMetaData();
                int maxCols = resultSetMetaData.getColumnCount();
                for(int c = 1;c<=maxCols;c++){
                    row.put(resultSetMetaData.getColumnName(c),rs.getObject(c));
                }
                list.add(row);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Object> doSelectGetOne(String query, Object... valores){
        System.out.println("Doing select: "+query);
        Map<String, Object> row = new HashMap<>();
        try {
            PreparedStatement statement = buildStatement(query,valores);
            ResultSet rs = statement.executeQuery();
            row = toMap(rs);
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row;
    }


    private Map<String,Object> toMap(ResultSet rs) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        if (rs.next()){
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int maxCols = resultSetMetaData.getColumnCount();
            for(int c = 1;c<=maxCols;c++){
                row.put(resultSetMetaData.getColumnName(c),rs.getObject(c));
            }
        }
        return row;
    }

    public long doUpdate(String query){
        try {
            System.out.println("Doing query: "+query);
            Statement statement = getConnection().createStatement();
            long updated = statement.executeUpdate(query);
            statement.close();
            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private PreparedStatement buildStatement(String query, Object...valores) throws SQLException {
        PreparedStatement  statement = getConnection().prepareStatement(query);
        int index = 1;
        for(int i=0;i<valores.length;i++){
            Object valor = valores[i];
            if(valor instanceof String){
                statement.setString(index,(String) valor);
            }else if(valor instanceof Integer){
                statement.setInt(index,(Integer)valor);
            }else if(valor instanceof Double){
                statement.setDouble(index,(Double)valor);
            }else if(valor instanceof Long){
                statement.setLong(index,(Long) valor);
            }else if(valor instanceof Boolean){
                statement.setBoolean(index,(Boolean) valor);
            }else if(valor== null){
                statement.setString(index,"null");
            }else {
                throw new SQLException("Prepared statement invalid Type..."+valor);
            }
            index++;
        }
        return statement;
    }

    public long doPreparedUpdate(String query, Object...valores) {
        try {
            System.out.println("Doing query: "+query);
            PreparedStatement statement = buildStatement(query,valores);
            long updated = statement.executeUpdate();
            statement.close();
            return updated;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean execute(String query){
        try {
            Statement statement = getConnection().createStatement();
            boolean updated = statement.execute(query);
            statement.close();
            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void dropAndCreate() {
        File output = new File("dbquery.sql");
        ArquivoUtils.copyFromClasspath("/dbquery.sql", output, getClass());
        String createScript = readFileLines(output);
        output.delete();
        try {
            System.out.println("Conexão realizada !!!!");
            Statement statement = getConnection().createStatement();
            statement.executeUpdate(createScript);
            statement.close();

            Statement smt = getConnection().createStatement();
            ResultSet rs = smt.executeQuery(
                    "SELECT\n" +
                    "    name\n" +
                    "FROM\n" +
                    "    sqlite_master\n" +
                    "WHERE\n" +
                    "        type ='table' AND\n" +
                    "        name NOT LIKE 'sqlite_%';");
            while(rs.next()){
                System.out.println("Table created: "+rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void doConstruct(){

    }

    public void close(){
        if(getConnection()!=null){
            try {
                getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String... args) {
        new Database("contas.db").dropAndCreate();


    }

    public Connection getConnection() {
        try {
            if(connection!=null && !connection.isClosed()){
                return connection;
            }else{
                return connection = DriverManager.getConnection("jdbc:sqlite:"+databaseLocation);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }

    }



    private String readFileLines(File file) {
        try {
            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);      //appends line to string buffer
                sb.append("\n");     //line feed
            }
            fr.close();    //closes the stream and release the resources
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
