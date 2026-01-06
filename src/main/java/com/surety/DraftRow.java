package com.surety;

public class DraftRow {
    public final long id;
    public final String principal;
    public final String obligee;
    public final String pekerjaan;
    public final String jenis;
    public final double nilaiJaminan;
    public final long jangkaHari;
    public final String tglTerbit;

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

    // ✅ GETTER (TableView butuh ini)
    public long getId() { return id; }
    public String getPrincipal() { return principal; }
    public String getObligee() { return obligee; }
    public String getPekerjaan() { return pekerjaan; }
    public String getJenis() { return jenis; }
    public double getNilaiJaminan() { return nilaiJaminan; }
    public long getJangkaHari() { return jangkaHari; }
    public String getTglTerbit() { return tglTerbit; }
}
