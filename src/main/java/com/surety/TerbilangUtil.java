package com.surety;

public class TerbilangUtil {

    private static final String[] ANGKA = {
            "", "satu", "dua", "tiga", "empat", "lima",
            "enam", "tujuh", "delapan", "sembilan", "sepuluh", "sebelas"
    };

    // Khusus buat "hari" (tanpa kata rupiah)
    public static String hari(long n) {
        if (n == 0) return "Nol";
        String t = terbilang(n).trim().replaceAll("\\s+", " ");
        return kapitalAwal(t);
    }

    // Buat rupiah (yang ini tetap buat nilai uang)
    public static String rupiah(long n) {
        if (n == 0) return "Nol rupiah";
        String t = terbilang(n).trim().replaceAll("\\s+", " ");
        return kapitalAwal(t) + " rupiah";
    }

    private static String kapitalAwal(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // terbilang inti (tanpa "rupiah")
    public static String terbilang(long n) {
        if (n < 0) return "minus " + terbilang(-n);

        if (n < 12) return ANGKA[(int) n];
        if (n < 20) return terbilang(n - 10) + " belas";
        if (n < 100) return terbilang(n / 10) + " puluh " + terbilang(n % 10);
        if (n < 200) return "seratus " + terbilang(n - 100);
        if (n < 1000) return terbilang(n / 100) + " ratus " + terbilang(n % 100);
        if (n < 2000) return "seribu " + terbilang(n - 1000);
        if (n < 1_000_000) return terbilang(n / 1000) + " ribu " + terbilang(n % 1000);
        if (n < 1_000_000_000) return terbilang(n / 1_000_000) + " juta " + terbilang(n % 1_000_000);
        if (n < 1_000_000_000_000L) return terbilang(n / 1_000_000_000) + " miliar " + terbilang(n % 1_000_000_000);
        if (n < 1_000_000_000_000_000L) return terbilang(n / 1_000_000_000_000L) + " triliun " + terbilang(n % 1_000_000_000_000L);

        return "angka terlalu besar";
    }
}
