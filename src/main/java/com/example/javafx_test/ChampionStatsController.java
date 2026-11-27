package com.example.javafx_test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.Optional;

/**
 * Champion Stats tab: add, update, delete, list all, and view with filter (by lane)
 * for the Champion_Patch_Stats table.
 */
public class ChampionStatsController {
    static Connection conn;

    private static final String[] LANES = {"Top", "Jungle", "Mid", "Bot", "Support"};
    private static final String ALL_LANES = "All Lanes";

    // ---- views ----
    @FXML
    private VBox statsMain;

    @FXML
    private VBox statsFormView;

    @FXML
    private Label recordCount;

    @FXML
    private Label formTitle;

    // ---- table + filter ----
    @FXML
    private ComboBox<String> laneFilter;

    @FXML
    private TableView<ChampionStats> statsTable;

    @FXML
    private TableColumn<ChampionStats, Integer> colCode;

    @FXML
    private TableColumn<ChampionStats, String> colChampion;

    @FXML
    private TableColumn<ChampionStats, String> colPatch;

    @FXML
    private TableColumn<ChampionStats, String> colLane;

    @FXML
    private TableColumn<ChampionStats, Integer> colGames;

    @FXML
    private TableColumn<ChampionStats, Integer> colWins;

    @FXML
    private TableColumn<ChampionStats, Integer> colBans;

    @FXML
    private TableColumn<ChampionStats, String> colWinRate;

    // ---- add / update form ----
    @FXML
    private ComboBox<ChampOption> championBox;

    @FXML
    private ComboBox<String> patchBox;

    @FXML
    private ComboBox<String> laneBox;

    @FXML
    private TextField gamesField;

    @FXML
    private TextField winsField;

    @FXML
    private TextField bansField;

    ObservableList<ChampionStats> stats = FXCollections.observableArrayList();

    /** The record being edited, or null while adding a new one. */
    private ChampionStats editing;

    /** A champion shown by name in the drop-down but identified by its code. */
    private static class ChampOption {
        final int code;
        final String name;

        ChampOption(int code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @FXML
    public void initialize() {
        connectDatabase();
        setStatsTable();
        setLaneBoxes();
        getStats();
    }

    @FXML
    public void connectDatabase() {
        // Use the same connection details as the other tabs.
        String url = "jdbc:mysql://localhost:3306/leaguechampreport";
        String user = "root";
        String password = "NissanSkylineR34";

        try {
            if (conn != null && !conn.isClosed()) {
                return;
            }
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setStatsTable() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colChampion.setCellValueFactory(new PropertyValueFactory<>("championName"));
        colPatch.setCellValueFactory(new PropertyValueFactory<>("patchNumber"));
        colLane.setCellValueFactory(new PropertyValueFactory<>("lane"));
        colGames.setCellValueFactory(new PropertyValueFactory<>("gamesPlayed"));
        colWins.setCellValueFactory(new PropertyValueFactory<>("wins"));
        colBans.setCellValueFactory(new PropertyValueFactory<>("bans"));
        colWinRate.setCellValueFactory(new PropertyValueFactory<>("winRate"));

        statsTable.setItems(stats);
    }

    private void setLaneBoxes() {
        laneFilter.getItems().add(ALL_LANES);
        laneFilter.getItems().addAll(LANES);
        laneFilter.setValue(ALL_LANES);

        laneBox.getItems().addAll(LANES);
    }

    // ---- list all / view with filter ----
    @FXML
    public void getStats() {
        stats.clear();
        if (conn == null) {
            recordCount.setText("Not connected to the database");
            return;
        }

        String query = "SELECT s.stat_code, s.champion_code, c.champion_name, s.patch_number, s.lane, "
                + "s.games_played, s.wins, s.bans "
                + "FROM Champion_Patch_Stats s "
                + "JOIN Champion c ON c.champion_code = s.champion_code "
                + "JOIN Patch p ON p.patch_number = s.patch_number ";

        String lane = laneFilter.getValue();
        boolean filtered = lane != null && !lane.equals(ALL_LANES);
        if (filtered) {
            query += "WHERE s.lane = ? ";
        }
        query += "ORDER BY p.release_date DESC, c.champion_name, s.lane";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            if (filtered) {
                ps.setString(1, lane);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.add(new ChampionStats(rs.getInt("stat_code"), rs.getInt("champion_code"),
                            rs.getString("champion_name"), rs.getString("patch_number"),
                            rs.getString("lane"), rs.getInt("games_played"),
                            rs.getInt("wins"), rs.getInt("bans")));
                }
            }
        } catch (SQLException e) {
            showError("Database Error: " + e.getMessage());
        }
        recordCount.setText(stats.size() + " Records");
    }

    @FXML
    public void filterStats() {
        getStats();
    }

    // ---- add ----
    @FXML
    public void addStats() {
        editing = null;
        formTitle.setText("Add Champion Stats");
        loadChampionAndPatchChoices();
        championBox.setValue(null);
        patchBox.setValue(null);
        laneBox.setValue(null);
        gamesField.clear();
        winsField.clear();
        bansField.clear();
        showForm();
    }

