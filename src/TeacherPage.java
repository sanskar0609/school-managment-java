import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class TeacherPage extends JFrame {
    public TeacherPage() {
        setTitle("Manage Teachers");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btnBackToHome = new JButton("Back to Home");
        btnBackToHome.addActionListener(e -> {
            dispose(); // Close the TeacherPage
            HomePage homePage = new HomePage();
            homePage.setVisible(true); // Reopen HomePage
        });

        // Labels and Input Fields
        JLabel lblName = new JLabel("Name:");
        JLabel lblSubject = new JLabel("Subject:");
        JLabel lblExperience = new JLabel("Experience (Years):");

        JTextField txtName = new JTextField(20);
        JTextField txtSubject = new JTextField(20);
        JTextField txtExperience = new JTextField(10);

        // Buttons
        JButton btnAdd = new JButton("Add Teacher");
        JButton btnUpdate = new JButton("Update Teacher");
        JButton btnDelete = new JButton("Delete Teacher");
        JTextArea txtArea = new JTextArea(15, 50);

        // Button Actions
        btnAdd.addActionListener(e -> addTeacher(txtName, txtSubject, txtExperience, txtArea));
        btnUpdate.addActionListener(e -> updateTeacher(txtName, txtSubject, txtExperience, txtArea));
        btnDelete.addActionListener(e -> deleteTeacher(txtName, txtArea));

        // Panel Layout
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(lblName);
        inputPanel.add(txtName);
        inputPanel.add(lblSubject);
        inputPanel.add(txtSubject);
        inputPanel.add(lblExperience);
        inputPanel.add(txtExperience);
        inputPanel.add(btnAdd);
        inputPanel.add(btnUpdate);


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBackToHome);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(txtArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        displayTeachers(txtArea);
    }

    private void addTeacher(JTextField txtName, JTextField txtSubject, JTextField txtExperience, JTextArea txtArea) {
        String name = txtName.getText();
        String subject = txtSubject.getText();
        int experience;

        try {
            experience = Integer.parseInt(txtExperience.getText());
        } catch (NumberFormatException ex) {
            txtArea.append("Experience must be a number.\n");
            return;
        }

        if (name.isEmpty() || subject.isEmpty()) {
            txtArea.append("Name and subject cannot be empty.\n");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO teachers (name, subject, experience) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, subject);
            stmt.setInt(3, experience);
            stmt.executeUpdate();

            txtArea.append("Teacher added successfully!\n");
            displayTeachers(txtArea);
        } catch (Exception ex) {
            txtArea.append("Error: " + ex.getMessage() + "\n");
        }
    }

    private void updateTeacher(JTextField txtName, JTextField txtSubject, JTextField txtExperience, JTextArea txtArea) {
        String name = txtName.getText();
        String subject = txtSubject.getText();
        int experience;

        try {
            experience = Integer.parseInt(txtExperience.getText());
        } catch (NumberFormatException ex) {
            txtArea.append("Experience must be a number.\n");
            return;
        }

        if (name.isEmpty() || subject.isEmpty()) {
            txtArea.append("Name and subject cannot be empty.\n");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE teachers SET subject = ?, experience = ? WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, subject);
            stmt.setInt(2, experience);
            stmt.setString(3, name);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                txtArea.append("Teacher updated successfully!\n");
            } else {
                txtArea.append("Teacher not found.\n");
            }
            displayTeachers(txtArea);
        } catch (Exception ex) {
            txtArea.append("Error: " + ex.getMessage() + "\n");
        }
    }

    private void deleteTeacher(JTextField txtName, JTextArea txtArea) {
        String name = txtName.getText();

        if (name.isEmpty()) {
            txtArea.append("Name cannot be empty.\n");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM teachers WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                txtArea.append("Teacher deleted successfully!\n");
            } else {
                txtArea.append("Teacher not found.\n");
            }
            displayTeachers(txtArea);
        } catch (Exception ex) {
            txtArea.append("Error: " + ex.getMessage() + "\n");
        }
    }

    private void displayTeachers(JTextArea txtArea) {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM teachers")) {

            txtArea.setText("Teachers:\n");
            while (rs.next()) {
                txtArea.append("ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("name") +
                        ", Subject: " + rs.getString("subject") +
                        ", Experience: " + rs.getInt("experience") + " years\n");
            }
        } catch (Exception ex) {
            txtArea.append("Error: " + ex.getMessage() + "\n");
        }
    }
}
