
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
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
public class Jadwal extends javax.swing.JFrame {
     private Connection conn;
    /**
     * Creates new form Jadwal
     */public Jadwal() {
        initComponents();
        customizeSpinner();
        koneksi();
        loadTabel();
        tabelJadwal.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    }
    // Method untuk mengatur Spinner Waktu
        private void customizeSpinner() {
            Waktu.setModel(new SpinnerDateModel()); // Atur model default untuk tipe Date
            Waktu.setEditor(new JSpinner.DateEditor(Waktu, "HH:mm")); // Hanya tampilkan waktu (jam:menit)
        }

        // Method untuk koneksi ke database
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

        // Method untuk memuat data ke tabel
        private void loadTabel() {
            // Inisialisasi model tabel dengan kolom ID disertakan
            DefaultTableModel model = new DefaultTableModel(
                new Object[][]{}, // Data awal kosong
                new String[]{"ID", "Nomor Penerbangan", "Asal", "Tujuan", "Tanggal Berangkat", "Waktu Berangkat"} // Header tabel
            );
            tabelJadwal.setModel(model); // Set model ke JTable

            // Query untuk mengambil data jadwal
            String query = "SELECT ID_Jadwal, Nomor_Penerbangan, Asal, Tujuan, Tanggal_Berangkat, Waktu_Berangkat FROM Jadwal";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    // Tambahkan data ke model tabel
                    model.addRow(new Object[]{
                        rs.getInt("ID_Jadwal"),           // ID jadwal
                        rs.getString("Nomor_Penerbangan"),// Nomor penerbangan
                        rs.getString("Asal"),             // Asal
                        rs.getString("Tujuan"),           // Tujuan
                        rs.getDate("Tanggal_Berangkat"),  // Tanggal berangkat
                        rs.getTime("Waktu_Berangkat")     // Waktu berangkat
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
            }

            // Sembunyikan kolom ID di JTable
            tabelJadwal.getColumnModel().getColumn(0).setMinWidth(0);
            tabelJadwal.getColumnModel().getColumn(0).setMaxWidth(0);
            tabelJadwal.getColumnModel().getColumn(0).setWidth(0);
        }

        // Method untuk mencari data
        private void cariData() {
            String keyword = txtCari.getText().trim(); // Ambil teks dari field pencarian
            DefaultTableModel model = (DefaultTableModel) tabelJadwal.getModel();
            model.setRowCount(0); // Kosongkan tabel sebelum menampilkan hasil pencarian

            // SQL query untuk mencari berdasarkan Nomor Penerbangan, Asal, atau Tujuan
            String sql = "SELECT * FROM Jadwal WHERE Nomor_Penerbangan LIKE ? OR Asal LIKE ? OR Tujuan LIKE ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
                pstmt.setString(3, "%" + keyword + "%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("ID_Jadwal"),
                            rs.getString("Nomor_Penerbangan"),
                            rs.getString("Asal"),
                            rs.getString("Tujuan"),
                            rs.getDate("Tanggal_Berangkat"),
                            rs.getTime("Waktu_Berangkat")
                        });
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
            }
        }

        // Method untuk menyimpan data
        private void simpanData() {
            String nomor = txtNo.getText().trim();
            String asal = txtAsal.getText().trim();
            String tujuan = txtTujuan.getText().trim();
            java.util.Date tanggal = DateBerangkat.getDate();
            java.util.Date waktu = (java.util.Date) Waktu.getValue();

            if (nomor.isEmpty() || asal.isEmpty() || tujuan.isEmpty() || tanggal == null || waktu == null) {
                JOptionPane.showMessageDialog(this, "Semua field wajib diisi.");
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO Jadwal (Nomor_Penerbangan, Asal, Tujuan, Tanggal_Berangkat, Waktu_Berangkat) VALUES (?, ?, ?, ?, ?)")) {
                pstmt.setString(1, nomor);
                pstmt.setString(2, asal);
                pstmt.setString(3, tujuan);
                pstmt.setDate(4, new java.sql.Date(tanggal.getTime()));
                pstmt.setTime(5, new java.sql.Time(waktu.getTime()));
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
                loadTabel();
                txtNo.setText("");
                txtAsal.setText("");
                txtTujuan.setText("");
                DateBerangkat.setDate(null);
                Waktu.setValue(new java.util.Date());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
            }
        }

        // Method untuk mengubah data
        private void ubahData() {
            int selectedRow = tabelJadwal.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih jadwal yang ingin diubah.");
                return;
            }

            String nomor = txtNo.getText().trim();
            String asal = txtAsal.getText().trim();
            String tujuan = txtTujuan.getText().trim();
            java.util.Date tanggal = DateBerangkat.getDate();
            java.util.Date waktu = (java.util.Date) Waktu.getValue();
            int idJadwal = (int) tabelJadwal.getValueAt(selectedRow, 0);

            if (nomor.isEmpty() || asal.isEmpty() || tujuan.isEmpty() || tanggal == null || waktu == null) {
                JOptionPane.showMessageDialog(this, "Semua field wajib diisi.");
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE Jadwal SET Nomor_Penerbangan = ?, Asal = ?, Tujuan = ?, Tanggal_Berangkat = ?, Waktu_Berangkat = ? WHERE ID_Jadwal = ?")) {
                pstmt.setString(1, nomor);
                pstmt.setString(2, asal);
                pstmt.setString(3, tujuan);
                pstmt.setDate(4, new java.sql.Date(tanggal.getTime()));
                pstmt.setTime(5, new java.sql.Time(waktu.getTime()));
                pstmt.setInt(6, idJadwal);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
                loadTabel();
                txtNo.setText("");
                txtAsal.setText("");
                txtTujuan.setText("");
                DateBerangkat.setDate(null);
                Waktu.setValue(new java.util.Date());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
            }
        }

        // Method untuk menghapus data
        private void hapusData() {
            int selectedRow = tabelJadwal.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih jadwal yang ingin dihapus.");
                return;
            }

            int idJadwal = (int) tabelJadwal.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Apakah Anda yakin ingin menghapus data ini?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Jadwal WHERE ID_Jadwal = ?")) {
                    pstmt.setInt(1, idJadwal);
                    pstmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                    loadTabel();
                   
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
                }
            }
            txtNo.setText("");
            txtAsal.setText("");
            txtTujuan.setText("");
            DateBerangkat.setDate(null);
            Waktu.setValue(new java.util.Date());
            txtCari.setText("");
            tabelJadwal.clearSelection();
        }

        // Method untuk membatalkan input
        private void batal() {
            txtNo.setText("");
            txtAsal.setText("");
            txtTujuan.setText("");
            DateBerangkat.setDate(null);
            Waktu.setValue(new java.util.Date());
            txtCari.setText("");
            tabelJadwal.clearSelection();
            loadTabel();
        }
        private void cetak(){
            try {
                    String reportPath = "src/Report/ReportJadwal.jasper"; // Lokasi file laporan Jasper
                    Connection conn = connectDB.getConnection(); // Metode untuk mendapatkan koneksi database

                    HashMap<String, Object> parameters = new HashMap<>(); // Membuat parameter untuk laporan

                    JasperPrint print = JasperFillManager.fillReport(reportPath, parameters, conn); // Mengisi laporan Jasper dengan data
                    JasperViewer viewer = new JasperViewer(print, false); // Membuat viewer untuk menampilkan laporan
                    viewer.setVisible(true); // Menampilkan viewer laporan
                    } catch (Exception e)    {
                        JOptionPane.showMessageDialog(this, "Kesalahan saat menampilkan laporan : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
             }

// Event untuk klik tabel
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
        txtTujuan = new javax.swing.JTextField();
        txtAsal = new javax.swing.JTextField();
        txtNo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelJadwal = new javax.swing.JTable();
        btnBatal = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        DateBerangkat = new com.toedter.calendar.JDateChooser();
        Waktu = new javax.swing.JSpinner();
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
        jPanel2.add(btnKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 480, 120, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel2.setText("Tujuan");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 120, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel3.setText("No Penerbangan");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 150, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel4.setText("Asal");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 110, -1));

        txtTujuan.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        txtTujuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTujuanActionPerformed(evt);
            }
        });
        jPanel2.add(txtTujuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 360, -1));

        txtAsal.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtAsal, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 60, 360, -1));

        txtNo.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 360, -1));

        jScrollPane1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        tabelJadwal.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        tabelJadwal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Penerbangan", "Asal", "Tujuan", "Tanggal Berangkat", "Waktu Berangkat"
            }
        ));
        tabelJadwal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelJadwalMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelJadwal);

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
        jLabel5.setText("Waktu Berangkat");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 140, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel6.setText("Tanggal Berangkat");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 150, -1));

        DateBerangkat.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(DateBerangkat, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, 360, 30));

        Waktu.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        Waktu.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1735608047026L), null, null, java.util.Calendar.AM_PM));
        jPanel2.add(Waktu, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 180, -1, -1));

        jPanel1.setBackground(new java.awt.Color(51, 153, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Jadwal Penerbangan");
        jPanel1.add(jLabel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
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

    private void txtTujuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTujuanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTujuanActionPerformed

    private void tabelJadwalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelJadwalMouseClicked
             int selectedRow = tabelJadwal.getSelectedRow();
            if (selectedRow != -1) {
                String id = tabelJadwal.getValueAt(selectedRow, 0).toString();
                String nomor = tabelJadwal.getValueAt(selectedRow, 1).toString();
                String asal = tabelJadwal.getValueAt(selectedRow, 2).toString();
                String tujuan = tabelJadwal.getValueAt(selectedRow, 3).toString();
                java.sql.Date tanggal = (java.sql.Date) tabelJadwal.getValueAt(selectedRow, 4);
                java.sql.Time waktu = (java.sql.Time) tabelJadwal.getValueAt(selectedRow, 5);

                txtNo.setText(nomor);
                txtAsal.setText(asal);
                txtTujuan.setText(tujuan);
                DateBerangkat.setDate(tanggal);
                Waktu.setValue(waktu);
            }        // TODO add your handling code here:
    }//GEN-LAST:event_tabelJadwalMouseClicked

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
            java.util.logging.Logger.getLogger(Jadwal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Jadwal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Jadwal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Jadwal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Jadwal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DateBerangkat;
    private javax.swing.JSpinner Waktu;
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelJadwal;
    private javax.swing.JTextField txtAsal;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtNo;
    private javax.swing.JTextField txtTujuan;
    // End of variables declaration//GEN-END:variables
}
