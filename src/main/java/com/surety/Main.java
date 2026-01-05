package com.surety;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Database.init();


        // ========== LAYOUT UTAMA ==========
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(8);
        grid.setHgap(15);

        // Atur lebar kolom biar rapi
        ColumnConstraints colLabel = new ColumnConstraints();
        colLabel.setPrefWidth(150);

        ColumnConstraints colField = new ColumnConstraints();
        colField.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(colLabel, colField);

        int row = 0;

        // ========== HEADER ==========
        Label header = new Label("Draft Jaminan Surety Bond");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        grid.add(header, 0, row, 2, 1);
        GridPane.setHalignment(header, HPos.CENTER);
        row++;

        // Spasi kecil setelah header
        row++;

        // ========== FIELD DATA DASAR ==========
        TextField tfPrincipal = new TextField();
        TextField tfAlamatPrincipal = new TextField();
        TextField tfObligee = new TextField();
        TextField tfAlamatObligee = new TextField();
        TextField tfPekerjaan = new TextField();
        TextField tfDasarSurat = new TextField();
        tfDasarSurat.setPromptText("Contoh: SPK No. 123/ABC/2025");
        DatePicker dpTanggalSurat = new DatePicker();
        ComboBox<String> cbJenisJaminan = new ComboBox<>();
        cbJenisJaminan.getItems().addAll(
    "BB tender",
                "BB mini Komp",
                "Performance Bond", 
                "Advance Payment Bond",
                "Maintenance Bond"
        );
        cbJenisJaminan.setPromptText("Pilih jenis jaminan");

        TextField tfDirektur = new TextField();
        TextField tfJabatan = new TextField();

        // ========== TANGGAL & JANGKA WAKTU ==========
        DatePicker dpMulai = new DatePicker();
        DatePicker dpSelesai = new DatePicker();
        DatePicker dpTerbit = new DatePicker();
        Label lblJangkaWaktu = new Label("- hari");

        // Listener jangka waktu
        Runnable updateJangka = () -> hitungJangkaWaktu(dpMulai, dpSelesai, lblJangkaWaktu);
        dpMulai.valueProperty().addListener((obs, o, n) -> updateJangka.run());
        dpSelesai.valueProperty().addListener((obs, o, n) -> updateJangka.run());

        // ========== MODE NILAI JAMINAN ==========
        RadioButton rbNilaiKontrak = new RadioButton("Input nilai kontrak + persentase");
        RadioButton rbNilaiJaminan = new RadioButton("Input nilai jaminan langsung");
        ToggleGroup tgMode = new ToggleGroup();
        rbNilaiKontrak.setToggleGroup(tgMode);
        rbNilaiJaminan.setToggleGroup(tgMode);
        rbNilaiKontrak.setSelected(true);

        TextField tfNilaiKontrak = new TextField();
        tfNilaiKontrak.setPromptText("Contoh: 200000000");

        TextField tfPersen = new TextField();
        tfPersen.setPromptText("Contoh: 5");

        TextField tfNilaiJaminan = new TextField();
        tfNilaiJaminan.setPromptText("Nilai jaminan");
        tfNilaiJaminan.setEditable(false); // default: dihitung otomatis

        // Listener auto-calc
        Runnable updateNilaiJaminan = () -> {
            if (rbNilaiKontrak.isSelected()) {
                hitungNilaiJaminan(tfNilaiKontrak, tfPersen, tfNilaiJaminan);
            }
        };

        tfNilaiKontrak.textProperty().addListener((obs, o, n) -> updateNilaiJaminan.run());
        tfPersen.textProperty().addListener((obs, o, n) -> updateNilaiJaminan.run());

        // Mode toggle
        rbNilaiKontrak.setOnAction(e -> {
            tfNilaiKontrak.setDisable(false);
            tfPersen.setDisable(false);
            tfNilaiJaminan.setEditable(false);
            updateNilaiJaminan.run();
        });

        rbNilaiJaminan.setOnAction(e -> {
            tfNilaiKontrak.setDisable(true);
            tfPersen.setDisable(true);
            tfNilaiJaminan.setEditable(true);
            tfNilaiJaminan.clear();
        });

        // ========== SUSUN FORM ==========
        grid.add(new Label("Nama Principal"), 0, row); grid.add(tfPrincipal, 1, row++);        
        grid.add(new Label("Alamat Principal"), 0, row); grid.add(tfAlamatPrincipal, 1, row++);
        grid.add(new Label("Nama Obligee"), 0, row); grid.add(tfObligee, 1, row++);
        grid.add(new Label("Alamat Obligee"), 0, row); grid.add(tfAlamatObligee, 1, row++);
        grid.add(new Label("Nama Pekerjaan"), 0, row); grid.add(tfPekerjaan, 1, row++);
        grid.add(new Label("Dasar Surat"), 0, row); grid.add(tfDasarSurat, 1, row++);
        grid.add(new Label("Tanggal Surat"), 0, row); grid.add(dpTanggalSurat, 1, row++);
        grid.add(new Label("Jenis Jaminan"), 0, row); grid.add(cbJenisJaminan, 1, row++);
        grid.add(new Label("Nama Direktur"), 0, row); grid.add(tfDirektur, 1, row++);
        grid.add(new Label("Jabatan"), 0, row); grid.add(tfJabatan, 1, row++);

        grid.add(new Label("Tanggal Mulai"), 0, row); grid.add(dpMulai, 1, row++);
        grid.add(new Label("Tanggal Selesai"), 0, row); grid.add(dpSelesai, 1, row++);
        grid.add(new Label("Jangka Waktu"), 0, row); grid.add(lblJangkaWaktu, 1, row++);
        grid.add(new Label("Tanggal Terbit"), 0, row); grid.add(dpTerbit, 1, row++);

        // Mode nilai
        grid.add(new Label("Mode Nilai"), 0, row);
        GridPane modeBox = new GridPane();
        modeBox.setHgap(10);
        modeBox.add(rbNilaiKontrak, 0, 0);
        modeBox.add(rbNilaiJaminan, 0, 1);
        grid.add(modeBox, 1, row++);
        
        grid.add(new Label("Nilai Kontrak"), 0, row); grid.add(tfNilaiKontrak, 1, row++);
        grid.add(new Label("Persentase Jaminan (%)"), 0, row); grid.add(tfPersen, 1, row++);
        grid.add(new Label("Nilai Jaminan"), 0, row); grid.add(tfNilaiJaminan, 1, row++);

        // ========== BUTTON & INFO ==========
        Button btnSimpan = new Button("Simpan");
        Button btnGenerate = new Button("Generate Draft");
        Button btnDaftar = new Button("Lihat Draft Tersimpan");
        btnDaftar.setMinWidth(190);


        btnSimpan.setMinWidth(140);
        btnGenerate.setMinWidth(170);

        GridPane buttonBox = new GridPane();
        buttonBox.setHgap(10);
        buttonBox.add(btnSimpan, 0, 0);
        buttonBox.add(btnGenerate, 1, 0);
        buttonBox.add(btnDaftar, 2, 0);     
        grid.add(buttonBox, 1, row++);

        Label lblInfo = new Label("Silakan isi data jaminan dengan lengkap.");
        lblInfo.setWrapText(true);
        grid.add(lblInfo, 0, row, 2, 1);

    // ========== AKSI TOMBOL (masih dummy) ==========
    btnSimpan.setOnAction(e -> {
    double nilaiJaminan = parseDouble(tfNilaiJaminan.getText());
    double nilaiKontrak = parseDouble(tfNilaiKontrak.getText());
    double persen = parseDouble(tfPersen.getText());
    
    var tanggalSurat = dpTanggalSurat.getValue();
    var mulai = dpMulai.getValue();
    var selesai = dpSelesai.getValue();
    var terbit = dpTerbit.getValue();

    long jangkaHari = 0;
    if (mulai != null && selesai != null && !selesai.isBefore(mulai)) {
        jangkaHari = ChronoUnit.DAYS.between(mulai, selesai) + 1;
    }

    String modeNilai = rbNilaiKontrak.isSelected()
            ? "Kontrak+Persen"
            : "NilaiJaminan";

    // simpan ke DB
    Database.simpanDraft(
            tfPrincipal.getText(),
            tfAlamatPrincipal.getText(),
            tfObligee.getText(),
            tfAlamatObligee.getText(),
            tfPekerjaan.getText(),
            tfDasarSurat.getText(),
            (tanggalSurat != null ? tanggalSurat.toString() : null),
            cbJenisJaminan.getValue(),
            tfDirektur.getText(),
            tfJabatan.getText(),
            (mulai != null ? mulai.toString() : null),
            (selesai != null ? selesai.toString() : null),
            (terbit != null ? terbit.toString() : null),
            modeNilai,
            nilaiKontrak,
            persen,
            nilaiJaminan,
            jangkaHari
    );

    lblInfo.setText("✅ Draft tersimpan ke database. Data boleh belum lengkap, bisa dilengkapi nanti.");

    System.out.println("=== DRAFT DISIMPAN KE DB ===");
});

