package form;
import Database.DatabaseConnection;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;

    public RegisterForm() {
        setTitle("Register");
        setSize(300, 200);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 20, 100, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(120, 20, 150, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 60, 100, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 60, 150, 25);
        add(passwordField);

        registerButton = new JButton("Register");
        registerButton.setBounds(90, 100, 120, 30);
        add(registerButton);

        registerButton.addActionListener(e -> register());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void register() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users(username, password) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registrasi sukses!");
            new LoginForm().setVisible(true);
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
