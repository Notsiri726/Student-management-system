import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FeeManager {
    private Connection conn;

    public FeeManager(Connection conn) {
        this.conn = conn;
    }

    public boolean addFee(String studentId, double amount, String status) {
        String sql = "INSERT INTO fee (student_id, amount, status) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, status);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding fee for student ID " + studentId);
            e.printStackTrace();
            return false;
        }
    }
}
