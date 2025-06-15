package form;

import Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class RekapPanel extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public RekapPanel() {
        setTitle("Rekap Kehadiran dan Tugas");
        setSize(800, 400); // Ukuran diperbesar untuk tambahan kolom Nama
        setLayout(null);

        // Tambahkan kolom "Nama"
        model = new DefaultTableModel(new String[]{"NIM", "Nama", "% Kehadiran", "% Tugas"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 20, 750, 300);
        add(scrollPane);

        JButton muatBtn = new JButton("Muat Rekap");
        muatBtn.setBounds(20, 330, 150, 30);
        add(muatBtn);

        muatBtn.addActionListener(e -> muatRekap());

        setLocationRelativeTo(null);
    }

    private void muatRekap() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Ambil daftar mahasiswa lengkap dengan nama
            String sql = "SELECT nim, nama FROM mahasiswa";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nim = rs.getString("nim");
                String nama = rs.getString("nama");
                double persenHadir = hitungPersenKehadiran(nim, conn);
                double persenTugas = hitungPersenTugas(nim, conn);
                model.addRow(new Object[]{nim, nama, persenHadir + "%", persenTugas + "%"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double hitungPersenKehadiran(String nim, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM kehadiran WHERE nim = ? AND status = 'Hadir'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, nim);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int hadir = rs.getInt(1);
            return Math.round((hadir / 16.0) * 10000.0) / 100.0;
        }
        return 0;
    }

    private double hitungPersenTugas(String nim, Connection conn) throws SQLException {
        String sqlTotalTugas = "SELECT COUNT(*) FROM tugas";
        Statement stmt = conn.createStatement();
        ResultSet rsTotal = stmt.executeQuery(sqlTotalTugas);
        int totalTugas = rsTotal.next() ? rsTotal.getInt(1) : 0;

        if (totalTugas == 0) return 0;

        String sqlSelesai = "SELECT COUNT(*) FROM status_tugas WHERE nim = ? AND status = 1";
        PreparedStatement stmtSelesai = conn.prepareStatement(sqlSelesai);
        stmtSelesai.setString(1, nim);
        ResultSet rsSelesai = stmtSelesai.executeQuery();
        int tugasSelesai = rsSelesai.next() ? rsSelesai.getInt(1) : 0;

        return Math.round((tugasSelesai / (double) totalTugas) * 10000.0) / 100.0;
    }
}
