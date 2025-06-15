package form;

import javax.swing.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Menu Utama");
        setSize(400, 350);
        setLayout(null);

        JButton mahasiswaBtn = new JButton("Mahasiswa");
        JButton kehadiranBtn = new JButton("Kehadiran");
        JButton tugasBtn = new JButton("Tugas");
        JButton dataTugasBtn = new JButton("Data Tugas");
        JButton rekapBtn = new JButton("Rekap Data");

        mahasiswaBtn.setBounds(50, 30, 300, 40);
        kehadiranBtn.setBounds(50, 80, 300, 40);
        tugasBtn.setBounds(50, 130, 300, 40);
        dataTugasBtn.setBounds(50, 180, 300, 40);
        rekapBtn.setBounds(50, 230, 300, 40);

        add(mahasiswaBtn);
        add(kehadiranBtn);
        add(tugasBtn);
        add(dataTugasBtn);
        add(rekapBtn);

        mahasiswaBtn.addActionListener(e -> new MahasiswaPanel().setVisible(true));
        kehadiranBtn.addActionListener(e -> new KehadiranPanel().setVisible(true));
        tugasBtn.addActionListener(e -> new TugasPanel().setVisible(true));
        dataTugasBtn.addActionListener(e -> new DataTugasPanel().setVisible(true));
        rekapBtn.addActionListener(e -> new RekapPanel().setVisible(true));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args){
        // Tampilkan LoginForm lebih dulu
        new LoginForm().setVisible(true);
    }
}
