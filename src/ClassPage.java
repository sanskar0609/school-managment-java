import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class ClassPage extends JFrame {
    public ClassPage() {
        setTitle("Manage Classes");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // UI Components
        JLabel lblClassName = new JLabel("Class Name:");
        JTextField txtClassName = new JTextField(20);

        JLabel lblStudentID = new JLabel("Student ID:");
        JTextField txtStudentID = new JTextField(10);

        JLabel lblTeacherID = new JLabel("Teacher ID:");
        JTextField txtTeacherID = new JTextField(10);

        JButton btnAddClass = new JButton("Add Class");
        JButton btnAssignStudent = new JButton("Assign Student to Class");
        JButton btnAssignTeacher = new JButton("Assign Teacher to Class");
        JButton btnDisplayClasses = new JButton("Display Classes");
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS)); // To arrange tables vertically

        // Button Listeners
        btnAddClass.addActionListener(e -> addClass(txtClassName, tablePanel));
        btnAssignStudent.addActionListener(e -> assignStudentToClass(txtClassName, txtStudentID, tablePanel));
        btnAssignTeacher.addActionListener(e -> assignTeacherToClass(txtClassName, txtTeacherID, tablePanel));
        btnDisplayClasses.addActionListener(e -> displayClasses(tablePanel));

        // Layout
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(lblClassName);
        inputPanel.add(txtClassName);
        inputPanel.add(lblStudentID);
        inputPanel.add(txtStudentID);
        inputPanel.add(lblTeacherID);
        inputPanel.add(txtTeacherID);
        inputPanel.add(btnAddClass);
        inputPanel.add(btnAssignStudent);

        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(btnAssignTeacher);
        southPanel.add(btnDisplayClasses);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(tablePanel), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Initial Load
        displayClasses(tablePanel);
    }

    // Adds a new class to the database
    private void addClass(JTextField txtClassName, JPanel tablePanel) {
        String className = txtClassName.getText();
        if (className.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Class name cannot be empty.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO classes (class_name) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, className);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Class added successfully!");
            displayClasses(tablePanel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Assigns a student to a class
    private void assignStudentToClass(JTextField txtClassName, JTextField txtStudentID, JPanel tablePanel) {
        assignToClass("student_id", txtClassName, txtStudentID, tablePanel);
    }

    // Assigns a teacher to a class
    private void assignTeacherToClass(JTextField txtClassName, JTextField txtTeacherID, JPanel tablePanel) {
        assignToClass("teacher_id", txtClassName, txtTeacherID, tablePanel);
    }

    // Generic method to assign a student/teacher to a class
    private void assignToClass(String column, JTextField txtClassName, JTextField txtEntityID, JPanel tablePanel) {
        String className = txtClassName.getText();
        int entityId;

        try {
            entityId = Integer.parseInt(txtEntityID.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, column.equals("student_id") ? "Student ID must be a number." : "Teacher ID must be a number.");
            return;
        }

        if (className.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Class name cannot be empty.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            // Find the class ID by name
            String findClassSql = "SELECT id FROM classes WHERE class_name = ?";
            PreparedStatement findClassStmt = conn.prepareStatement(findClassSql);
            findClassStmt.setString(1, className);
            ResultSet rs = findClassStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Class not found. Add the class first.");
                return;
            }
            int classId = rs.getInt("id");

            // Assign the entity (student/teacher) to the class
            String assignSql = "UPDATE classes SET " + column + " = ? WHERE id = ?";
            PreparedStatement assignStmt = conn.prepareStatement(assignSql);
            assignStmt.setInt(1, entityId);
            assignStmt.setInt(2, classId);
            assignStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, (column.equals("student_id") ? "Student" : "Teacher") + " assigned to class successfully!");
            displayClasses(tablePanel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Displays all classes with assigned students and teachers in different tables for each class
    private void displayClasses(JPanel tablePanel) {
        tablePanel.removeAll();  // Clear existing tables
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT classes.id AS class_id, classes.class_name, " +
                             "students.id AS student_id, students.name AS student_name, " +
                             "teachers.id AS teacher_id, teachers.name AS teacher_name " +
                             "FROM classes " +
                             "LEFT JOIN students ON classes.student_id = students.id " +
                             "LEFT JOIN teachers ON classes.teacher_id = teachers.id")) {

            Map<String, Object[]> classData = new LinkedHashMap<>();
            while (rs.next()) {
                String className = rs.getString("class_name");
                Object[] row = new Object[]{
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getInt("teacher_id"),
                        rs.getString("teacher_name")
                };
                classData.put(className, row);
            }

            for (Map.Entry<String, Object[]> entry : classData.entrySet()) {
                String className = entry.getKey();
                Object[] data = entry.getValue();

                String[] columns = {"Student ID", "Student Name", "Teacher ID", "Teacher Name"};
                Object[][] rowData = {data};

                JTable table = new JTable(rowData, columns);
                JScrollPane scrollPane = new JScrollPane(table);
                JPanel classPanel = new JPanel(new BorderLayout());
                classPanel.setBorder(BorderFactory.createTitledBorder(className));
                classPanel.add(scrollPane, BorderLayout.CENTER);

                tablePanel.add(classPanel);
            }

            tablePanel.revalidate();
            tablePanel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
