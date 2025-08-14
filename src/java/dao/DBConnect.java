/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author yunkh
 */

public class DBConnect {
    public Connection conn = null;
    private String URL="jdbc:sqlserver://localhost:1433;databaseName=kicap";
    private String username="yunkhngn";
    private String password="123";
    
    public DBConnect(String URL, String userName, String password) {
        try {
            //call driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try {
                //connect
                conn=DriverManager.getConnection(URL, userName, password);
                System.out.println("Connected");
            } catch (SQLException ex) {
                Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public DBConnect(){
        this("jdbc:sqlserver://localhost:1433;databaseName=kicap","yunkhngn","123");
        //this(URL, userName, password)
    }
    
    public ResultSet getData(String sql){
        ResultSet rs=null;
        try {
            Statement state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            rs=state.executeQuery(sql);
        } catch (SQLException ex) {
            System.getLogger(DBConnect.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return rs;
    }
}