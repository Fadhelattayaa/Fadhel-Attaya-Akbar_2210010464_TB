import java.awt.Font;
import java.awt.List;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author ass
 */
public class Tiket extends javax.swing.JFrame {
    private Connection conn;
    /**
     * Creates new form Tiket
     */
    public Tiket() {
        initComponents();
        koneksi();
        loadTabel();
        populatePelangganComboBox();
        populateJadwalComboBox();
        tabelTiket.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    }
    private void koneksi() {
        try {
            conn = connectDB.getConnection();
            if (conn != null) {
                System.out.println("Koneksi ke database berhasil.");
            } else {
                JOptionPane.showMessageDialog(this, "Koneksi ke database gagal.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi gagal: " + e.getMessage());
        }
    }

    private void loadTabel() {
    DefaultTableModel model = new DefaultTableModel(
        new Object[][]{}, // Data awal kosong
        new String[]{"ID", "Nama Penumpang", "Nomor Telepon", "Nomor Penerbangan", "Asal", "Tujuan", 
                     "Tanggal Berangkat", "Waktu Berangkat", "Nomor Kursi", "Kelas Tiket", "Harga"} // Header tabel
    );
    tabelTiket.setModel(model); // Set model ke JTable

    // Query untuk mengambil data tiket
    String query = "SELECT tiket.ID_Tiket, pelanggan.Nama_Lengkap AS Nama_Penumpang, pelanggan.Nomor_Telepon, jadwal.Nomor_Penerbangan, " +
               "jadwal.Asal, jadwal.Tujuan, jadwal.Tanggal_Berangkat, jadwal.Waktu_Berangkat, tiket.Nomor_Kursi, " +
               "tiket.Kelas_Tiket, tiket.Harga " +
               "FROM tiket " +
               "JOIN pelanggan ON tiket.ID_Pelanggan = pelanggan.ID_Pelanggan " +
               "JOIN jadwal ON tiket.ID_Jadwal = jadwal.ID_Jadwal";

    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            // Tambahkan data ke model tabel
            model.addRow(new Object[]{
                rs.getInt("ID_Tiket"),              // ID Tiket
                rs.getString("Nama_Penumpang"),               // Nama Penumpang
                rs.getString("Nomor_Telepon"),            // Nomor Telepon
                rs.getString("Nomor_Penerbangan"),  // Nomor Penerbangan
                rs.getString("Asal"),              // Asal
                rs.getString("Tujuan"),            // Tujuan
                rs.getDate("Tanggal_Berangkat"),    // Tanggal Berangkat
                rs.getTime("Waktu_Berangkat"),      // Waktu Berangkat
                rs.getString("Nomor_Kursi"),        // Nomor Kursi
                rs.getString("Kelas_Tiket"),        // Kelas Tiket
                rs.getBigDecimal("Harga")          // Harga
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
    }

    // Sembunyikan kolom ID jika perlu
    tabelTiket.getColumnModel().getColumn(0).setMinWidth(0);
    tabelTiket.getColumnModel().getColumn(0).setMaxWidth(0);
    tabelTiket.getColumnModel().getColumn(0).setWidth(0);
}


 private final java.util.List<Integer> pelangganIds = new java.util.ArrayList<Integer>();

  
private void populatePelangganComboBox() {
    cbPelanggan.removeAllItems();
    pelangganIds.clear(); // Kosongkan list sebelumnya

    cbPelanggan.addItem("-- Pilih Pelanggan --");
    pelangganIds.add(0); // Tambahkan placeholder

    String queryPelanggan = "SELECT ID_Pelanggan, Nama_Lengkap FROM Pelanggan";
    try (Connection conn = connectDB.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(queryPelanggan)) {

        while (rs.next()) {
            int idPelanggan = rs.getInt("ID_Pelanggan");
            String namaPelanggan = rs.getString("Nama_Lengkap");

            pelangganIds.add(idPelanggan);   // Simpan ID di List
            cbPelanggan.addItem(namaPelanggan); // Tambahkan nama ke ComboBox
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}


 private final java.util.List<Integer> jadwalIds = new java.util.ArrayList<Integer>();

private void populateJadwalComboBox() {
    cbJadwal.removeAllItems(); // Hapus item sebelumnya
    jadwalIds.clear();         // Kosongkan list sebelumnya

    cbJadwal.addItem("-- Pilih Penerbangan --"); // Tambahkan placeholder
    jadwalIds.add(0); // Tambahkan placeholder untuk indeks 0

    String queryJadwal = "SELECT ID_Jadwal, Nomor_Penerbangan FROM Jadwal";
    try (Connection conn = connectDB.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(queryJadwal)) {

        while (rs.next()) {
            int idJadwal = rs.getInt("ID_Jadwal");
            String nomorPenerbangan = rs.getString("Nomor_Penerbangan");

            jadwalIds.add(idJadwal);           // Simpan ID ke List
            cbJadwal.addItem(nomorPenerbangan); // Tambahkan nomor penerbangan ke ComboBox
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}



   private void cariData() {
    String keyword = txtCari.getText().trim();
    DefaultTableModel model = new DefaultTableModel(
        new Object[][]{}, // Data awal kosong
        new String[]{"ID", "Nama Penumpang", "Nomor Telepon", "Nomor Penerbangan", "Asal", "Tujuan", 
                     "Tanggal Berangkat", "Waktu Berangkat", "Nomor Kursi", "Kelas Tiket", "Harga"} // Header tabel
    );
    tabelTiket.setModel(model); // Set model baru ke JTable

    String sql = "SELECT tiket.ID_Tiket, pelanggan.Nama_Lengkap AS Nama_Penumpang, pelanggan.Nomor_Telepon, " +
                 "jadwal.Nomor_Penerbangan, jadwal.Asal, jadwal.Tujuan, jadwal.Tanggal_Berangkat, " +
                 "jadwal.Waktu_Berangkat, tiket.Nomor_Kursi, tiket.Kelas_Tiket, tiket.Harga " +
                 "FROM tiket " +
                 "JOIN pelanggan ON tiket.ID_Pelanggan = pelanggan.ID_Pelanggan " +
                 "JOIN jadwal ON tiket.ID_Jadwal = jadwal.ID_Jadwal " +
                 "WHERE pelanggan.Nama_Lengkap LIKE ? " +
                 "OR jadwal.Nomor_Penerbangan LIKE ? " +
                 "OR tiket.Nomor_Kursi LIKE ? " +
                 "OR tiket.Kelas_Tiket LIKE ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, "%" + keyword + "%");
        pstmt.setString(2, "%" + keyword + "%");
        pstmt.setString(3, "%" + keyword + "%");
        pstmt.setString(4, "%" + keyword + "%");

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID_Tiket"),              // ID Tiket
                    rs.getString("Nama_Penumpang"),    // Nama Penumpang
                    rs.getString("Nomor_Telepon"),     // Nomor Telepon
                    rs.getString("Nomor_Penerbangan"), // Nomor Penerbangan
                    rs.getString("Asal"),              // Asal
                    rs.getString("Tujuan"),            // Tujuan
                    rs.getDate("Tanggal_Berangkat"),   // Tanggal Berangkat
                    rs.getTime("Waktu_Berangkat"),     // Waktu Berangkat
                    rs.getString("Nomor_Kursi"),       // Nomor Kursi
                    rs.getString("Kelas_Tiket"),       // Kelas Tiket
                    rs.getBigDecimal("Harga")          // Harga
                });
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
    }

    // Sembunyikan kolom ID_Tiket
    tabelTiket.getColumnModel().getColumn(0).setMinWidth(0);
    tabelTiket.getColumnModel().getColumn(0).setMaxWidth(0);
    tabelTiket.getColumnModel().getColumn(0).setWidth(0);
}


   private void simpanData() {
    int selectedPelangganIndex = cbPelanggan.getSelectedIndex();
    int selectedJadwalIndex = cbJadwal.getSelectedIndex();
    String nomorKursi = txtKursi.getText().trim();
    String kelasTiket = cbKelas.getSelectedItem().toString();
    String harga = txtHarga.getText().trim();

    if (selectedPelangganIndex == 0 || selectedJadwalIndex == 0 || 
        nomorKursi.isEmpty() || kelasTiket.isEmpty() || harga.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
        return;
    }

    int idPelanggan = pelangganIds.get(selectedPelangganIndex); // Ambil ID_Pelanggan
    int idJadwal = jadwalIds.get(selectedJadwalIndex);          // Ambil ID_Jadwal

    try (PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO tiket (ID_Pelanggan, ID_Jadwal, Nomor_Kursi, Kelas_Tiket, Harga) VALUES (?, ?, ?, ?, ?)")) {
        pstmt.setInt(1, idPelanggan);
        pstmt.setInt(2, idJadwal);
        pstmt.setString(3, nomorKursi);
        pstmt.setString(4, kelasTiket);
        pstmt.setBigDecimal(5, new BigDecimal(harga));

        pstmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
        loadTabel(); // Muat ulang data tiket

        cbPelanggan.setSelectedIndex(0);
        cbJadwal.setSelectedIndex(0);
        txtKursi.setText("");
        cbKelas.setSelectedIndex(0);
        txtHarga.setText("");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
    }
}

    private void ubahData() {
    int selectedRow = tabelTiket.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah.");
        return;
    }

    // Ambil data dari tabel berdasarkan baris yang dipilih
    String idTiket = tabelTiket.getValueAt(selectedRow, 0).toString();

    // Validasi input
    int selectedPelangganIndex = cbPelanggan.getSelectedIndex();
    int selectedJadwalIndex = cbJadwal.getSelectedIndex();
    String nomorKursi = txtKursi.getText().trim();
    String kelasTiket = cbKelas.getSelectedItem().toString();
    String harga = txtHarga.getText().trim();

    if (selectedPelangganIndex == 0 || selectedJadwalIndex == 0 || 
        nomorKursi.isEmpty() || kelasTiket.isEmpty() || harga.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
        return;
    }

    // Ambil ID dari pelanggan dan jadwal berdasarkan pilihan di ComboBox
    int idPelanggan = pelangganIds.get(selectedPelangganIndex); // Ambil ID_Pelanggan
    int idJadwal = jadwalIds.get(selectedJadwalIndex);          // Ambil ID_Jadwal

    try (PreparedStatement pstmt = conn.prepareStatement(
            "UPDATE tiket SET ID_Pelanggan = ?, ID_Jadwal = ?, Nomor_Kursi = ?, Kelas_Tiket = ?, Harga = ? WHERE ID_Tiket = ?")) {
        pstmt.setInt(1, idPelanggan);              // ID Pelanggan
        pstmt.setInt(2, idJadwal);                 // ID Jadwal
        pstmt.setString(3, nomorKursi);            // Nomor Kursi
        pstmt.setString(4, kelasTiket);            // Kelas Tiket
        pstmt.setBigDecimal(5, new BigDecimal(harga)); // Harga
        pstmt.setInt(6, Integer.parseInt(idTiket)); // ID Tiket (primary key)

        pstmt.executeUpdate(); // Jalankan query

        JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
        loadTabel(); // Muat ulang data tabel setelah perubahan

        // Reset input form
        cbPelanggan.setSelectedIndex(0);
        cbJadwal.setSelectedIndex(0);
        txtKursi.setText("");
        cbKelas.setSelectedIndex(0);
        txtHarga.setText("");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
    }
}


    private void hapusData() {
        int selectedRow = tabelTiket.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
            return;
        }

        String selectedIdTiket = tabelTiket.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Apakah Anda yakin ingin menghapus data ini?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION
                
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tiket WHERE ID_Tiket = ?")) {
                pstmt.setInt(1, Integer.parseInt(selectedIdTiket));
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                loadTabel();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }
                cbPelanggan.setSelectedIndex(0);
                cbJadwal.setSelectedIndex(0);
                txtKursi.setText("");
                cbKelas.setSelectedIndex(0);
                txtHarga.setText("");
                tabelTiket.clearSelection();
    }

