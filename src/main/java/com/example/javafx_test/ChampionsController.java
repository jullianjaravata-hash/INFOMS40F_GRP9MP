package com.example.javafx_test;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import java.sql.*;

public class ChampionsController{
    static Connection conn;

    @FXML
    public void initialize(){
        connectDatabase();
        getChampions();
        setChampionTable();
    }

    @FXML
    private VBox championsMain;
    ObservableList<Champion> champions = FXCollections.observableArrayList();

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
    public void addChampions(){
        championsMain.setVisible(false);
        addChampionView.setVisible(true);
    }

    @FXML
    public void returnMainView(){
        addChampionView.setVisible(false);
        championsMain.setVisible(true);
    }
}

