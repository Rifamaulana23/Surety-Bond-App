package com.surety;

public class DraftRow {
    public final long id;
    public final String principal;
    public final String obligee;
    public final String pekerjaan;
    public final String jenis;
    public final double nilaiJaminan;
    public final long jangkaHari;
    public final String tglTerbit; // atau LocalDate kalau kamu simpan itu

    public DraftRow(long id, String principal, String obligee, String pekerjaan,
                    String jenis, double nilaiJaminan, long jangkaHari, String tglTerbit) {
        this.id = id;
        this.principal = principal;
        this.obligee = obligee;
        this.pekerjaan = pekerjaan;
        this.jenis = jenis;
        this.nilaiJaminan = nilaiJaminan;
        this.jangkaHari = jangkaHari;
        this.tglTerbit = tglTerbit;
    }
}
