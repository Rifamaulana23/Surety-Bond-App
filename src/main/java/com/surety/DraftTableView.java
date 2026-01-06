package com.surety;

import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class DraftTableView {

    public void show(Consumer<Long> onEditId) {
        System.out.println("=== OPENING TABLEVIEW (DraftTableView) ===");

        Stage stage = new Stage();
        stage.setTitle("Daftar Draft Jaminan (Table)");

        TableView<DraftRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ===== Kolom =====
        TableColumn<DraftRow, String> colPerusahaan = new TableColumn<>("Nama Perusahaan");
        colPerusahaan.setCellValueFactory(new PropertyValueFactory<>("principal"));

        TableColumn<DraftRow, Double> colNilai = new TableColumn<>("Nilai Jaminan");
        colNilai.setCellValueFactory(new PropertyValueFactory<>("nilaiJaminan"));
        colNilai.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(CurrencyUtil.rupiah(Math.round(value)));
                }
            }
        });

        TableColumn<DraftRow, String> colPekerjaan = new TableColumn<>("Pekerjaan");
        colPekerjaan.setCellValueFactory(new PropertyValueFactory<>("pekerjaan"));

        TableColumn<DraftRow, String> colJenis = new TableColumn<>("Jenis Jaminan");
        colJenis.setCellValueFactory(new PropertyValueFactory<>("jenis"));

        table.getColumns().addAll(colPerusahaan, colNilai, colPekerjaan, colJenis);

        // ===== Tombol =====
        Button btnRefresh = new Button("Refresh");
        Button btnEdit = new Button("Edit");
        Button btnDelete = new Button("Delete");

        btnRefresh.setOnAction(e -> load(table));

        btnEdit.setOnAction(e -> {
            DraftRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Pilih draft dulu yang mau diedit.").showAndWait();
                return;
            }
            if (onEditId != null) {
                onEditId.accept(selected.getId()); // kirim ID ke Main
            }
            stage.close(); // opsional: tutup tabel setelah pilih edit
        });

        btnDelete.setOnAction(e -> {
            DraftRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Pilih draft dulu yang mau dihapus.").showAndWait();
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Konfirmasi Hapus");
            confirm.setHeaderText("Yakin mau hapus draft ini?");
            confirm.setContentText(
                    "ID: " + selected.getId() + "\n" +
                    "Principal: " + selected.getPrincipal()
            );

            confirm.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    try {
                        Database.deleteById(selected.getId());
                        load(table); // refresh table setelah delete
                        new Alert(Alert.AlertType.INFORMATION, "Draft berhasil dihapus.").showAndWait();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "Gagal menghapus draft.").showAndWait();
                    }
                }
            });
        });

        // Optional: double click row = edit
        table.setRowFactory(tv -> {
            TableRow<DraftRow> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    DraftRow selected = row.getItem();
                    if (onEditId != null) onEditId.accept(selected.getId());
                    stage.close();
                }
            });
            return row;
        });

        HBox top = new HBox(10, btnRefresh, btnEdit, btnDelete);
        top.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(table);

        stage.setScene(new Scene(root, 950, 520));
        stage.show();

        load(table);
    }

    private void load(TableView<DraftRow> table) {
        try {
            ObservableList<DraftRow> items =
                    FXCollections.observableArrayList(Database.getAllDraftRows());
            table.setItems(items);
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal load draft dari database.").showAndWait();
        }
    }
}
