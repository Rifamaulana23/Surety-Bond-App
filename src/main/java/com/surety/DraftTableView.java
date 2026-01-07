package com.surety;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Consumer;

public class DraftTableView {

    
    public void show(Consumer<Long> onEditId) {
        System.out.println("=== OPENING MODERN TABLEVIEW (DraftTableView) ===");

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Daftar Draft Jaminan");

        
        StackPane rootStack = new StackPane();
        rootStack.setStyle("-fx-background-color: #f3f4f6;");

        
        Label title = new Label("Daftar Draft Jaminan");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:700;");

        TextField tfSearch = new TextField();
        tfSearch.setPromptText("Cari berdasarkan nama perusahaan, pekerjaan, atau jenis...");
        tfSearch.setMinWidth(360);

        Button btnRefresh = styledBtn("Refresh", "#2563eb");
        HBox topRight = new HBox(8, tfSearch, btnRefresh);
        topRight.setAlignment(Pos.CENTER_RIGHT);

        BorderPane topPane = new BorderPane();
        topPane.setLeft(title);
        topPane.setRight(topRight);
        topPane.setPadding(new Insets(12));
        topPane.setStyle("-fx-background-color: transparent;");

        
        TableView<DraftRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DraftRow, String> colPerusahaan = new TableColumn<>("Nama Perusahaan");
        colPerusahaan.setCellValueFactory(new PropertyValueFactory<>("principal"));

        TableColumn<DraftRow, Double> colNilai = new TableColumn<>("Nilai Jaminan");
        colNilai.setCellValueFactory(new PropertyValueFactory<>("nilaiJaminan"));
        colNilai.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) setText(null);
                else setText(CurrencyUtil.rupiah(Math.round(value)));
            }
        });

        TableColumn<DraftRow, String> colPekerjaan = new TableColumn<>("Pekerjaan");
        colPekerjaan.setCellValueFactory(new PropertyValueFactory<>("pekerjaan"));

        TableColumn<DraftRow, String> colJenis = new TableColumn<>("Jenis Jaminan");
        colJenis.setCellValueFactory(new PropertyValueFactory<>("jenis"));

        table.getColumns().addAll(colPerusahaan, colNilai, colPekerjaan, colJenis);

        
        Button btnEdit = styledBtn("Edit", "#10b981");
        Button btnDelete = styledBtn("Delete", "#ef4444");
        Button btnClose = styledBtn("Tutup", "#6b7280");

        HBox bottom = new HBox(10, btnEdit, btnDelete, btnClose);
        bottom.setPadding(new Insets(12));
        bottom.setAlignment(Pos.CENTER_RIGHT);

        
        BorderPane content = new BorderPane();
        content.setTop(topPane);
        content.setCenter(table);
        content.setBottom(bottom);
        content.setPadding(new Insets(10));

        
        VBox card = new VBox(content);
        card.setPadding(new Insets(12));
        card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 6);
            """);
        card.setMaxWidth(980);

        VBox outer = new VBox(12, card);
        outer.setPadding(new Insets(12));
        outer.setAlignment(Pos.TOP_CENTER);

        rootStack.getChildren().add(outer);

        
        StackPane overlay = new StackPane();
        overlay.setPickOnBounds(false); 
        rootStack.getChildren().add(overlay);

        
        Scene scene = new Scene(rootStack, 950, 520);
        stage.setScene(scene);
        stage.show();

        
        ObservableList<DraftRow> master = FXCollections.observableArrayList();
        FilteredList<DraftRow> filtered = new FilteredList<>(master, p -> true);
        table.setItems(filtered);

        
        Runnable doLoad = () -> {
            try {
                List<DraftRow> rows = Database.getAllDraftRows(); 
                Platform.runLater(() -> {
                    master.setAll(rows);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(overlay, "Error", "Gagal load draft dari database.", AlertType.ERROR);
            }
        };

        
        doLoad.run();

        
        tfSearch.textProperty().addListener((obs, oldV, newV) -> {
            final String q = (newV == null) ? "" : newV.trim().toLowerCase();
            filtered.setPredicate(row -> {
                if (q.isEmpty()) return true;
                boolean c1 = row.getPrincipal() != null && row.getPrincipal().toLowerCase().contains(q);
                boolean c2 = row.getPekerjaan() != null && row.getPekerjaan().toLowerCase().contains(q);
                boolean c3 = row.getJenis() != null && row.getJenis().toLowerCase().contains(q);
                return c1 || c2 || c3;
            });
        });

        
        btnRefresh.setOnAction(e -> doLoad.run());

        
        btnEdit.setOnAction(e -> {
            DraftRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(overlay, "Pilih Draft", "Pilih draft dulu yang mau diedit.", AlertType.WARNING);
                return;
            }
            if (onEditId != null) onEditId.accept(selected.getId());
            stage.close();
        });

        
        btnDelete.setOnAction(e -> {
            DraftRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(overlay, "Pilih Draft", "Pilih draft dulu yang mau dihapus.", AlertType.WARNING);
                return;
            }

            showConfirm(overlay,
                    "Konfirmasi Hapus",
                    "Yakin mau hapus draft ini?\n\nID: " + selected.getId() + "\nPrincipal: " + selected.getPrincipal(),
                    confirmed -> {
                        if (confirmed) {
                            try {
                                Database.deleteById(selected.getId());
                                doLoad.run();
                                showAlert(overlay, "Terhapus", "Draft berhasil dihapus.", AlertType.INFO);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                showAlert(overlay, "Gagal", "Gagal menghapus draft.", AlertType.ERROR);
                            }
                        }
                    });
        });

        
        table.setRowFactory(tv -> {
            TableRow<DraftRow> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    DraftRow sel = row.getItem();
                    if (onEditId != null) onEditId.accept(sel.getId());
                    stage.close();
                }
            });
            return row;
        });

        
        btnClose.setOnAction(e -> stage.close());
    }

    

    private enum AlertType { SUCCESS, ERROR, WARNING, INFO }

    
    private void showAlert(StackPane overlay, String title, String message, AlertType type) {
        Platform.runLater(() -> {
            overlay.getChildren().clear();
            overlay.setPickOnBounds(true);

            Region backdrop = new Region();
            backdrop.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            backdrop.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

            String bg;
            String icon;
            switch (type) {
                case SUCCESS -> { bg = "#ecfdf5"; icon = "✅"; }
                case ERROR -> { bg = "#fff1f2"; icon = "✖️"; }
                case WARNING -> { bg = "#fffbeb"; icon = "⚠️"; }
                default -> { bg = "#eef2ff"; icon = "ℹ️"; }
            }

            Label iconLbl = new Label(icon);
            iconLbl.setStyle("-fx-font-size:28px;");

            Label titleLbl = new Label(title);
            titleLbl.setStyle("-fx-font-weight:700; -fx-font-size:14px;");
            titleLbl.setWrapText(true);
            titleLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            Label msgLbl = new Label(message);
            msgLbl.setWrapText(true);
            msgLbl.setMaxWidth(380);
            msgLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            Button ok = new Button("OK");
            ok.setStyle("-fx-background-color:#111827; -fx-text-fill:white; -fx-background-radius:8;");
            ok.setOnAction(ev -> {
                overlay.getChildren().clear();
                overlay.setPickOnBounds(false);
            });

            VBox box = new VBox(10, iconLbl, titleLbl, msgLbl, ok);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(16));
            box.setMaxWidth(420);
            box.setMinHeight(Region.USE_PREF_SIZE);
            box.setMaxHeight(Region.USE_PREF_SIZE);
            box.setStyle(String.format("-fx-background-color:%s; -fx-background-radius:12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 16,0,0,8);", bg));

            StackPane wrapper = new StackPane(backdrop, box);
            StackPane.setAlignment(box, Pos.CENTER);

            overlay.getChildren().add(wrapper);

            PauseTransition pt = new PauseTransition(Duration.seconds(3.5));
            pt.setOnFinished(ev -> {
                overlay.getChildren().clear();
                overlay.setPickOnBounds(false);
            });
            pt.play();
        });
    }

    
    private void showConfirm(StackPane overlay, String title, String message, Consumer<Boolean> resultCb) {
        Platform.runLater(() -> {
            overlay.getChildren().clear();
            overlay.setPickOnBounds(true);

            Region backdrop = new Region();
            backdrop.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
            backdrop.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

            Label icon = new Label("⚠️");
            icon.setStyle("-fx-font-size:26px;");

            Label titleLbl = new Label(title);
            titleLbl.setStyle("-fx-font-weight:700; -fx-font-size:14px;");
            titleLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            Label msgLbl = new Label(message);
            msgLbl.setWrapText(true);
            msgLbl.setMaxWidth(420);
            msgLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            Button btnYes = new Button("Ya");
            btnYes.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white; -fx-background-radius:8;");

            Button btnNo = new Button("Batal");
            btnNo.setStyle("-fx-background-color:#6b7280; -fx-text-fill:white; -fx-background-radius:8;");

            HBox actions = new HBox(10, btnNo, btnYes);
            actions.setAlignment(Pos.CENTER);

            VBox box = new VBox(12, icon, titleLbl, msgLbl, actions);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(16));
            box.setMaxWidth(460);
            box.setMinHeight(Region.USE_PREF_SIZE);
            box.setMaxHeight(Region.USE_PREF_SIZE);
            box.setStyle("-fx-background-color:#fff7ed; -fx-background-radius:12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 16,0,0,8);");

            StackPane wrapper = new StackPane(backdrop, box);
            StackPane.setAlignment(box, Pos.CENTER);

            btnNo.setOnAction(ev -> {
                overlay.getChildren().clear();
                overlay.setPickOnBounds(false);
                resultCb.accept(false);
            });

            btnYes.setOnAction(ev -> {
                overlay.getChildren().clear();
                overlay.setPickOnBounds(false);
                resultCb.accept(true);
            });

            overlay.getChildren().add(wrapper);
        });
    }

    
    private Button styledBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle(String.format("-fx-background-color:%s; -fx-text-fill:white; -fx-font-weight:600; -fx-background-radius:8;", color));
        return b;
    }
}
