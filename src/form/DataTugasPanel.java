package form;

import Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DataTugasPanel extends JFrame {
    private JComboBox<String> nimBox;
    private JComboBox<String> tugasBox;
    private JCheckBox statusCheck;
    private JButton simpanBtn, kembaliBtn, editBtn, hapusBtn, cariBtn;
    private JTextField cariField;
    private JTable table;
    private DefaultTableModel model;

    public DataTugasPanel() {
        setTitle("Pengerjaan Tugas Mahasiswa");
        setSize(750, 500);
        setLayout(null);

        JLabel nimLabel = new JLabel("NIM:");
        nimLabel.setBounds(20, 20, 50, 25);
        add(nimLabel);

        nimBox = new JComboBox<>();
        nimBox.setBounds(80, 20, 150, 25);
        add(nimBox);

        JLabel tugasLabel = new JLabel("ID Tugas:");
        tugasLabel.setBounds(20, 60, 100, 25);
        add(tugasLabel);

        tugasBox = new JComboBox<>();
        tugasBox.setBounds(80, 60, 150, 25);
        add(tugasBox);

        statusCheck = new JCheckBox("Sudah Mengerjakan");
        statusCheck.setBounds(80, 100, 200, 25);
        add(statusCheck);

        simpanBtn = new JButton("Simpan");
        simpanBtn.setBounds(80, 140, 100, 30);
        add(simpanBtn);

        editBtn = new JButton("Edit");
        editBtn.setBounds(190, 140, 100, 30);
        add(editBtn);

        hapusBtn = new JButton("Hapus");
        hapusBtn.setBounds(80, 180, 100, 30);
        add(hapusBtn);

        kembaliBtn = new JButton("Kembali");
        kembaliBtn.setBounds(190, 180, 100, 30);
        add(kembaliBtn);

        JLabel cariLabel = new JLabel("Cari ID:");
        cariLabel.setBounds(20, 230, 60, 25);
        add(cariLabel);

        cariField = new JTextField();
        cariField.setBounds(80, 230, 100, 25);
        add(cariField);

        cariBtn = new JButton("Cari");
        cariBtn.setBounds(190, 230, 100, 25);
        add(cariBtn);

        model = new DefaultTableModel(new String[]{"NIM", "ID Tugas", "Status"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(310, 20, 400, 400);
        add(scrollPane);

        simpanBtn.addActionListener(e -> {
            simpanData();
            tampilData((String) nimBox.getSelectedItem());
        });

        editBtn.addActionListener(e -> isiFormDariTabel());

        hapusBtn.addActionListener(e -> hapusData());

        cariBtn.addActionListener(e -> cariData());

        kembaliBtn.addActionListener(e -> {
            this.dispose();
            new MainMenu().setVisible(true);
        });

        nimBox.addActionListener(e -> {
            String selectedNim = (String) nimBox.getSelectedItem();
            if (selectedNim != null) {
                tampilData(selectedNim);
            }
        });

        muatTugas();
        loadMahasiswaToComboBox();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void loadMahasiswaToComboBox() {
        nimBox.removeAllItems();
        java.util.List<String> nimList = new java.util.ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nim FROM mahasiswa")) {

            while (rs.next()) {
                nimList.add(rs.getString("nim"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load data mahasiswa: " + e.getMessage());
        }

        for (String nim : nimList) {
            nimBox.addItem(nim);
        }

        if (!nimList.isEmpty()) {
            nimBox.setSelectedIndex(0);
            tampilData(nimList.get(0));
        }
    }

    private void muatTugas() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM tugas")) {

            tugasBox.removeAllItems();
            while (rs.next()) {
                tugasBox.addItem(String.valueOf(rs.getInt("id")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat daftar tugas: " + e.getMessage());
        }
    }

    private void simpanData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "REPLACE INTO status_tugas(nim, id_tugas, status) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nimBox.getSelectedItem().toString());
            stmt.setInt(2, Integer.parseInt(tugasBox.getSelectedItem().toString()));
            stmt.setBoolean(3, statusCheck.isSelected());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
        }
    }

    private void tampilData(String nim) {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM status_tugas WHERE nim = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nim);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("nim"),
                        rs.getInt("id_tugas"),
                        rs.getBoolean("status") ? "Sudah" : "Belum"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + e.getMessage());
        }
    }

    private void isiFormDariTabel() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            nimBox.setSelectedItem(model.getValueAt(row, 0).toString());
            tugasBox.setSelectedItem(model.getValueAt(row, 1).toString());
            statusCheck.setSelected(model.getValueAt(row, 2).toString().equals("Sudah"));
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu untuk diedit.");
        }
    }

    private void hapusData() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String nim = model.getValueAt(row, 0).toString();
            int idTugas = Integer.parseInt(model.getValueAt(row, 1).toString());
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM status_tugas WHERE nim = ? AND id_tugas = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nim);
                stmt.setInt(2, idTugas);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                tampilData(nim);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
        }
    }

    private void cariData() {
        String keyword = cariField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan ID tugas yang ingin dicari.");
            return;
        }

        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM status_tugas WHERE id_tugas = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(keyword));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("nim"),
                        rs.getInt("id_tugas"),
                        rs.getBoolean("status") ? "Sudah" : "Belum"
                });
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari data: " + e.getMessage());
        }
    }
}
