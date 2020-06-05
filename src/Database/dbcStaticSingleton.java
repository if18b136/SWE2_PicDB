package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class dbcStaticSingleton {

    private static dbcStaticSingleton jdbc;

    static {
        try {
            jdbc = new dbcStaticSingleton();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection con;

    private dbcStaticSingleton() throws SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1:3306/picdb";
            String username = "root";
            String password = "";
            this.con = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException cnf) {
            System.out.println("Database Connection Creation Failed : " + cnf.getMessage());
            cnf.printStackTrace();
        }
    }

    public Connection getConnection(){ return this.con; }

    public static dbcStaticSingleton getInstance()throws SQLException{
        return jdbc;
    }
}
