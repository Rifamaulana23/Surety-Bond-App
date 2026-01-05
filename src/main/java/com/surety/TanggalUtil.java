package com.surety;

import java.time.LocalDate;

public class TanggalUtil {

    private static final String[] BULAN = {
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    public static String formatIndonesia(LocalDate date) {
        if (date == null) return "";

        int hari = date.getDayOfMonth();
        int bulan = date.getMonthValue(); // 1-12
        int tahun = date.getYear();

        return hari + " " + BULAN[bulan - 1] + " " + tahun;
    }
}
