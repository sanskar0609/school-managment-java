import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.imageio.ImageIO;

public class HomePage extends JFrame {
    private Image backgroundImage;

    public HomePage() {
        setTitle("School Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("back.jpg")); // Update with your image path
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton btnStudents = new JButton("Manage Students");
        JButton btnTeachers = new JButton("Manage Teachers");
        JButton btnClasses = new JButton("Manage Classes");

        btnStudents.addActionListener(e -> openPage(new StudentPage()));
        btnTeachers.addActionListener(e -> openPage(new TeacherPage()));
        btnClasses.addActionListener(e -> openPage(new ClassPage()));

        // Customize buttons
        customizeButton(btnStudents);
        customizeButton(btnTeachers);
        customizeButton(btnClasses);

        JPanel panel = new JPanel();
        panel.setOpaque(false); // Make the panel transparent
        panel.add(btnStudents);
        panel.add(btnTeachers);
        panel.add(btnClasses);

        // Use a layout manager to center the buttons
        panel.setLayout(new GridLayout(3, 1, 10, 10)); // 3 rows, 1 column, 10px gaps

        add(panel);
    }

    private void customizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 204)); // A blue color
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
    }

    private void openPage(JFrame page) {
        page.setVisible(true);
        dispose();
    }


    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomePage homePage = new HomePage();
            homePage.setVisible(true);
        });
    }
}