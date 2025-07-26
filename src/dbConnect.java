import java.sql.*;

public class dbConnect {
private static Connection mycon = null;
public static Connection getConnection() throws ClassNotFoundException, SQLException{
    String db = "studentdata";
    String user = "root", pass = "JaiJagannath7";
String url = "jdbc:mysql://localhost:3306/"+db;
Class.forName("com.mysql.cj.jdbc.Driver");
Connection conn = DriverManager.getConnection(url,user,pass);
return conn;


}
}
