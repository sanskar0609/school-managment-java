import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StudentPage extends JFrame {
    public StudentPage() {
        setTitle("Manage Students");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btnBackToHome = new JButton("Back to Home");
        btnBackToHome.addActionListener(e -> {
            dispose(); // Close the StudentPage
            HomePage homePage = new HomePage();
            homePage.setVisible(true); // Reopen HomePage
        });

        JLabel lblId = new JLabel("ID:");
        JLabel lblName = new JLabel("Name:");
        JLabel lblAge = new JLabel("Age:");
        JLabel lblGrade = new JLabel("Grade:");
        JTextField txtId = new JTextField(5);
        JTextField txtName = new JTextField(20);
        JTextField txtAge = new JTextField(5);
        JTextField txtGrade = new JTextField(10);
        JButton btnAdd = new JButton("Add Student");
        JButton btnUpdate = new JButton("Update Student");
        JButton btnDelete = new JButton("Delete Student");
        JButton btnRead = new JButton("View Students");
        JTextArea txtArea = new JTextArea(15, 50);
        txtArea.setEditable(false);

        btnAdd.addActionListener(e -> addStudent(txtName, txtAge, txtGrade, txtArea));
        btnUpdate.addActionListener(e -> updateStudent(txtId, txtName, txtAge, txtGrade, txtArea));
        btnDelete.addActionListener(e -> deleteStudent(txtId, txtArea));
        btnRead.addActionListener(e -> displayStudents(txtArea));

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(lblId);
        panel.add(txtId);
        panel.add(lblName);
        panel.add(txtName);
        panel.add(lblAge);
        panel.add(txtAge);
        panel.add(lblGrade);
        panel.add(txtGrade);
        panel.add(btnAdd);
        panel.add(btnUpdate);

        JPanel actionPanel = new JPanel();
        actionPanel.add(btnDelete);
        actionPanel.add(btnRead);

        JPanel southPanel = new JPanel();
        southPanel.add(btnBackToHome);

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(txtArea), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        displayStudents(txtArea); // Initial load
    }

    private void addStudent(JTextField txtName, JTextField txtAge, JTextField txtGrade, JTextArea txtArea) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO students (name, age, grade) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtName.getText());
            stmt.setInt(2, Integer.parseInt(txtAge.getText()));
            stmt.setString(3, txtGrade.getText());
            stmt.executeUpdate();

            txtArea.append("Student added successfully!\n");
            displayStudents(txtArea);
        } catch (Exception ex) {
            txtArea.append("Error: " + ex.getMessage() + "\n");
        }
    }

    private void updateStudent(JTextField txtId, JTextField txtName, JTextField txtAge, JTextField txtGrade, JTextArea txtArea) {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE students SET name = ?, age = ?, grade = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtName.getText());
            stmt.setInt(2, Integer.parseInt(txtAge.getText()));
            stmt.setString(3, txtGrade.getText());
            stmt.setInt(4, Integer.parseInt(txtId.getText()));
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                txtArea.append("Student updated successfully!\n");
            } else {
                txtArea.append("No student found with ID: " + txtId.getText() + "\n");
            }
            displayStudents(txtArea);
        } catch (Exception ex) {
            txtArea.append("Error: " + ex.getMessage() + "\n");
        }
    }

    private void deleteStudent(JTextField txtId, JTextArea txtArea) {
        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM students WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(txtId.getText()));
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                txtArea.append("Student deleted successfully!\n");
            } else {
                txtArea.append("No student found with ID: " + txtId.getText() + "\n");
            }
            displayStudents(txtArea);
        } catch (Exception ex) {
            txtArea.append("Error: " + ex.getMessage() + "\n");
        }
    }

    private void displayStudents(JTextArea txtArea) {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {

            txtArea.setText("Students:\n");
            while (rs.next()) {
                txtArea.append("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name")
                        + ", Age: " + rs.getInt("age") + ", Grade: " + rs.getString("grade") + "\n");
            }
        } catch (Exception ex) {
            txtArea.append("Error: " + ex.getMessage() + "\n");
        }
    }
}