btnDaftar.setOnAction(e -> {
    Stage listStage = new Stage();
    listStage.setTitle("Daftar Draft Jaminan");

    TextArea area = new TextArea();
    area.setEditable(false);
    area.setStyle("-fx-font-family: Consolas; -fx-font-size: 12px;");

    String data = Database.getAllDrafts();
    area.setText(data);

    Scene sc = new Scene(area, 600, 400);
    listStage.setScene(sc);
    listStage.show();
});

btnGenerate.setOnAction(e -> {
    try {
        Map<String, String> data = new HashMap<>();

        // sesuaikan KEY ini dengan placeholder di template Draft
        data.put("NAMA_PRINCIPAL", tfPrincipal.getText());
        data.put("ALAMAT_PRINCIPAL", tfAlamatPrincipal.getText());
        data.put("NAMA_OBLIGEE", tfObligee.getText());
        data.put("ALAMAT_OBLIGEE", tfAlamatObligee.getText());
        data.put("NAMA_PEKERJAAN", tfPekerjaan.getText());
        data.put("DASAR_SURAT", tfDasarSurat.getText());
        data.put("TGL_DASAR_SURAT",
        TanggalUtil.formatIndonesia(dpTanggalSurat.getValue()));
        data.put("JENIS_JAMINAN", cbJenisJaminan.getValue());
        data.put("NAMA_DIREKTUR_PRINCIPAL", tfDirektur.getText());
        data.put("JABATAN_DIREKTUR_PRINCIPAL", tfJabatan.getText());

        data.put("TGL_MULAI",
        TanggalUtil.formatIndonesia(dpMulai.getValue()));

        data.put("TGL_SELESAI",
        TanggalUtil.formatIndonesia(dpSelesai.getValue()));

        data.put("TGL_TERBIT",
        TanggalUtil.formatIndonesia(dpTerbit.getValue()));

        String jwText = lblJangkaWaktu.getText().trim();   // contoh: "8"
        long jangkaHari = 0;
        if (dpMulai.getValue() != null && dpSelesai.getValue() != null) {
        jangkaHari = java.time.temporal.ChronoUnit.DAYS.between(dpMulai.getValue(), dpSelesai.getValue());
        if (jangkaHari < 0) jangkaHari = 0; // kalau kebalik tanggalnya
}
        data.put("JANGKA_WAKTU", jangkaHari == 0 ? "" : String.valueOf(jangkaHari)); // TANPA kata "hari"
        data.put("JANGKA_WAKTU_TERBILANG", jangkaHari == 0 ? "" : TerbilangUtil.terbilang(jangkaHari)); // "empat" dll

        data.put("NILAI_KONTRAK", tfNilaiKontrak.getText());
        data.put("PERSEN", tfPersen.getText());
        long nilaiJaminanAngka = (long) parseDouble(tfNilaiJaminan.getText());

        data.put("NILAI_JAMINAN", CurrencyUtil.rupiah(nilaiJaminanAngka));

        data.put("NILAI_JAMINAN_TERBILANG",
        TerbilangUtil.rupiah(nilaiJaminanAngka));

        String jenis = cbJenisJaminan.getValue(); // ambil jenis yang dipilih user

        String templateRes = TemplateResolver.resolve(jenis);
        if (templateRes == null) {
            System.out.println("[DOCX] Jenis jaminan belum dipilih / belum dimapping: " + jenis);
            return; // atau tampilkan alert
        }

        //draft jaminan
        String outputPath = "output/" + jenis.replaceAll("\\s+", "_") + "_" + safeFile(tfPrincipal.getText()) + ".docx";

        DocxGenerator.generateFromResource(templateRes, outputPath, data);

        // ===== GENERATE SURAT PERMOHONAN (SPPA) =====
        String spTemplate = TemplateResolver.resolve("SPPA");
        String spOutput = "output/SPPA_" + safeFile(tfPrincipal.getText()) + ".docx";

        DocxGenerator.generateFromResource(spTemplate, spOutput, data);

        lblInfo.setText("✅ Draft berhasil digenerate! (" + outputPath + ")");
        System.out.println("[DOCX] Generated: " + outputPath);

    } catch (Exception ex) {
        ex.printStackTrace();
        lblInfo.setText("Gagal salah input data atau belum memilih jenis jaminan!");
    }

    });

        // ========== TAMPILKAN WINDOW ==========
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true); // biar form ikut lebar window 

        Scene scene = new Scene(scroll, 720, 720);

        stage.setTitle("Surety Bond App - Draft Jaminan");
        stage.setScene(scene);
        stage.show();
    }

    // ===== Helper: Hitung Nilai Jaminan =====
    private void hitungNilaiJaminan(TextField tfNilaiKontrak,
                                    TextField tfPersen,
                                    TextField tfNilaiJaminan) {
        double kontrak = parseDouble(tfNilaiKontrak.getText());
        double persen = parseDouble(tfPersen.getText());
        if (kontrak <= 0 || persen <= 0) {
            tfNilaiJaminan.setText("");
            return;
        }
        double jaminan = kontrak * (persen / 100.0);
        tfNilaiJaminan.setText(String.format("%.0f", jaminan));
    }

    // ===== Helper: Hitung Jangka Waktu =====
    private void hitungJangkaWaktu(DatePicker dpMulai,
                                   DatePicker dpSelesai,
                                   Label lbl) {
        try {
            LocalDate mulai = dpMulai.getValue();
            LocalDate selesai = dpSelesai.getValue();
            if (mulai != null && selesai != null && !selesai.isBefore(mulai)) {
                long days = ChronoUnit.DAYS.between(mulai, selesai) + 1;
                lbl.setText(String.valueOf(days));
            } else {
                lbl.setText("- hari");
            }
        } catch (Exception e) {
            lbl.setText("- hari");
        }
    }

    // ===== Helper: Parsing angka aman =====
    private double parseDouble(String text) {
        if (text == null || text.isBlank()) return 0;
        try {
            String cleaned = text.replace(".", "").replace(",", ".");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String safeFile(String s) {
    if (s == null || s.isBlank()) return "UNKNOWN";
    return s.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }


    public static void main(String[] args) {
        launch();
    }
}