    private void batal() {
        cbPelanggan.setSelectedIndex(0);
        cbJadwal.setSelectedIndex(0);
        txtKursi.setText("");
        cbKelas.setSelectedIndex(0);
        txtHarga.setText("");
        txtCari.setText("");
        tabelTiket.clearSelection();
        loadTabel();
    }
    private void cetak(){
            try {
                    String reportPath = "src/Report/ReportTiket.jasper"; // Lokasi file laporan Jasper
                    Connection conn = connectDB.getConnection(); // Metode untuk mendapatkan koneksi database

                    HashMap<String, Object> parameters = new HashMap<>(); // Membuat parameter untuk laporan

                    JasperPrint print = JasperFillManager.fillReport(reportPath, parameters, conn); // Mengisi laporan Jasper dengan data
                    JasperViewer viewer = new JasperViewer(print, false); // Membuat viewer untuk menampilkan laporan
                    viewer.setVisible(true); // Menampilkan viewer laporan
                    } catch (Exception e)    {
                        JOptionPane.showMessageDialog(this, "Kesalahan saat menampilkan laporan : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
             }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnKembali = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        txtKursi = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelTiket = new javax.swing.JTable();
        btnBatal = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cbKelas = new javax.swing.JComboBox<>();
        cbPelanggan = new javax.swing.JComboBox<>();
        cbJadwal = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 153, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Informasi Tiket");
        jPanel1.add(jLabel1);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });
        jPanel2.add(btnKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 480, 120, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel2.setText("Nomor Kursi");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 120, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel3.setText("Nama Penumpang");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 150, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel4.setText("No Penerbangan");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 140, -1));

        txtHarga.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        txtHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaActionPerformed(evt);
            }
        });
        jPanel2.add(txtHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 180, 360, -1));

        txtKursi.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtKursi, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 360, -1));

        jScrollPane1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        tabelTiket.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        tabelTiket.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Penumpang", "No Telepon", "Asal", "Tujuan", "Tanggal Berangkat", "Waktu Berangkat", "Nomor Kursi", "Kelas", "Harga"
            }
        ));
        tabelTiket.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelTiketMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelTiket);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 220, 510, 140));

        btnBatal.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        jPanel2.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 370, 120, 40));

        btnSimpan.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel2.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 370, 120, 40));

        btnCetak.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnCetak.setText("Cetak");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });
        jPanel2.add(btnCetak, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 480, 120, 40));

        btnUbah.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });
        jPanel2.add(btnUbah, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 370, 120, 40));

        btnHapus.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel2.add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 370, 120, 40));

        btnCari.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        jPanel2.add(btnCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 430, 120, 40));

        txtCari.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 430, 380, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel5.setText("Harga");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 140, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel6.setText("Kelas Tiket");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 150, -1));

        cbKelas.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        cbKelas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih Kelas --", "Ekonomi", "Bisnis", "First Class" }));
        jPanel2.add(cbKelas, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, 360, -1));

        cbPelanggan.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(cbPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 360, -1));

        cbJadwal.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(cbJadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 60, 360, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        new HomePage().setVisible(true);
        dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void txtHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        batal();        // TODO add your handling code here:
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        simpanData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
        cetak();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetakActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        ubahData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
            hapusData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        cariData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariActionPerformed

    private void tabelTiketMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelTiketMouseClicked
       int selectedRow = tabelTiket.getSelectedRow(); // Ambil baris yang dipilih di tabel
    if (selectedRow != -1) { // Pastikan ada baris yang dipilih
    // Ambil data dari baris yang dipilih
    String id = tabelTiket.getValueAt(selectedRow, 0).toString();         // Ambil ID Tiket
    String namaPelanggan = tabelTiket.getValueAt(selectedRow, 1).toString(); // Ambil nama palnggan dari id Pelanggan
    String noPenerbangan = tabelTiket.getValueAt(selectedRow, 3).toString();   // Ambil nomo penerbangan dari id jadwal
    String nomorKursi = tabelTiket.getValueAt(selectedRow, 8).toString(); // Ambil Nomor Kursi
    String kelasTiket = tabelTiket.getValueAt(selectedRow, 9).toString(); // Ambil Kelas Tiket
    String harga = tabelTiket.getValueAt(selectedRow, 10).toString();      // Ambil Harga

    // Debugging: Menampilkan data di konsol
    System.out.println("Baris yang dipilih: " + selectedRow);
    System.out.println("ID Tiket: " + id);
    System.out.println("ID Pelanggan: " + namaPelanggan);
    System.out.println("ID Jadwal: " + noPenerbangan);
    System.out.println("Nomor Kursi: " + nomorKursi);
    System.out.println("Kelas Tiket: " + kelasTiket);
    System.out.println("Harga: " + harga);

    // Set data yang diambil ke field input
    cbPelanggan.setSelectedItem(namaPelanggan);  // Set nama Pelanggan ke ComboBox cbPelanggan
    cbJadwal.setSelectedItem(noPenerbangan);        // Set no penerbangan ke ComboBox cbJadwal
    txtKursi.setText(nomorKursi);              // Set Nomor Kursi ke TextField txtKursi
    cbKelas.setSelectedItem(kelasTiket);       // Set Kelas Tiket ke ComboBox cbKelas
    txtHarga.setText(harga);                   // Set Harga ke TextField txtHarga
}
   // TODO add your handling code here:
    }//GEN-LAST:event_tabelTiketMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Tiket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Tiket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Tiket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tiket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Tiket().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cbJadwal;
    private javax.swing.JComboBox<String> cbKelas;
    private javax.swing.JComboBox<String> cbPelanggan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelTiket;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtKursi;
    // End of variables declaration//GEN-END:variables
}
