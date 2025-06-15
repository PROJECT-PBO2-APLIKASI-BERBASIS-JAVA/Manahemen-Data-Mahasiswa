package form;

import Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class MahasiswaPanel extends JFrame {
    private JTextField nimField, namaField, kelasField, searchField;
    private JCheckBox krsCheck;
    private JTable table;
    private DefaultTableModel model;
    private JButton simpanBtn, editBtn, kembaliBtn;

    public MahasiswaPanel() {
        setTitle("Manajemen Mahasiswa");
        setSize(650, 450);
        setLayout(null);

        JLabel nimLabel = new JLabel("NIM:");
        nimLabel.setBounds(20, 20, 50, 25);
        add(nimLabel);

        nimField = new JTextField();
        nimField.setBounds(80, 20, 150, 25);
        add(nimField);

        JLabel namaLabel = new JLabel("Nama:");
        namaLabel.setBounds(20, 60, 50, 25);
        add(namaLabel);

        namaField = new JTextField();
        namaField.setBounds(80, 60, 150, 25);
        add(namaField);

        JLabel kelasLabel = new JLabel("Kelas:");
        kelasLabel.setBounds(20, 100, 50, 25);
        add(kelasLabel);

        kelasField = new JTextField();
        kelasField.setBounds(80, 100, 150, 25);
        add(kelasField);

        krsCheck = new JCheckBox("KRS Aktif");
        krsCheck.setBounds(80, 140, 100, 25);
        add(krsCheck);

        simpanBtn = new JButton("Simpan");
        simpanBtn.setBounds(80, 180, 100, 30);
        add(simpanBtn);

        editBtn = new JButton("Edit");
        editBtn.setBounds(190, 180, 100, 30);
        add(editBtn);

        kembaliBtn = new JButton("Kembali");
        kembaliBtn.setBounds(300, 180, 100, 30);
        add(kembaliBtn);

        JLabel searchLabel = new JLabel("Cari Nama:");
        searchLabel.setBounds(400, 20, 80, 25);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(480, 20, 150, 25);
        add(searchField);

        JButton searchBtn = new JButton("Cari");
        searchBtn.setBounds(480, 60, 150, 30);
        add(searchBtn);

        model = new DefaultTableModel(new String[]{"NIM", "Nama", "Kelas", "KRS"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 230, 610, 180);
        add(scrollPane);

        // Event listeners
        simpanBtn.addActionListener(e -> simpanData());
        editBtn.addActionListener(e -> editData());
        kembaliBtn.addActionListener(e -> dispose()); // Tutup window saat klik kembali
        searchBtn.addActionListener(e -> cariData());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    nimField.setText(model.getValueAt(row, 0).toString());
                    namaField.setText(model.getValueAt(row, 1).toString());
                    kelasField.setText(model.getValueAt(row, 2).toString());
                    krsCheck.setSelected(Boolean.parseBoolean(model.getValueAt(row, 3).toString()));
                    nimField.setEditable(false); // NIM tidak bisa diubah saat edit
                }
            }
        });

        tampilData();

        setLocationRelativeTo(null);
    }

    private void simpanData() {
        if (nimField.getText().isEmpty() || namaField.getText().isEmpty() || kelasField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua data!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO mahasiswa(nim, nama, kelas, is_krs) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nimField.getText());
            stmt.setString(2, namaField.getText());
            stmt.setString(3, kelasField.getText());
            stmt.setBoolean(4, krsCheck.isSelected());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
            resetForm();
            tampilData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void editData() {
        if (nimField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit dari tabel!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE mahasiswa SET nama = ?, kelas = ?, is_krs = ? WHERE nim = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, namaField.getText());
            stmt.setString(2, kelasField.getText());
            stmt.setBoolean(3, krsCheck.isSelected());
            stmt.setString(4, nimField.getText());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil diupdate.");
                resetForm();
                tampilData();
                nimField.setEditable(true);
            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void cariData() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM mahasiswa WHERE nama LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + searchField.getText() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("kelas"),
                        rs.getBoolean("is_krs")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat pencarian: " + e.getMessage());
        }
    }

    private void tampilData() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM mahasiswa");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("kelas"),
                        rs.getBoolean("is_krs")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat mengambil data: " + e.getMessage());
        }
    }

    private void resetForm() {
        nimField.setText("");
        namaField.setText("");
        kelasField.setText("");
        krsCheck.setSelected(false);
        nimField.setEditable(true);
    }
}
