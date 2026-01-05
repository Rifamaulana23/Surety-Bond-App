package com.surety;

import java.util.Map;

public class TemplateResolver {
    private static final Map<String, String> MAP = Map.of(
        "BB tender", "/templates/BB_Takaful_Tender_Template.docx",
        "BB mini Komp", "/templates/BB_Takaful_MiniKomp_Template.docx",
        "Performance Bond", "/templates/PB_Takaful_Template.docx",
        "Advance Payment Bond", "/templates/APB_Takaful_Template.docx",
        "Maintenance Bond", "/templates/MB_Takaful_Template.docx",
        "SPPA", "/templates/SPPA_Template.docx"

    );

    public static String resolve(String jenisJaminan) {
        return MAP.get(jenisJaminan);
    }
}
