import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceManagement {

    private Connection conn;

    // Constructor to initialize the database connection
    public AttendanceManagement(Connection conn) {
        this.conn = conn;
    }

    // Method to add attendance
    public boolean addAttendance(String studentId, Date date, String status) {
        String sql = "INSERT INTO attendance (student_id, date, status) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setDate(2, date);
            pstmt.setString(3, status);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to get all attendance records of a student
    public List<String> getAttendanceByStudent(String studentId) {
        List<String> attendanceList = new ArrayList<>();
        String sql = "SELECT date, status FROM attendance WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Date date = rs.getDate("date");
                String status = rs.getString("status");
                attendanceList.add(date.toString() + " - " + status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }

    // Optional: Method to delete an attendance record (by date)
    public boolean deleteAttendance(String studentId, Date date) {
        String sql = "DELETE FROM attendance WHERE student_id = ? AND date = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setDate(2, date);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
