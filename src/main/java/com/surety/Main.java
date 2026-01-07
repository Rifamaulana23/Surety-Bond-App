package com.surety;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Main.java
 * - Modern responsive UI (card per section)
 * - All original business logic preserved
 * - Replaced inline text messages with a custom popup "sweet-alert"-like UI
 */
public class Main extends Application {

    
    private StackPane overlayPane;

    @Override
    public void start(Stage stage) {
        Database.init();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#f4f6f9;");

        /* ================= HEADER ================= */
        Label title = new Label("Draft Jaminan — Surety Bond");
        title.setStyle("-fx-font-size:22px;-fx-font-weight:700;");

        Label subtitle = new Label("Kelola draft jaminan, generate dokumen dan SPPA");
        subtitle.setStyle("-fx-text-fill:#6b7280;");

        VBox header = new VBox(6, title, subtitle);
        header.setPadding(new Insets(16));
        root.setTop(header);

        /* ================= FORM FIELDS ================= */
        TextField tfPrincipal = new TextField();
        tfPrincipal.setPromptText("Contoh: PT. ABC Indonesia");

        TextField tfAlamatPrincipal = new TextField();
        tfAlamatPrincipal.setPromptText("Contoh: Jl. Merdeka No.1, Jakarta");

        TextField tfObligee = new TextField();
        tfObligee.setPromptText("Contoh: PT. Pemilik Proyek");

        TextField tfAlamatObligee = new TextField();
        tfAlamatObligee.setPromptText("Alamat lengkap obligee");

        TextField tfPekerjaan = new TextField();
        tfPekerjaan.setPromptText("Contoh: Pembangunan Gedung A - Paket 1");

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
        tfDirektur.setPromptText("Nama direktur yang menandatangani (mis. John Doe)");

        TextField tfJabatan = new TextField();
        tfJabatan.setPromptText("Jabatan direktur (mis. Direktur Utama)");

        DatePicker dpMulai = new DatePicker();
        DatePicker dpSelesai = new DatePicker();
        DatePicker dpTerbit = new DatePicker();

        Label lblJangka = new Label("- hari");
        lblJangka.setStyle("-fx-font-weight:600;");

        TextField tfNilaiKontrak = new TextField();
        tfNilaiKontrak.setPromptText("Contoh: 200000000 (angka tanpa pemisah)");
        TextField tfPersen = new TextField();
        tfPersen.setPromptText("Contoh: 5 (persentase)");
        TextField tfNilaiJaminan = new TextField();
        tfNilaiJaminan.setPromptText("Nilai jaminan (otomatis dihitung jika Kontrak+%)");
        tfNilaiJaminan.setEditable(false);

        RadioButton rbKontrak = new RadioButton("Nilai Kontrak + %");
        RadioButton rbLangsung = new RadioButton("Nilai Jaminan Langsung");
        ToggleGroup tg = new ToggleGroup();
        rbKontrak.setToggleGroup(tg);
        rbLangsung.setToggleGroup(tg);
        rbKontrak.setSelected(true);

        /* ================= LISTENER ================= */
        dpMulai.valueProperty().addListener((a,b,c)->updateJangka(dpMulai, dpSelesai, lblJangka));
        dpSelesai.valueProperty().addListener((a,b,c)->updateJangka(dpMulai, dpSelesai, lblJangka));

        tfNilaiKontrak.textProperty().addListener((a,b,c)->calcJaminan(rbKontrak, tfNilaiKontrak, tfPersen, tfNilaiJaminan));
        tfPersen.textProperty().addListener((a,b,c)->calcJaminan(rbKontrak, tfNilaiKontrak, tfPersen, tfNilaiJaminan));

        rbKontrak.setOnAction(e->{
            tfNilaiKontrak.setDisable(false);
            tfPersen.setDisable(false);
            tfNilaiJaminan.setEditable(false);
            tfNilaiJaminan.setPromptText("Nilai jaminan (otomatis dihitung saat pilih Kontrak+%)");
        });

        rbLangsung.setOnAction(e->{
            tfNilaiKontrak.setDisable(true);
            tfPersen.setDisable(true);
            tfNilaiJaminan.setEditable(true);
            tfNilaiJaminan.setPromptText("Masukkan nilai jaminan secara manual (mis. 10000000)");
            tfNilaiJaminan.clear();
        });

        
        Control[] fields = { tfPrincipal, tfAlamatPrincipal, tfObligee, tfAlamatObligee, tfPekerjaan,
                tfDasarSurat, dpTanggalSurat, cbJenisJaminan, tfDirektur, tfJabatan,
                dpMulai, dpSelesai, dpTerbit, tfNilaiKontrak, tfPersen, tfNilaiJaminan };
        for (Control c : fields) c.setPrefWidth(620);

        /* ================= CARDS ================= */
        VBox cardData = card("A. Data Dasar",
                grid(
                        row("Nama Principal", tfPrincipal),
                        row("Alamat Principal", tfAlamatPrincipal),
                        row("Nama Obligee", tfObligee),
                        row("Alamat Obligee", tfAlamatObligee),
                        row("Nama Pekerjaan", tfPekerjaan),
                        row("Dasar Surat", tfDasarSurat),
                        row("Tanggal Surat", dpTanggalSurat),
                        row("Jenis Jaminan", cbJenisJaminan),
                        row("Nama Direktur", tfDirektur),
                        row("Jabatan", tfJabatan)
                )
        );

        VBox cardTanggal = card("B. Tanggal",
                grid(
                        row("Tanggal Mulai", dpMulai),
                        row("Tanggal Selesai", dpSelesai),
                        row("Jangka Waktu", lblJangka),
                        row("Tanggal Terbit", dpTerbit)
                )
        );

        VBox cardNilai = card("C. Nilai Jaminan",
                grid(
                        row("Mode Nilai", new VBox(6, rbKontrak, rbLangsung)),
                        row("Nilai Kontrak", tfNilaiKontrak),
                        row("Persentase Jaminan (%)", tfPersen),
                        row("Nilai Jaminan", tfNilaiJaminan)
                )
        );
        Button btnOpenOutput = actionBtn("📂 Output", "#0ea5e9");
        Button btnSave = actionBtn("Simpan", "#10b981");
        Button btnGen = actionBtn("Generate", "#2563eb");
        Button btnList = actionBtn("Draft Tersimpan", "#6b7280");
        Button btnUpdate = actionBtn("Update", "#f59e0b");

        HBox actions = new HBox(12, btnOpenOutput, btnSave, btnGen, btnList, btnUpdate);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox cardAction = card("", new VBox(10, actions));

        /* ================= MAIN CONTENT ================= */
        VBox content = new VBox(16, cardData, cardTanggal, cardNilai, cardAction);
        content.setPadding(new Insets(20));
        content.setMaxWidth(1100);
        content.setFillWidth(true);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;");

        StackPane center = new StackPane(scroll);
        StackPane.setMargin(scroll, new Insets(0,20,20,20));

        root.setCenter(center);

        /* ================= OVERLAY (popup) ================= */
        overlayPane = new StackPane();
        overlayPane.setPickOnBounds(false); 
        
        StackPane appStack = new StackPane(root, overlayPane);

        /* ===========================
           RESTORE FUNCTIONALITY: HANDLERS
           =========================== */
        final long[] editingId = new long[] { -1 };

        btnOpenOutput.setOnAction(e -> {
        try {
        File dir = new File("output");

        // Kalau folder belum ada → buat otomatis
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Buka folder di file explorer
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(dir);
        } else {
            Runtime.getRuntime().exec("explorer " + dir.getAbsolutePath());
        }

        } catch (Exception ex) {
         ex.printStackTrace();
        new Alert(Alert.AlertType.ERROR, "Gagal membuka folder output.").showAndWait();
         }
        });

