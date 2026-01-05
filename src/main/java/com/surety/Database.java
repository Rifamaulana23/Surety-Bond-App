package com.surety;

import java.sql.*;

public class Database {

    private static final String DB_URL =
        "jdbc:sqlite:" + new java.io.File("surety.db").getAbsolutePath();


    // Dipanggil sekali di awal untuk bikin file DB & tabel kalau belum ada
    public static void init() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    String sql = """
                            CREATE TABLE IF NOT EXISTS draft_jaminan (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                principal TEXT,
                                alamat_principal TEXT,
                                obligee TEXT,
                                alamat_obligee TEXT,
                                pekerjaan TEXT,
                                dasar_surat TEXT,
                                tgl_dasar_surat TEXT,
                                jenis_jaminan TEXT,
                                direktur TEXT,
                                jabatan TEXT,
                                tgl_mulai TEXT,
                                tgl_selesai TEXT,
                                tgl_terbit TEXT,
                                mode_nilai TEXT,
                                nilai_kontrak REAL,
                                persen REAL,
                                nilai_jaminan REAL,
                                jangka_hari INTEGER
                            );
                            """;
                    stmt.execute(sql);
                    System.out.println("[DB] Tabel draft_jaminan siap dipakai.");
                    System.out.println("[DB] DB Path: " + new java.io.File("surety.db").getAbsolutePath());

                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Gagal inisialisasi DB: " + e.getMessage());
        }
    }

    
    public static void simpanDraft(
            String principal,
            String alamatPrincipal,
            String obligee,
            String alamatObligee,
            String pekerjaan,
            String dasarSurat,
            String TglDasarSurat,
            String jenisJaminan,
            String direktur,
            String jabatan,
            String tglMulai,
            String tglSelesai,
            String tglTerbit,
            String modeNilai,
            double nilaiKontrak,
            double persen,
            double nilaiJaminan,
            long jangkaHari
    ) {
        String sql = """
                INSERT INTO draft_jaminan (
                    principal, alamat_principal, obligee, alamat_obligee,
                    pekerjaan, dasar_surat, tgl_dasar_surat, jenis_jaminan,
                    direktur, jabatan,
                    tgl_mulai, tgl_selesai, tgl_terbit,
                    mode_nilai, nilai_kontrak, persen, nilai_jaminan, jangka_hari
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;


        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, principal);
            ps.setString(2, alamatPrincipal);
            ps.setString(3, obligee);
            ps.setString(4, alamatObligee);
            ps.setString(5, pekerjaan);
            ps.setString(6, dasarSurat);
            ps.setString(7, TglDasarSurat);     
            ps.setString(8, jenisJaminan);
            ps.setString(9, direktur);
            ps.setString(10, jabatan);
            ps.setString(11, tglMulai);
            ps.setString(12, tglSelesai);
            ps.setString(13, tglTerbit);
            ps.setString(14, modeNilai);
            ps.setDouble(15, nilaiKontrak);
            ps.setDouble(16, persen);
            ps.setDouble(17, nilaiJaminan);
            ps.setLong(18, jangkaHari);        


            ps.executeUpdate();
            System.out.println("[DB] Draft jaminan tersimpan ke database.");
        } catch (SQLException e) {
            System.err.println("[DB] Gagal simpan draft: " + e.getMessage());
        }
    }
    public static String getAllDrafts() {
    StringBuilder sb = new StringBuilder();
    String sql = "SELECT * FROM draft_jaminan ORDER BY id DESC";

    try (Connection conn = DriverManager.getConnection(DB_URL);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            sb.append("ID: ").append(rs.getInt("id")).append("\n");
            sb.append("Principal: ").append(rs.getString("principal")).append("\n");
            sb.append("Obligee: ").append(rs.getString("obligee")).append("\n");
            sb.append("Pekerjaan: ").append(rs.getString("pekerjaan")).append("\n");
            sb.append("Jenis: ").append(rs.getString("jenis_jaminan")).append("\n");
            sb.append("Nilai Jaminan: ").append(rs.getDouble("nilai_jaminan")).append("\n");
            sb.append("Jangka Waktu: ").append(rs.getInt("jangka_hari")).append(" hari\n");
            sb.append("Tanggal Terbit: ").append(rs.getString("tgl_terbit")).append("\n");
            sb.append("-------------------------------------------\n");
        }

        if (sb.length() == 0) {
            sb.append("Belum ada draft tersimpan.");
        }

    } catch (SQLException e) {
        sb.append("[DB] Gagal membaca data: ").append(e.getMessage());
    }

    

    return sb.toString();
    }   

    public static java.util.List<DraftRow> getAllDraftRows() throws SQLException {
    java.util.List<DraftRow> list = new java.util.ArrayList<>();
    String sql = """
        SELECT id, principal, obligee, pekerjaan, jenis_jaminan, nilai_jaminan, jangka_hari, tgl_terbit
        FROM draft_jaminan
        ORDER BY id DESC
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL);
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {

        while (rs.next()) {
            list.add(new DraftRow(
                rs.getInt("id"),
                rs.getString("principal"),
                rs.getString("obligee"),
                rs.getString("pekerjaan"),
                rs.getString("jenis_jaminan"),
                rs.getDouble("nilai_jaminan"),
                rs.getLong("jangka_hari"),
                rs.getString("tgl_terbit")
            ));
        }
    }
    return list;
}


}
