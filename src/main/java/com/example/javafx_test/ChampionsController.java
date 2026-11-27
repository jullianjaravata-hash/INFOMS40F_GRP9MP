package com.example.javafx_test;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import java.sql.*;
import java.util.Optional;

public class ChampionsController{
    static Connection conn;

    @FXML
    public void initialize(){
        connectDatabase();
        getChampions();
        setChampionTable();
        championClass.getItems().addAll("Fighter", "Tank", "Mage", "Support", "Assasin", "Marksman");
    }

    @FXML
    private VBox championsMain;
    ObservableList<Champion> champions = FXCollections.observableArrayList();

    @FXML
    private TextField championName;

    @FXML
    private TextField championTitle;

    @FXML
    private TextField championResource;

    @FXML
    private TextField championHP;

    @FXML
    private TextField championRange;

    @FXML
    private TextField championRelease;

    @FXML
    private ComboBox <String> championClass;

    @FXML
    ToggleButton championActive;

    @FXML
    private VBox addChampionView;


    @FXML
    public void connectDatabase(){
        String url = "jdbc:mysql://localhost:3306/leaguechampreport";
        String user = "root";
        String password = "NissanSkylineR34";

        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private TableView<Champion> championTable;

    @FXML
    private TableColumn<Champion, Integer> Code;

    @FXML
    private TableColumn<Champion, String> Name;

    @FXML
    private TableColumn<Champion, String> Title;

    @FXML
    private TableColumn<Champion, String> Class;

    @FXML
    private TableColumn<Champion, String> Resource;

    @FXML
    private TableColumn<Champion, Integer> HP;

    @FXML
    private TableColumn<Champion, Integer> Range;

    @FXML
    private TableColumn<Champion, Boolean> Active;

    @FXML
    public void getChampions(){
        String query = "SELECT champion_code, champion_name, title, class" +
                ", resource_type, base_HP, attack_range, release_date, active " +
                "FROM Champion";

        try {
            PreparedStatement ps = conn.prepareStatement (query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                champions.add(new Champion(rs.getInt("champion_code"), rs.getString("champion_name"), rs.getString("title"),
                                    rs.getString("class"), rs.getString("resource_type"), rs.getInt("base_HP"),
                                    rs.getInt("attack_range"), rs.getString("release_date"), rs.getBoolean("active")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setChampionTable(){
        Code.setCellValueFactory(new PropertyValueFactory<>("code"));
        Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        Title.setCellValueFactory(new PropertyValueFactory<>("title"));
        Class.setCellValueFactory(new PropertyValueFactory<>("champClass"));
        Resource.setCellValueFactory(new PropertyValueFactory<>("resource"));
        HP.setCellValueFactory(new PropertyValueFactory<>("hp"));
        Range.setCellValueFactory(new PropertyValueFactory<>("range"));
        Active.setCellValueFactory(new PropertyValueFactory<>("active"));

        championTable.setItems(champions);
    }

    @FXML
    public void refreshChampionTable(){
        champions.clear();
        getChampions();
        setChampionTable();
    }

    @FXML
    public void addChampions(){
        championsMain.setVisible(false);
        addChampionView.setVisible(true);
    }

    @FXML
    public void returnMainView(){
        emptyAddChamp();
        addChampionView.setVisible(false);
        championsMain.setVisible(true);
    }

    @FXML
    public void saveChampion(){
        String query = "INSERT INTO Champion (champion_name, title, class" +
                ",resource_type, base_HP, attack_range, release_date, active)" +
                "VALUES (?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, championName.getText());
            ps.setString(2, championTitle.getText());
            ps.setString(3, championClass.getValue());
            ps.setString(4, championResource.getText());
            ps.setInt(5, Integer.parseInt(championHP.getText()));
            ps.setInt(6, Integer.parseInt(championRange.getText()));
            ps.setDate(7, java.sql.Date.valueOf(championRelease.getText()));
            ps.setBoolean(8, championActive.isSelected());
            int rowsInserted = ps.executeUpdate();
        } catch(SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }

        refreshChampionTable();
        returnMainView();
    }

    @FXML
    public void emptyAddChamp(){
        championName.clear();
        championTitle.clear();
        championClass.getSelectionModel().clearSelection();
        championResource.clear();
        championHP.clear();
        championRange.clear();
        championRelease.clear();
        championActive.setSelected(false);
    }

    @FXML
    public void deleteChampion(){
        Champion champion = championTable.getSelectionModel().getSelectedItem();

        if (champion == null) {
            showError("Select a row in the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete the champion " + champion.getName() +
                " From the table?",
                ButtonType.YES, ButtonType.NO);
        confirm.initOwner(championsMain.getScene().getWindow());
        Optional<ButtonType> answer = confirm.showAndWait();
        if (answer.isEmpty() || answer.get() != ButtonType.YES) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM Champion WHERE champion_code = ?")) {
            ps.setInt(1, champion.getCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            showError("Database Error: " + e.getMessage());
            return;
        }
        refreshChampionTable();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(championsMain.getScene().getWindow());
        alert.showAndWait();
    }

}

