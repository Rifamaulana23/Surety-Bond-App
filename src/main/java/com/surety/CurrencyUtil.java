package com.surety;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {

    private static final Locale LOCALE_ID = new Locale("id", "ID");

    public static String rupiah(double value) {
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_ID);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(value);
    }
}
