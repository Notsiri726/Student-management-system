import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AppGUI extends JFrame implements ActionListener {
    private final JLabel studentIdLabel, firstNameLabel, lastNameLabel, majorLabel, phnLabel, gpaLabel, dobLabel;
    private final JTextField studentIdField, firstNameField, lastNameField, majorField, phnField, gpaField, dobField;
    private final JButton addButton, displayButton, modifyButton, sortButton, searchButton;
    private final JButton feeButton, attendanceButton, deleteButton;

    private final FeeManager feeManager;
    private final AttendanceManagement attendanceManager;

    public AppGUI() {
        super("🎓 Student Management System");

        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Enter Student Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        studentIdLabel = new JLabel("Student ID:");
        firstNameLabel = new JLabel("First Name:");
        lastNameLabel = new JLabel("Last Name:");
        majorLabel = new JLabel("Major:");
        phnLabel = new JLabel("Phone:");
        gpaLabel = new JLabel("GPA:");
        dobLabel = new JLabel("DOB (yyyy-mm-dd):");

        studentIdField = new JTextField(15);
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        majorField = new JTextField(15);
        phnField = new JTextField(15);
        gpaField = new JTextField(15);
        dobField = new JTextField(15);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(studentIdLabel, gbc);
        gbc.gridx = 1; formPanel.add(studentIdField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; formPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1; formPanel.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; formPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1; formPanel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; formPanel.add(majorLabel, gbc);
        gbc.gridx = 1; formPanel.add(majorField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; formPanel.add(phnLabel, gbc);
        gbc.gridx = 1; formPanel.add(phnField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; formPanel.add(gpaLabel, gbc);
        gbc.gridx = 1; formPanel.add(gpaField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; formPanel.add(dobLabel, gbc);
        gbc.gridx = 1; formPanel.add(dobField, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        addButton = new JButton(" Add");
        displayButton = new JButton("Display");
        sortButton = new JButton(" Sort");
        searchButton = new JButton(" Search");
        modifyButton = new JButton("Modify");
        feeButton = new JButton(" Add Fee");
        attendanceButton = new JButton(" Mark Attendance");
        deleteButton = new JButton(" Delete");

        JButton[] buttons = {addButton, displayButton, sortButton, searchButton, modifyButton, feeButton, attendanceButton, deleteButton};
        for (JButton btn : buttons) {
            btn.addActionListener(this);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(240, 240, 240));
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            buttonPanel.add(btn);
        }

        JPanel wrapper = new JPanel(new BorderLayout(15, 15));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);

        add(wrapper);
        setSize(680, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            Connection conn = dbConnect.getConnection();
            feeManager = new FeeManager(conn);
            attendanceManager = new AttendanceManagement(conn);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize managers.");
        }
    }

    public void actionPerformed(ActionEvent event) {
        try (Connection conn = dbConnect.getConnection()) {
            Table tb = new Table();

            if (event.getSource() == addButton) {
                String sql = "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, studentIdField.getText());
                    pstmt.setString(2, firstNameField.getText());
                    pstmt.setString(3, lastNameField.getText());
                    pstmt.setString(4, majorField.getText());
                    pstmt.setString(5, phnField.getText());
                    pstmt.setBigDecimal(6, new java.math.BigDecimal(gpaField.getText()));
                    pstmt.setString(7, dobField.getText());
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, " Student Record Added Successfully.");
                }

            } else if (event.getSource() == displayButton) {
                String sql = "SELECT s.*, f.amount AS fee_amount, f.status AS fee_status, a.date AS attendance_date, a.status AS attendance_status " +
                        "FROM students s " +
                        "LEFT JOIN fee f ON s.student_id = f.student_id " +
                        "LEFT JOIN attendance a ON s.student_id = a.student_id";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    ResultSet rs = pstmt.executeQuery();
                    JTable table = new JTable(tb.buildTableModel(rs));
                    JOptionPane.showMessageDialog(null, new JScrollPane(table));
                }

            } else if (event.getSource() == sortButton) {
                String[] options = {"First Name", "Last Name", "Major"};
                int choice = JOptionPane.showOptionDialog(null, "Sort by:", "Sort", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                String column = switch (choice) {
                    case 0 -> "first_name";
                    case 1 -> "last_name";
                    case 2 -> "major";
                    default -> "";
                };

                String sql = "SELECT * FROM students ORDER BY " + column;
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    ResultSet rs = pstmt.executeQuery();
                    JTable table = new JTable(tb.buildTableModel(rs));
                    JOptionPane.showMessageDialog(null, new JScrollPane(table));
                }

            } else if (event.getSource() == searchButton) {
                String[] options = {"Student ID", "Last Name", "Major"};
                int choice = JOptionPane.showOptionDialog(null, "Search by:", "Search", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                String column = switch (choice) {
                    case 0 -> "student_id";
                    case 1 -> "last_name";
                    case 2 -> "major";
                    default -> "";
                };

                String searchTerm = JOptionPane.showInputDialog("Enter Search Term:");
                String sql = "SELECT * FROM students WHERE " + column + " LIKE ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, "%" + searchTerm + "%");
                    ResultSet rs = pstmt.executeQuery();
                    JTable table = new JTable(tb.buildTableModel(rs));
                    JOptionPane.showMessageDialog(null, new JScrollPane(table));
                }

            } else if (event.getSource() == modifyButton) {
                String studentID = JOptionPane.showInputDialog("Enter Student ID:");
                String checkSql = "SELECT * FROM students WHERE student_id = ?";

                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, studentID);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        String[] options = {"First Name", "Last Name", "Major", "Phone", "GPA", "Date Of Birth"};
                        int choice = JOptionPane.showOptionDialog(null, "Select field to modify:", "Modify",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                        String column = switch (choice) {
                            case 0 -> "first_name";
                            case 1 -> "last_name";
                            case 2 -> "major";
                            case 3 -> "phone";
                            case 4 -> "gpa";
                            case 5 -> "date_of_birth";
                            default -> "";
                        };

                        String newValue = JOptionPane.showInputDialog("Enter new value:");
                        String updateSql = "UPDATE students SET " + column + " = ? WHERE student_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            if (column.equals("gpa")) {
                                updateStmt.setBigDecimal(1, new java.math.BigDecimal(newValue));
                            } else {
                                updateStmt.setString(1, newValue);
                            }
                            updateStmt.setString(2, studentID);
                            updateStmt.executeUpdate();
                            JOptionPane.showMessageDialog(null, " Student data updated successfully");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, " ERROR: Student record not found");
                    }
                }

            } else if (event.getSource() == feeButton) {
                String studentId = studentIdField.getText();
                String amountStr = JOptionPane.showInputDialog("Enter Fee Amount:");
                String status = JOptionPane.showInputDialog("Enter Fee Status (Paid/Unpaid):");

                boolean success = feeManager.addFee(studentId, Double.parseDouble(amountStr), status);
                JOptionPane.showMessageDialog(null, success ? "Fee recorded successfully." : " Fee recording failed.");

            } else if (event.getSource() == attendanceButton) {
                String studentId = studentIdField.getText();
                String status = JOptionPane.showInputDialog("Enter Attendance Status (Present/Absent):");

                boolean success = attendanceManager.addAttendance(studentId, new java.sql.Date(System.currentTimeMillis()), status);
                JOptionPane.showMessageDialog(null, success ? " Attendance recorded." : " Attendance recording failed.");

            } else if (event.getSource() == deleteButton) {
                String studentID = JOptionPane.showInputDialog("Enter Student ID to delete:");

                try (PreparedStatement pstmt1 = conn.prepareStatement("DELETE FROM attendance WHERE student_id = ?");
                     PreparedStatement pstmt2 = conn.prepareStatement("DELETE FROM fee WHERE student_id = ?");
                     PreparedStatement pstmt3 = conn.prepareStatement("DELETE FROM students WHERE student_id = ?")) {

                    pstmt1.setString(1, studentID); pstmt1.executeUpdate();
                    pstmt2.setString(1, studentID); pstmt2.executeUpdate();
                    pstmt3.setString(1, studentID);
                    int rowsAffected = pstmt3.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, " Student record deleted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(null, " No such student found.");
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, " Something went wrong:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppGUI::new);
    }
}
