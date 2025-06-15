package form;

import Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class KehadiranPanel extends JFrame {
    private JComboBox<String> nimComboBox;
    private JTextField pertemuanField;
    private JComboBox<String> statusBox;
    private JButton simpanBtn, editBtn, kembaliBtn;
    private JTable table;
    private DefaultTableModel model;

    public KehadiranPanel() {
        setTitle("Kehadiran Mahasiswa");
        setSize(600, 400);
        setLayout(null);

        JLabel nimLabel = new JLabel("NIM:");
        nimLabel.setBounds(20, 20, 50, 25);
        add(nimLabel);

        nimComboBox = new JComboBox<>();
        nimComboBox.setBounds(80, 20, 150, 25);
        add(nimComboBox);

        JLabel pertemuanLabel = new JLabel("Pertemuan (1-16):");
        pertemuanLabel.setBounds(20, 60, 120, 25);
        add(pertemuanLabel);

        pertemuanField = new JTextField();
        pertemuanField.setBounds(150, 60, 80, 25);
        add(pertemuanField);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(20, 100, 50, 25);
        add(statusLabel);

        statusBox = new JComboBox<>(new String[]{"Hadir", "Sakit", "Izin", "Alfa"});
        statusBox.setBounds(80, 100, 150, 25);
        add(statusBox);

        simpanBtn = new JButton("Simpan");
        simpanBtn.setBounds(20, 140, 90, 30);
        add(simpanBtn);

        editBtn = new JButton("Edit");
        editBtn.setBounds(120, 140, 90, 30);
        add(editBtn);

        kembaliBtn = new JButton("Kembali");
        kembaliBtn.setBounds(20, 300, 90, 30);
        add(kembaliBtn);

        model = new DefaultTableModel(new String[]{"NIM", "Pertemuan", "Status"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(280, 20, 280, 320);
        add(scrollPane);

        loadMahasiswaToComboBox();

        simpanBtn.addActionListener(e -> simpanKehadiran());
        editBtn.addActionListener(e -> editKehadiran());
        kembaliBtn.addActionListener(e -> dispose());

        nimComboBox.addActionListener(e -> {
            String selectedNim = (String) nimComboBox.getSelectedItem();
            if (selectedNim != null) {
                tampilData(selectedNim);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    nimComboBox.setSelectedItem(model.getValueAt(row, 0).toString());
                    pertemuanField.setText(model.getValueAt(row, 1).toString());
                    statusBox.setSelectedItem(model.getValueAt(row, 2).toString());
                    pertemuanField.setEditable(false);
                    nimComboBox.setEnabled(false);
                }
            }
        });

        if (nimComboBox.getItemCount() > 0) {
            tampilData((String) nimComboBox.getSelectedItem());
        }

        setLocationRelativeTo(null);
    }

    private void loadMahasiswaToComboBox() {
        nimComboBox.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nim FROM mahasiswa");
            while (rs.next()) {
                nimComboBox.addItem(rs.getString("nim"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load data mahasiswa: " + e.getMessage());
        }
    }

    private void simpanKehadiran() {
        try {
            int pertemuan = Integer.parseInt(pertemuanField.getText());
            if (pertemuan < 1 || pertemuan > 16) {
                JOptionPane.showMessageDialog(this, "Pertemuan harus antara 1-16");
                return;
            }
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "REPLACE INTO kehadiran(nim, pertemuan, status) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, (String) nimComboBox.getSelectedItem());
                stmt.setInt(2, pertemuan);
                stmt.setString(3, (String) statusBox.getSelectedItem());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data kehadiran disimpan.");
                tampilData((String) nimComboBox.getSelectedItem());
                resetForm();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Pertemuan harus angka.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error simpan kehadiran: " + e.getMessage());
        }
    }

    private void editKehadiran() {
        try {
            int pertemuan = Integer.parseInt(pertemuanField.getText());
            if (pertemuan < 1 || pertemuan > 16) {
                JOptionPane.showMessageDialog(this, "Pertemuan harus antara 1-16");
                return;
            }
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE kehadiran SET status = ? WHERE nim = ? AND pertemuan = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, (String) statusBox.getSelectedItem());
                stmt.setString(2, (String) nimComboBox.getSelectedItem());
                stmt.setInt(3, pertemuan);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Data kehadiran berhasil diupdate.");
                    tampilData((String) nimComboBox.getSelectedItem());
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Data kehadiran tidak ditemukan.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Pertemuan harus angka.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error update kehadiran: " + e.getMessage());
        }
    }

    private void tampilData(String nim) {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM kehadiran WHERE nim = ? ORDER BY pertemuan";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nim);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("nim"),
                        rs.getInt("pertemuan"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + e.getMessage());
        }
    }

    private void resetForm() {
        nimComboBox.setEnabled(true);
        pertemuanField.setText("");
        statusBox.setSelectedIndex(0);
        pertemuanField.setEditable(true);
    }
}
