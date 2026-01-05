package com.surety;

import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.Map;

public class DocxGenerator {

    public static void generate(String templatePath, String outputPath, Map<String, String> data) throws Exception {
        try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            // replace di paragraf
            for (XWPFParagraph p : doc.getParagraphs()) {
                replaceInParagraph(p, data);
            }
                
            // replace di tabel (kalau ada)
            for (XWPFTable tbl : doc.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            replaceInParagraph(p, data);
                        }
                    }
                }
            }

            // pastikan folder output ada
            File outFile = new File(outputPath);
            outFile.getParentFile().mkdirs();

            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                doc.write(fos);
            }
        }
    }

    private static void replaceInParagraph(XWPFParagraph p, Map<String, String> data) {
        for (XWPFRun run : p.getRuns()) {
            String text = run.getText(0);
            if (text == null) continue;

            for (Map.Entry<String, String> e : data.entrySet()) {
                String key = "{{" + e.getKey() + "}}";
                String val = (e.getValue() == null) ? "" : e.getValue();
                text = text.replace(key, val);
            }

            run.setText(text, 0);
        }
    }


    public static void generateFromResource(String templateResource, String outputPath, Map<String, String> data) throws Exception {
    try (InputStream is = DocxGenerator.class.getResourceAsStream(templateResource)) {
        if (is == null) throw new FileNotFoundException("Template tidak ditemukan: " + templateResource);
        try (XWPFDocument doc = new XWPFDocument(is)) {
            // PAKAI loop replace kamu yang sudah ada (paragraf + tabel)
            for (XWPFParagraph p : doc.getParagraphs()) replaceInParagraph(p, data);
            for (XWPFTable tbl : doc.getTables())
                for (XWPFTableRow row : tbl.getRows())
                    for (XWPFTableCell cell : row.getTableCells())
                        for (XWPFParagraph p : cell.getParagraphs())
                            replaceInParagraph(p, data);

            File outFile = new File(outputPath);
            if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                doc.write(fos);
            }
        }
    }
    }

}
