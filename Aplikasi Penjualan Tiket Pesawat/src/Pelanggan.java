import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class Pelanggan extends javax.swing.JFrame {
private Connection conn;
    /**
     * Creates new form Pelanggan
     */
    public Pelanggan() {
        initComponents();
        koneksi();
        loadTabel();
        tabelPelanggan.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
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
        // Inisialisasi model tabel dengan kolom ID disertakan
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, // Data awal kosong
            new String[]{"ID", "Nama Lengkap", "Email", "Nomor Telepon"} // Header tabel, ID akan disembunyikan
        );
        tabelPelanggan.setModel(model); // Set model ke JTable

        // Query untuk mengambil data pelanggan
        String query = "SELECT ID_Pelanggan, Nama_Lengkap, Email, Nomor_Telepon FROM Pelanggan";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Tambahkan data ke model tabel
                model.addRow(new Object[]{
                    rs.getInt("ID_Pelanggan"),   // ID pelanggan, tetap ditambahkan tapi akan disembunyikan
                    rs.getString("Nama_Lengkap"), // Nama pelanggan
                    rs.getString("Email"),        // Email pelanggan
                    rs.getString("Nomor_Telepon") // Nomor telepon pelanggan
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }

        // Sembunyikan kolom ID di JTable
        tabelPelanggan.getColumnModel().getColumn(0).setMinWidth(0);  // Menyembunyikan kolom pertama (ID)
        tabelPelanggan.getColumnModel().getColumn(0).setMaxWidth(0);  // Menyembunyikan kolom pertama (ID)
        tabelPelanggan.getColumnModel().getColumn(0).setWidth(0);     // Menyembunyikan kolom pertama (ID)
    }


    private void cariData() {
        String keyword = txtCari.getText().trim(); // Ambil teks dari field pencarian
        DefaultTableModel model = (DefaultTableModel) tabelPelanggan.getModel();
        model.setRowCount(0); // Kosongkan tabel sebelum menampilkan hasil pencarian

        // SQL query untuk mencari berdasarkan Nama, Email, atau Nomor Telepon
        String sql = "SELECT * FROM Pelanggan WHERE Nama_Lengkap LIKE ? OR Email LIKE ? OR Nomor_Telepon LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("ID_Pelanggan"),
                        rs.getString("Nama_Lengkap"),
                        rs.getString("Email"),
                        rs.getString("Nomor_Telepon")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
        }
    }

    private void simpanData() {
        String nama = txtNama.getText().trim();  
        String email = txtEmail.getText().trim();  
        String tlp = txtTlp.getText().trim();  

        // Validasi input
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap wajib diisi.");
            return;
        }

        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Pelanggan (Nama_Lengkap, Email, Nomor_Telepon) VALUES (?, ?, ?)")) {
            pstmt.setString(1, nama);
            pstmt.setString(2, email);
            pstmt.setString(3, tlp);
            pstmt.executeUpdate();  // Eksekusi query untuk menyimpan data

            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
            loadTabel(); // Memuat ulang data dari database ke tabel GUI

            // Kosongkan field input setelah data disimpan
            txtNama.setText("");
            txtEmail.setText("");
            txtTlp.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
        }
    }

    private void ubahData() {
        int selectedRow = tabelPelanggan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan yang ingin diubah.");
            return;
        }

        String nama = txtNama.getText().trim();
        String email = txtEmail.getText().trim();
        String tlp = txtTlp.getText().trim();
        int idPelanggan = (int) tabelPelanggan.getValueAt(selectedRow, 0); // Ambil ID_Pelanggan dari tabel

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap wajib diisi.");
            return;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE Pelanggan SET Nama_Lengkap = ?, Email = ?, Nomor_Telepon = ? WHERE ID_Pelanggan = ?")) {
            pstmt.setString(1, nama);
            pstmt.setString(2, email);
            pstmt.setString(3, tlp);
            pstmt.setInt(4, idPelanggan);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
            loadTabel();
            txtNama.setText("");
            txtEmail.setText("");
            txtTlp.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
        }
    }

    private void hapusData() {
        int selectedRow = tabelPelanggan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan yang ingin dihapus.");
            return;
        }

        int idPelanggan = (int) tabelPelanggan.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menghapus data ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Pelanggan WHERE ID_Pelanggan = ?")) {
                pstmt.setInt(1, idPelanggan);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                loadTabel();
                
                txtNama.setText("");
                txtEmail.setText("");
                txtTlp.setText("");
                txtCari.setText("");
                tabelPelanggan.clearSelection();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }
    }

    private void batal() {
        txtNama.setText("");
        txtEmail.setText("");
        txtTlp.setText("");
        txtCari.setText("");
        tabelPelanggan.clearSelection();
        loadTabel();
    }
    private void cetak(){
                try {
                    String reportPath = "src/Report/ReportPelanggan.jasper"; // Lokasi file laporan Jasper
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

        jPanel2 = new javax.swing.JPanel();
        btnKembali = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtTlp = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelPelanggan = new javax.swing.JTable();
        btnBatal = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });
        jPanel2.add(btnKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 410, 120, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel2.setText("No Telepon");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 120, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel3.setText("Nama Lengkap");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 150, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel4.setText("Email");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 70, -1));

        txtTlp.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtTlp, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 360, -1));

        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 60, 360, -1));

        txtNama.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 360, -1));

        jScrollPane1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        tabelPelanggan.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        tabelPelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Lengkap", "Email", "No Telepon"
            }
        ));
        tabelPelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelPelangganMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelPelanggan);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, 510, 140));

        btnBatal.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        jPanel2.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 300, 120, 40));

        btnSimpan.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel2.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 300, 120, 40));

        btnCetak.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnCetak.setText("Cetak");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });
        jPanel2.add(btnCetak, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 410, 120, 40));

        btnUbah.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });
        jPanel2.add(btnUbah, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 300, 120, 40));

        btnHapus.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel2.add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 120, 40));

        btnCari.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        jPanel2.add(btnCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 360, 120, 40));

        txtCari.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 360, 380, -1));

        jPanel1.setBackground(new java.awt.Color(51, 153, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Informasi Pelanggan");
        jPanel1.add(jLabel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        new HomePage().setVisible(true);
        dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKembaliActionPerformed

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

    private void tabelPelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelPelangganMouseClicked
        int selectedRow = tabelPelanggan.getSelectedRow(); // Ambil baris yang dipilih di tabel
        if (selectedRow != -1) { // Pastikan ada baris yang dipilih
        // Ambil data dari baris yang dipilih
        String id = tabelPelanggan.getValueAt(selectedRow, 0).toString();       // Ambil ID
        String namaLengkap = tabelPelanggan.getValueAt(selectedRow, 1).toString(); // Ambil Nama Lengkap
        String email = tabelPelanggan.getValueAt(selectedRow, 2).toString();       // Ambil Email
        String nomorTelepon = tabelPelanggan.getValueAt(selectedRow, 3).toString(); // Ambil Nomor Telepon

        // Debugging: Menampilkan data di konsol
        System.out.println("Baris yang dipilih: " + selectedRow);
        System.out.println("ID: " + id);
        System.out.println("Nama Lengkap: " + namaLengkap);
        System.out.println("Email: " + email);
        System.out.println("Nomor Telepon: " + nomorTelepon);

        // Set data yang diambil ke field input
        txtNama.setText(namaLengkap);       // Set Nama Lengkap ke TextField txtNama
        txtEmail.setText(email);            // Set Email ke TextField txtEmail
        txtTlp.setText(nomorTelepon);       // Set Nomor Telepon ke TextField txtTlp
    }        // TODO add your handling code here:
    }//GEN-LAST:event_tabelPelangganMouseClicked

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
            java.util.logging.Logger.getLogger(Pelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Pelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Pelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Pelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Pelanggan().setVisible(true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelPelanggan;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtTlp;
    // End of variables declaration//GEN-END:variables
}