    // ---- update ----
    @FXML
    public void updateStats() {
        ChampionStats selected = statsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a row in the table first.");
            return;
        }

        editing = selected;
        formTitle.setText("Update Champion Stats");
        loadChampionAndPatchChoices();

        for (ChampOption option : championBox.getItems()) {
            if (option.code == selected.getChampionCode()) {
                championBox.setValue(option);
            }
        }
        patchBox.setValue(selected.getPatchNumber());
        laneBox.setValue(selected.getLane());
        gamesField.setText(String.valueOf(selected.getGamesPlayed()));
        winsField.setText(String.valueOf(selected.getWins()));
        bansField.setText(String.valueOf(selected.getBans()));
        showForm();
    }

    // ---- save (used by both add and update) ----
    @FXML
    public void saveStats() {
        if (conn == null) {
            showError("Not connected to the database.");
            return;
        }

        ChampOption champion = championBox.getValue();
        String patch = patchBox.getValue();
        String lane = laneBox.getValue();

        if (champion == null) {
            showError("Please select a champion.");
            return;
        }
        if (patch == null) {
            showError("Please select a patch.");
            return;
        }
        if (lane == null) {
            showError("Please select a lane.");
            return;
        }

        int games;
        int wins;
        int bans;
        try {
            games = parseCount(gamesField, "Games Played");
            wins = parseCount(winsField, "Wins");
            bans = parseCount(bansField, "Bans");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }

        if (wins > games) {
            showError("Wins cannot be more than games played.");
            return;
        }

        int editCode = (editing == null) ? 0 : editing.getCode();

        try {
            // one row per champion + patch + lane
            String check = "SELECT 1 FROM Champion_Patch_Stats "
                    + "WHERE champion_code = ? AND patch_number = ? AND lane = ? AND stat_code <> ?";
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setInt(1, champion.code);
                ps.setString(2, patch);
                ps.setString(3, lane);
                ps.setInt(4, editCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        showError("Stats for this champion, patch and lane already exist. "
                                + "Update that record instead.");
                        return;
                    }
                }
            }

            if (editing == null) {
                String insert = "INSERT INTO Champion_Patch_Stats "
                        + "(champion_code, patch_number, lane, games_played, wins, bans) "
                        + "VALUES (?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    ps.setInt(1, champion.code);
                    ps.setString(2, patch);
                    ps.setString(3, lane);
                    ps.setInt(4, games);
                    ps.setInt(5, wins);
                    ps.setInt(6, bans);
                    ps.executeUpdate();
                }
            } else {
                String update = "UPDATE Champion_Patch_Stats SET champion_code = ?, patch_number = ?, "
                        + "lane = ?, games_played = ?, wins = ?, bans = ? WHERE stat_code = ?";
                try (PreparedStatement ps = conn.prepareStatement(update)) {
                    ps.setInt(1, champion.code);
                    ps.setString(2, patch);
                    ps.setString(3, lane);
                    ps.setInt(4, games);
                    ps.setInt(5, wins);
                    ps.setInt(6, bans);
                    ps.setInt(7, editCode);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            showError("Database Error: " + e.getMessage());
            return;
        }

        getStats();
        returnMainView();
    }

    // ---- delete ----
    @FXML
    public void deleteStats() {
        ChampionStats selected = statsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a row in the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete the " + selected.getLane() + " stats of " + selected.getChampionName()
                        + " for patch " + selected.getPatchNumber() + "?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> answer = confirm.showAndWait();
        if (answer.isEmpty() || answer.get() != ButtonType.YES) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM Champion_Patch_Stats WHERE stat_code = ?")) {
            ps.setInt(1, selected.getCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            showError("Database Error: " + e.getMessage());
            return;
        }
        getStats();
    }

    // ---- switching between the table view and the form view ----
    @FXML
    public void returnMainView() {
        statsFormView.setVisible(false);
        statsMain.setVisible(true);
    }

    private void showForm() {
        statsMain.setVisible(false);
        statsFormView.setVisible(true);
    }

    // ---- helpers ----
    /** Fills the champion and patch drop-downs from the database (other tabs may have added rows). */
    private void loadChampionAndPatchChoices() {
        championBox.getItems().clear();
        patchBox.getItems().clear();
        if (conn == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT champion_code, champion_name FROM Champion ORDER BY champion_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                championBox.getItems().add(new ChampOption(rs.getInt("champion_code"),
                        rs.getString("champion_name")));
            }
        } catch (SQLException e) {
            showError("Database Error: " + e.getMessage());
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT patch_number FROM Patch ORDER BY release_date DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                patchBox.getItems().add(rs.getString("patch_number"));
            }
        } catch (SQLException e) {
            showError("Database Error: " + e.getMessage());
        }
    }

    private int parseCount(TextField field, String label) {
        String text = field.getText() == null ? "" : field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a whole number.");
        }
        if (value < 0) {
            throw new IllegalArgumentException(label + " cannot be negative.");
        }
        return value;
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
