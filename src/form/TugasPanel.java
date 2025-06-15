package form;

import Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class TugasPanel extends JFrame {
    private JTextField namaTugasField, mataKuliahField, dosenField, searchField;
    private JTable table;
    private DefaultTableModel model;
    private JButton simpanBtn, editBtn, hapusBtn, cariBtn, kembaliBtn;
    private int selectedId = -1;

    public TugasPanel() {
        setTitle("Input Tugas");
        setSize(700, 500); // Ditambah tinggi agar cukup tombol kembali
        setLayout(null);

        JLabel namaLabel = new JLabel("Nama Tugas:");
        namaLabel.setBounds(20, 20, 100, 25);
        add(namaLabel);

        namaTugasField = new JTextField();
        namaTugasField.setBounds(130, 20, 200, 25);
        add(namaTugasField);

        JLabel mkLabel = new JLabel("Mata Kuliah:");
        mkLabel.setBounds(20, 60, 100, 25);
        add(mkLabel);

        mataKuliahField = new JTextField();
        mataKuliahField.setBounds(130, 60, 200, 25);
        add(mataKuliahField);

        JLabel dosenLabel = new JLabel("Dosen Pengampu:");
        dosenLabel.setBounds(20, 100, 120, 25);
        add(dosenLabel);

        dosenField = new JTextField();
        dosenField.setBounds(130, 100, 200, 25);
        add(dosenField);

        simpanBtn = new JButton("Simpan");
        simpanBtn.setBounds(130, 140, 90, 30);
        add(simpanBtn);

        editBtn = new JButton("Edit");
        editBtn.setBounds(230, 140, 90, 30);
        add(editBtn);

        hapusBtn = new JButton("Hapus");
        hapusBtn.setBounds(330, 140, 90, 30);
        add(hapusBtn);

        JLabel searchLabel = new JLabel("Cari Tugas:");
        searchLabel.setBounds(360, 20, 100, 25);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(440, 20, 150, 25);
        add(searchField);

        cariBtn = new JButton("Cari");
        cariBtn.setBounds(600, 20, 60, 25);
        add(cariBtn);

        model = new DefaultTableModel(new String[]{"ID", "Nama Tugas", "Mata Kuliah", "Dosen"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 190, 640, 200);
        add(scrollPane);

        kembaliBtn = new JButton("Kembali");
        kembaliBtn.setBounds(20, 410, 90, 30); // Tombol kembali
        add(kembaliBtn);

        simpanBtn.addActionListener(e -> simpanTugas());
        editBtn.addActionListener(e -> editTugas());
        hapusBtn.addActionListener(e -> hapusTugas());
        cariBtn.addActionListener(e -> cariTugas());
        kembaliBtn.addActionListener(e -> dispose()); // Menutup window saat tombol kembali diklik

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedId = (int) model.getValueAt(row, 0);
                    namaTugasField.setText(model.getValueAt(row, 1).toString());
                    mataKuliahField.setText(model.getValueAt(row, 2).toString());
                    dosenField.setText(model.getValueAt(row, 3).toString());
                }
            }
        });

        tampilData();
        setLocationRelativeTo(null);
    }

    private void simpanTugas() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO tugas(nama_tugas, mata_kuliah, dosen) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, namaTugasField.getText());
            stmt.setString(2, mataKuliahField.getText());
            stmt.setString(3, dosenField.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Tugas berhasil disimpan.");
            tampilData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan tugas: " + e.getMessage());
        }
    }

    private void editTugas() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang akan diedit.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE tugas SET nama_tugas = ?, mata_kuliah = ?, dosen = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, namaTugasField.getText());
            stmt.setString(2, mataKuliahField.getText());
            stmt.setString(3, dosenField.getText());
            stmt.setInt(4, selectedId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Tugas berhasil diperbarui.");
            tampilData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui tugas: " + e.getMessage());
        }
    }

    private void hapusTugas() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang akan dihapus.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus tugas ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM tugas WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, selectedId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Tugas berhasil dihapus.");
                tampilData();
                resetForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus tugas: " + e.getMessage());
            }
        }
    }

    private void cariTugas() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM tugas WHERE nama_tugas LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + searchField.getText() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama_tugas"),
                        rs.getString("mata_kuliah"),
                        rs.getString("dosen")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari tugas: " + e.getMessage());
        }
    }

    private void tampilData() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tugas");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama_tugas"),
                        rs.getString("mata_kuliah"),
                        rs.getString("dosen")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + e.getMessage());
        }
    }

    private void resetForm() {
        namaTugasField.setText("");
        mataKuliahField.setText("");
        dosenField.setText("");
        searchField.setText("");
        selectedId = -1;
    }
}