        btnSave.setOnAction(e -> {
            double nilaiJaminan = parseDouble(tfNilaiJaminan.getText());
            double nilaiKontrak = parseDouble(tfNilaiKontrak.getText());
            double persen = parseDouble(tfPersen.getText());

            LocalDate tanggalSurat = dpTanggalSurat.getValue();
            LocalDate mulai = dpMulai.getValue();
            LocalDate selesai = dpSelesai.getValue();
            LocalDate terbit = dpTerbit.getValue();

            long jangkaHari = 0;
            if (mulai != null && selesai != null && !selesai.isBefore(mulai)) {
                jangkaHari = ChronoUnit.DAYS.between(mulai, selesai) + 1;
            }

            String modeNilai = rbKontrak.isSelected() ? "Kontrak+Persen" : "NilaiJaminan";

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

            showPopup("Sukses", "Draft tersimpan ke database. Data boleh belum lengkap, bisa dilengkapi nanti.", PopupType.SUCCESS);
            System.out.println("=== DRAFT DISIMPAN KE DB ===");
        });

        
        btnList.setOnAction(e -> {
            System.out.println("### BTN DAFTAR CLICKED -> OPEN TABLEVIEW ###");

            new DraftTableView().show(id -> {
                System.out.println("EDIT ID: " + id);
                try {
                    Map<String, Object> d = Database.getDraftById(id);
                    if (d == null) {
                        showPopup("Tidak Ditemukan", "Data draft tidak ditemukan.", PopupType.WARNING);
                        return;
                    }

                    editingId[0] = id;

                    tfPrincipal.setText((String) d.get("principal"));
                    tfAlamatPrincipal.setText((String) d.get("alamat_principal"));
                    tfObligee.setText((String) d.get("obligee"));
                    tfAlamatObligee.setText((String) d.get("alamat_obligee"));
                    tfPekerjaan.setText((String) d.get("pekerjaan"));
                    tfDasarSurat.setText((String) d.get("dasar_surat"));

                    cbJenisJaminan.setValue((String) d.get("jenis_jaminan"));
                    tfDirektur.setText((String) d.get("direktur"));
                    tfJabatan.setText((String) d.get("jabatan"));

                    String tglDasar = (String) d.get("tgl_dasar_surat");
                    dpTanggalSurat.setValue(tglDasar == null ? null : LocalDate.parse(tglDasar));

                    String mulaiStr = (String) d.get("tgl_mulai");
                    dpMulai.setValue(mulaiStr == null ? null : LocalDate.parse(mulaiStr));

                    String selesaiStr = (String) d.get("tgl_selesai");
                    dpSelesai.setValue(selesaiStr == null ? null : LocalDate.parse(selesaiStr));

                    String terbitStr = (String) d.get("tgl_terbit");
                    dpTerbit.setValue(terbitStr == null ? null : LocalDate.parse(terbitStr));

                    
                    String mode = (String) d.get("mode_nilai");
                    if ("NilaiJaminan".equals(mode)) {
                        rbLangsung.fire();
                    } else {
                        rbKontrak.fire();
                    }

                    
                    Object nk = d.get("nilai_kontrak");
                    Object p = d.get("persen");
                    Object nj = d.get("nilai_jaminan");

                    tfNilaiKontrak.setText(nk == null ? "" : String.valueOf(((Number) nk).doubleValue()));
                    tfPersen.setText(p == null ? "" : String.valueOf(((Number) p).doubleValue()));
                    tfNilaiJaminan.setText(nj == null ? "" : String.valueOf(((Number) nj).doubleValue()));

                    showPopup("Edit Mode", "✏️ Edit mode aktif (ID=" + editingId[0] + "). Ubah lalu klik Update Draft.", PopupType.INFO);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showPopup("Error", "Gagal load data untuk edit.", PopupType.ERROR);
                }
            });
        });

        
        btnUpdate.setOnAction(e -> {
            try {
                if (editingId[0] <= 0) {
                    showPopup("Info", "Pilih draft dulu dari tabel (klik Edit).", PopupType.INFO);
                    return;
                }

                double nilaiJaminan = parseDouble(tfNilaiJaminan.getText());
                double nilaiKontrak = parseDouble(tfNilaiKontrak.getText());
                double persen = parseDouble(tfPersen.getText());

                LocalDate tanggalSurat = dpTanggalSurat.getValue();
                LocalDate mulai = dpMulai.getValue();
                LocalDate selesai = dpSelesai.getValue();
                LocalDate terbit = dpTerbit.getValue();

                long jangkaHari = 0;
                if (mulai != null && selesai != null && !selesai.isBefore(mulai)) {
                    jangkaHari = ChronoUnit.DAYS.between(mulai, selesai) + 1;
                }

                String modeNilai = rbKontrak.isSelected() ? "Kontrak+Persen" : "NilaiJaminan";

                Database.updateDraft(
                        editingId[0],
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

                showPopup("Sukses", "Draft berhasil di-update (ID=" + editingId[0] + ")", PopupType.SUCCESS);
                editingId[0] = -1;
            } catch (Exception ex) {
                ex.printStackTrace();
                showPopup("Gagal", "Gagal update draft.", PopupType.ERROR);
            }
        });

        
        btnGen.setOnAction(e -> {
            try {
                Map<String, String> data = new HashMap<>();

                data.put("NAMA_PRINCIPAL", tfPrincipal.getText());
                data.put("ALAMAT_PRINCIPAL", tfAlamatPrincipal.getText());
                data.put("NAMA_OBLIGEE", tfObligee.getText());
                data.put("ALAMAT_OBLIGEE", tfAlamatObligee.getText());
                data.put("NAMA_PEKERJAAN", tfPekerjaan.getText());
                data.put("DASAR_SURAT", tfDasarSurat.getText());
                data.put("TGL_DASAR_SURAT", TanggalUtil.formatIndonesia(dpTanggalSurat.getValue()));
                data.put("JENIS_JAMINAN", cbJenisJaminan.getValue());
                data.put("NAMA_DIREKTUR_PRINCIPAL", tfDirektur.getText());
                data.put("JABATAN_DIREKTUR_PRINCIPAL", tfJabatan.getText());

                data.put("TGL_MULAI", TanggalUtil.formatIndonesia(dpMulai.getValue()));
                data.put("TGL_SELESAI", TanggalUtil.formatIndonesia(dpSelesai.getValue()));
                data.put("TGL_TERBIT", TanggalUtil.formatIndonesia(dpTerbit.getValue()));

                LocalDate mulai = dpMulai.getValue();
                LocalDate selesai = dpSelesai.getValue();

                long jangkaHari = 0;
                if (mulai != null && selesai != null && !selesai.isBefore(mulai)) {
                    jangkaHari = ChronoUnit.DAYS.between(mulai, selesai) + 1;
                }

                data.put("JANGKA_WAKTU", jangkaHari == 0 ? "" : String.valueOf(jangkaHari));
                data.put("JANGKA_WAKTU_TERBILANG", jangkaHari == 0 ? "" : TerbilangUtil.terbilang(jangkaHari));

                data.put("NILAI_KONTRAK", tfNilaiKontrak.getText());
                data.put("PERSEN", tfPersen.getText());
                long nilaiJaminanAngka = (long) parseDouble(tfNilaiJaminan.getText());

                data.put("NILAI_JAMINAN", CurrencyUtil.rupiah(nilaiJaminanAngka));
                data.put("NILAI_JAMINAN_TERBILANG", TerbilangUtil.rupiah(nilaiJaminanAngka));

                String jenis = cbJenisJaminan.getValue();
                String templateRes = TemplateResolver.resolve(jenis);
                if (templateRes == null) {
                    System.out.println("[DOCX] Jenis jaminan belum dipilih / belum dimapping: " + jenis);
                    showPopup("Peringatan", "Pilih jenis jaminan terlebih dahulu.", PopupType.WARNING);
                    return;
                }

                String outputPath = "output/" + jenis.replaceAll("\\s+", "_") + "_" + safeFile(tfPrincipal.getText()) + ".docx";
                DocxGenerator.generateFromResource(templateRes, outputPath, data);

                String spTemplate = TemplateResolver.resolve("SPPA");
                String spOutput = "output/SPPA_" + safeFile(tfPrincipal.getText()) + ".docx";
                DocxGenerator.generateFromResource(spTemplate, spOutput, data);

                showPopup("Sukses", "Draft berhasil digenerate! (" + outputPath + ")", PopupType.SUCCESS);
                System.out.println("[DOCX] Generated: " + outputPath);
            } catch (Exception ex) {
                ex.printStackTrace();
                showPopup("Gagal", "Gagal salah input data atau belum memilih jenis jaminan!", PopupType.ERROR);
            }
        });

        /* ================= SCENE ================= */
        Scene scene = new Scene(appStack(root, overlayPane), 1200, 820);
        stage.setTitle("Surety Bond App");
        stage.setScene(scene);
        stage.show();
    }

    
    private StackPane appStack(BorderPane root, StackPane overlay) {
        StackPane stack = new StackPane(root, overlay);
        
        overlay.setPickOnBounds(false);
        return stack;
    }

    
    private enum PopupType { SUCCESS, ERROR, INFO, WARNING }

    
    private void showPopup(String title, String message, PopupType type) {
        Platform.runLater(() -> {
            
            overlayPane.getChildren().clear();
            overlayPane.setPickOnBounds(true);

            
            Region backdrop = new Region();
            backdrop.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            backdrop.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
            StackPane.setAlignment(backdrop, Pos.CENTER);

            
            String bgColor;
            String icon;
            switch (type) {
                case SUCCESS -> { bgColor = "#ecfdf5"; icon = "✅"; }
                case ERROR -> { bgColor = "#fff1f2"; icon = "✖️"; }
                case WARNING -> { bgColor = "#fffbeb"; icon = "⚠️"; }
                default -> { bgColor = "#eef2ff"; icon = "ℹ️"; }
            }

            Label iconLabel = new Label(icon);
            iconLabel.setStyle("-fx-font-size: 26px;");

            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-weight:700; -fx-font-size:14px;");

            Label msgLabel = new Label(message);
            msgLabel.setWrapText(true);
            msgLabel.setMaxWidth(420);

            Button ok = new Button("OK");
            ok.setStyle("-fx-background-color:#111827; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 12;");
            ok.setOnAction(ae -> hidePopup());

            VBox box = new VBox(12);
            box.setPadding(new Insets(20));
            box.setAlignment(Pos.CENTER);

            box.getChildren().addAll(iconLabel, titleLabel, msgLabel, ok);

            
            box.setMaxWidth(420);
            box.setMinHeight(Region.USE_PREF_SIZE);
            box.setMaxHeight(Region.USE_PREF_SIZE);

            box.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-background-radius: 12;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 18, 0, 0, 10);
            """, bgColor));

            StackPane.setAlignment(box, Pos.CENTER);

            overlayPane.getChildren().addAll(backdrop, box);

            
            PauseTransition pt = new PauseTransition(Duration.seconds(4));
            pt.setOnFinished(ev -> hidePopup());
            pt.play();
        });
    }

    private void hidePopup() {
        Platform.runLater(() -> {
            overlayPane.getChildren().clear();
            overlayPane.setPickOnBounds(false);
        });
    }

    /* ================= HELPERS ================= */

    private VBox card(String title, Node body){
        VBox v = new VBox(12);
        v.setPadding(new Insets(16));
        v.setStyle("""
                -fx-background-color:white;
                -fx-background-radius:12;
                -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),14,0,0,6);
        """);
        if(title != null && !title.isBlank()){
            Label l = new Label(title);
            l.setStyle("-fx-font-weight:700;");
            v.getChildren().add(l);
        }
        v.getChildren().add(body);
        VBox.setVgrow(body, Priority.ALWAYS);
        return v;
    }

    private GridPane grid(Node... rows){
        GridPane g = new GridPane();
        g.setHgap(14);
        g.setVgap(10);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setMinWidth(180);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);

        g.getColumnConstraints().addAll(c1, c2);

        for(int i=0;i<rows.length;i++){
            g.add(rows[i],0,i,2,1);
        }
        return g;
    }

    private HBox row(String label, Node field){
        Label l = new Label(label);
        l.setMinWidth(180);
        HBox h = new HBox(12, l, field);
        HBox.setHgrow(field, Priority.ALWAYS);
        return h;
    }

    private Button actionBtn(String text, String color){
        Button b = new Button(text);
        b.setStyle("""
                -fx-background-color:%s;
                -fx-text-fill:white;
                -fx-font-weight:600;
                -fx-background-radius:8;
        """.formatted(color));
        return b;
    }

    private void updateJangka(DatePicker m, DatePicker s, Label l){
        try {
            if(m.getValue()!=null && s.getValue()!=null){
                long d = ChronoUnit.DAYS.between(m.getValue(), s.getValue())+1;
                l.setText(d>0?d+" hari":"- hari");
            } else {
                l.setText("- hari");
            }
        } catch (Exception e) {
            l.setText("- hari");
        }
    }

    private void calcJaminan(RadioButton rb, TextField nk, TextField p, TextField nj){
        if(!rb.isSelected()) return;
        try{
            double kontrak = parseDouble(nk.getText());
            double persen = parseDouble(p.getText());
            if (kontrak <= 0 || persen <= 0) { nj.clear(); return; }
            double v = kontrak * persen / 100.0;
            nj.setText(String.format("%.0f", v));
        }catch(Exception e){ nj.clear(); }
    }

    
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
