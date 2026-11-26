package com.example.javafx_test;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import java.io.IOException;
import java.sql.*;

public class MainController {
    @FXML
    private BorderPane mainTab;


    @FXML
    public void switchToChampions(){
        loadTab("ChampionsTab.fxml");
    }

    @FXML
    public void switchToAbilities(){
        loadTab("AbilitiesTab.fxml");
    }

    @FXML
    public void switchToStats(){
        loadTab("ChampionStatsTab.fxml");
    }

    private void loadTab(String fxmlFile){
        try {
            Parent tab = FXMLLoader.load(getClass().getResource(fxmlFile));
            mainTab.setCenter(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
