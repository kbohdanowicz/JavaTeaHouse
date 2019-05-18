package dev;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private String secretCode = "";

    private HBox hbTea;
    private HBox hbHerbs;
    private HBox hbMate;

    private HBox hbButtons;
    private HBox hbButtonLabels;

    private HBox hbFoot;
    private VBox vbInvoice;

    private VBox vbMain;

    private Label labPrice;

    private Label labBlackTea;
    private Label labGreenTea;
    private Label labWhiteTea;
    private Label labCamomile;
    private Label labMint;
    private Label labSage;
    private Label labYerbaArgentine;
    private Label labYerbaUruguayan;
    private Label labYerbaParaguayan;

    private Label labInvoice;

    private Scene sceneMain;
    private Scene sceneInvoice;

    private DecimalFormat df = new DecimalFormat("0.00");

    private ComboBox<Integer> comboBox;
    private Warehouse warehouse = new Warehouse();

    private ObservableList<Item> cartList = FXCollections.observableArrayList();
    private TableView<Item> table;

    //CLASSES///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class Warehouse{

        int blackTea = 12;
        int greenTea = 7;
        int whiteTea = 8;
        int yerbaArgentine = 17;
        int yerbaUruguayan = 15;
        int yerbaParaguayan = 25;
        int camomile = 35;
        int mint = 32;
        int sage = 16;

        int ogBlackTea = blackTea;
        int ogGreenTea = greenTea;
        int ogWhiteTea = whiteTea;
        int ogYerbaArgentine = yerbaArgentine;
        int ogYerbaUruguayan = yerbaUruguayan;
        int ogYerbaParaguayan = yerbaParaguayan;
        int ogCamomile = camomile;
        int ogMint = mint;
        int ogSage = sage;

        void setOriginalValues() {
            blackTea = ogBlackTea;
            greenTea = ogGreenTea;
            whiteTea = ogWhiteTea;
            yerbaArgentine = ogYerbaArgentine;
            yerbaUruguayan = ogYerbaUruguayan;
            yerbaParaguayan = ogYerbaParaguayan;
            camomile = ogCamomile;
            mint = ogMint;
            sage = ogSage;
        }
    }

    public class Item {
        String name;
        double price;
        int id;
        int quantity;
        String value;

        Item(String name, double price, int id, int quantity) {
            this.name = name;
            this.price = price;
            this.id = id;
            this.quantity = quantity;
            value = getDottedString(df.format(price * quantity));
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getId() {
            return id;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getValue() {
            return value;
        }
    }

    public class QueryData {
        String query;
        String email;

        QueryData(String query, String email) {
            this.query = query;
            this.email = email;
        }

        public String getQuery() {
            return query;
        }

        public String getEmail() {
            return email;
        }
    }

    //ALERTS////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void showInfoDodanoProdukt(ComboBox<Integer> comboBox) {
        Alert dg = new Alert(Alert.AlertType.INFORMATION);
        dg.setTitle("");
        dg.setHeaderText("");
        dg.setResizable(false);
        if (comboBox.getValue() != 1)
            dg.setContentText("Dodano produkty do koszyka!");
        else
            dg.setContentText("Dodano produkt do koszyka!");
        dg.show();
    }

    private void showWarningBrakTowaru() {
        Alert dg = new Alert(Alert.AlertType.ERROR);
        dg.setTitle("Błąd");
        dg.setHeaderText("");
        dg.setResizable(false);
        dg.setContentText("Brak w magazynie");
        dg.show();
    }

    private void showWarningWybierzIlosc() {
        Alert dg = new Alert(Alert.AlertType.WARNING);
        dg.setTitle("");
        dg.setHeaderText("");
        dg.setResizable(false);
        dg.setContentText("Wybierz ilość produktów");
        dg.show();
    }

    //METHODS///////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String getCartSum(){
        double dubSum = 0;
        for (Item item: cartList) {
                dubSum += Double.parseDouble(item.value);
        }
        return df.format(dubSum);
    }

    private String getDottedString(String str){
        return str.replace(',', '.');
    }

    private int getCartSize(){
        int size = 0;
        for (Item item: cartList)
            size += item.quantity;
        return size;
    }

    private String getLabPriceText(){
        return getCartSum() + " PLN / " + getCartSize() + " szt.";
    }

    private TableView<Item> getInvoice() {
        ObservableList<Item> newList = cartList;
        newList.sort((o1, o2) -> String.valueOf(o1.name).compareTo(o2.name));
        //Removes duplicates                                                                            czemu dziala?
        for (int i = 0; i < newList.size(); i++) {
            for (int j = i + 1; j < newList.size(); j++) {
                if (newList.get(i).id == newList.get(j).id) {
                    newList.get(i).quantity += newList.get(j).quantity;
                    newList.remove(j);
                    j--;
                }
            }
        }
        TableColumn<Item, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setMinWidth(30);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setStyle( "-fx-alignment: CENTER;");

        TableColumn<Item, String> nameColumn = new TableColumn<>("Nazwa Towaru");
        nameColumn.setMinWidth(140);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");

        TableColumn<Item, Integer> quantityColumn = new TableColumn<>("Ilość");
        quantityColumn.setMinWidth(50);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");

        TableColumn<Item, Double> priceColumn = new TableColumn<>("Cena");
        priceColumn.setMinWidth(40);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");

        TableColumn<Item, String> valueColumn = new TableColumn<>("Wartość");
        valueColumn.setMinWidth(70);
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");

        table = new TableView<>();
        table.setMaxSize(339,242);
        table.setItems(newList);
        table.getColumns().addAll(Arrays.asList(idColumn, nameColumn, quantityColumn, priceColumn, valueColumn));
        return table;
    }

    private void switchButton(int id){
        switch (id) {
            case 1:
                if (comboBox.getValue() != null) {
                    if (warehouse.blackTea - comboBox.getValue() >= 0) {
                        if (warehouse.blackTea != 0) {
                            cartList.add(new Item(strBlackTea.substring(0, 19), blackTeaPrice, 1, comboBox.getValue()));
                            warehouse.blackTea -= comboBox.getValue();
                            labBlackTea.setText(strBlackTea + warehouse.blackTea + " szt.");
                            labPrice.setText(getLabPriceText());
                            showInfoDodanoProdukt(comboBox);
                        }else showWarningBrakTowaru();
                    }else showWarningBrakTowaru();
                }else showWarningWybierzIlosc();
                break;
            case 2:
                if (comboBox.getValue() != null) {
                    if (warehouse.greenTea - comboBox.getValue() >= 0) {
                        if (warehouse.greenTea != 0) {
                            cartList.add(new Item(strGreenTea.substring(0, 20), greenTeaPrice, 2, comboBox.getValue()));
                            warehouse.greenTea -= comboBox.getValue();
                            labGreenTea.setText(strGreenTea + warehouse.greenTea + " szt.");
                            labPrice.setText(getCartSum() + " PLN / " + getCartSize() + " szt.");
                            showInfoDodanoProdukt(comboBox);
                        }else showWarningBrakTowaru();
                    }else showWarningBrakTowaru();
                }else showWarningWybierzIlosc();
                break;
            case 3:
                if (comboBox.getValue() != null) {
                    if (warehouse.whiteTea - comboBox.getValue() >= 0) {
                        if (warehouse.whiteTea != 0) {
                            cartList.add(new Item(strWhiteTea.substring(0, 17), whiteTeaPrice, 3, comboBox.getValue()));
                            warehouse.whiteTea -= comboBox.getValue();
                            labWhiteTea.setText(strWhiteTea + warehouse.whiteTea + " szt.");
                            labPrice.setText(getCartSum() + " PLN / " + getCartSize() + " szt.");
                            showInfoDodanoProdukt(comboBox);
                        }else showWarningBrakTowaru();
                    }else showWarningBrakTowaru();
                }else showWarningWybierzIlosc();
                break;
            case 4:
                if (comboBox.getValue() != null) {
                    if (warehouse.camomile - comboBox.getValue() >= 0) {
                        if (warehouse.camomile != 0) {
                            cartList.add(new Item(strCamomile.substring(0, 12), camomilePrice, 4, comboBox.getValue()));
                            warehouse.camomile -= comboBox.getValue();
                            labCamomile.setText(strCamomile + warehouse.camomile + " szt.");
                            labPrice.setText(getCartSum() + " PLN / " + getCartSize() + " szt.");
                            showInfoDodanoProdukt(comboBox);
                        }else showWarningBrakTowaru();
                    }else showWarningBrakTowaru();
                }else showWarningWybierzIlosc();
                break;
            case 5:
                if (comboBox.getValue() != null) {
                    if (warehouse.mint - comboBox.getValue() >= 0) {
                        if (warehouse.mint != 0) {
                            cartList.add(new Item(strMint.substring(0, 9), mintPrice, 5, comboBox.getValue()));
                            warehouse.mint -= comboBox.getValue();
                            labMint.setText(strMint + warehouse.mint + " szt.");
                            labPrice.setText(getCartSum() + " PLN / " + getCartSize() + " szt.");
                            showInfoDodanoProdukt(comboBox);
                        }else showWarningBrakTowaru();
                    }else showWarningBrakTowaru();
                }else showWarningWybierzIlosc();
                break;
            case 6:
                if (comboBox.getValue() != null) {
                    if (warehouse.sage - comboBox.getValue() >= 0) {
                        if (warehouse.sage != 0) {
                            cartList.add(new Item(strSage.substring(0, 11), sagePrice, 6, comboBox.getValue()));
                            warehouse.sage -= comboBox.getValue();
                            labSage.setText(strSage + warehouse.sage + " szt.");
                            labPrice.setText(getCartSum() + " PLN / " + getCartSize() + " szt.");
                            showInfoDodanoProdukt(comboBox);
                        }else showWarningBrakTowaru();
                    }else showWarningBrakTowaru();
                }else showWarningWybierzIlosc();
                break;
            case 7:
                if (comboBox.getValue() != null) {
                    if (warehouse.yerbaArgentine - comboBox.getValue() >= 0) {
                        if (warehouse.yerbaArgentine != 0) {
                            cartList.add(new Item(strYerbaArgentine.substring(0, 23), yerbaArgentinePrice, 7, comboBox.getValue()));
                            warehouse.yerbaArgentine -= comboBox.getValue();
                            labYerbaArgentine.setText(strYerbaArgentine + warehouse.yerbaArgentine + " szt.");
                            labPrice.setText(getCartSum() + " PLN / " + getCartSize() + " szt.");
                            showInfoDodanoProdukt(comboBox);
                        }else showWarningBrakTowaru();
                    }else showWarningBrakTowaru();
                }else showWarningWybierzIlosc();
                break;
            case 8:
                if (comboBox.getValue() != null) {
                    if (warehouse.yerbaUruguayan - comboBox.getValue() >= 0) {
                        if (warehouse.yerbaUruguayan != 0) {
                            cartList.add(new Item(strYerbaUruguayan.substring(0, 21), yerbaUruguayanPrice, 8, comboBox.getValue()));
                            warehouse.yerbaUruguayan -= comboBox.getValue();
                            labYerbaUruguayan.setText(strYerbaUruguayan + warehouse.yerbaUruguayan + " szt.");
                            labPrice.setText(getCartSum() + " PLN / " + getCartSize() + " szt.");
                            showInfoDodanoProdukt(comboBox);
                        }else showWarningBrakTowaru();
                    }else showWarningBrakTowaru();
                }else showWarningWybierzIlosc();
                break;
            case 9:
                if (comboBox.getValue() != null) {
                    if (warehouse.yerbaParaguayan - comboBox.getValue() >= 0) {
                        if (warehouse.yerbaParaguayan != 0) {
                            cartList.add(new Item(strYerbaParaguayan.substring(0, 21), yerbaParaguayanPrice, 9, comboBox.getValue()));
                            warehouse.yerbaParaguayan -= comboBox.getValue();
                            labYerbaParaguayan.setText(strYerbaParaguayan + warehouse.yerbaParaguayan + " szt.");
                            labPrice.setText(getCartSum() + " PLN / " + getCartSize() + " szt.");
                            showInfoDodanoProdukt(comboBox);
                        }else showWarningBrakTowaru();
                    }else showWarningBrakTowaru();
                }else showWarningWybierzIlosc();
                break;
        }
    }

    //CONSTANTS/////////////////////////////////////////////////////////////////////////////////////////////////////////
    private final double  blackTeaPrice = 9.99, greenTeaPrice = 11.99, whiteTeaPrice = 8.99,
            camomilePrice = 3.99, mintPrice = 4.99, sagePrice = 5.99,
            yerbaArgentinePrice = 19.99, yerbaUruguayanPrice = 35.99, yerbaParaguayanPrice = 13.99;

    private String  strBlackTea = "Czarna Herbata 125g\n" + df.format(blackTeaPrice) + " zł\nIlość: ",
            strGreenTea = "Zielona Herbata 100g\n" + df.format(greenTeaPrice) + " zł\nIlość: ",
            strWhiteTea = "Biała Herbata 45g\n" + df.format(whiteTeaPrice) + " zł\nIlość: ",
            strCamomile = "Rumianek 40g\n" + df.format(camomilePrice) + " zł\nIlość: ",
            strMint = "Mięta 40g\n" + df.format(mintPrice) + " zł\nIlość: ",
            strSage = "Szałwia 40g\n" + df.format(sagePrice) + " zł\nIlość: ",
            strYerbaArgentine = "Yerba Mate Taragui 500g\n" + df.format(yerbaArgentinePrice) + " zł\nIlość: ",
            strYerbaUruguayan = "Yerba Mate Amanda 1kg\n" + df.format(yerbaUruguayanPrice) + " zł\nIlość: ",
            strYerbaParaguayan = "Yerba Mate Green 200g\n" + df.format(yerbaParaguayanPrice) + " zł\nIlość: ";

    @Override
    public void start(Stage primaryStage) {

        //LABELS////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*
        System.out.println(strBlackTea.substring(0, 19));
        System.out.println(strGreenTea.substring(0, 20));
        System.out.println(strWhiteTea.substring(0, 17));

        System.out.println(strCamomile.substring(0, 12));
        System.out.println(strMint.substring(0, 9));
        System.out.println(strSage.substring(0, 11));

        System.out.println(strYerbaArgentine.substring(0, 23));
        System.out.println(strYerbaUruguayan.substring(0, 21));
        System.out.println(strYerbaParaguayan.substring(0, 21));
*/
        labBlackTea = new Label(strBlackTea + warehouse.blackTea + " szt.");
        labGreenTea = new Label(strGreenTea + warehouse.greenTea + " szt.");
        labWhiteTea = new Label(strWhiteTea + warehouse.whiteTea + " szt.");
        labCamomile = new Label(strCamomile + warehouse.camomile + " szt.");
        labMint = new Label(strMint + warehouse.mint + " szt.");
        labSage = new Label(strSage + warehouse.sage + " szt.");
        labYerbaArgentine = new Label(strYerbaArgentine + warehouse.yerbaArgentine + " szt.");
        labYerbaUruguayan = new Label(strYerbaUruguayan + warehouse.yerbaUruguayan + " szt.");
        labYerbaParaguayan = new Label( strYerbaParaguayan+ warehouse.yerbaParaguayan + " szt.");

        //HEADER////////////////////////////////////////////////////////////////////////////////////////////////////////
        Label labShopName = new Label("Herba Market");
        labShopName.setMinWidth(560);
        labShopName.setId("labShopName");

        labPrice = new Label(getLabPriceText());
        labPrice.setId("labPrice");

        Button btnClear = new Button("Opróżnij");
        btnClear.setMinSize(90,30);
        btnClear.setId("btnClear");
        btnClear.setOnAction(e -> {
            cartList.clear();
            labPrice.setText(getLabPriceText());
            warehouse.setOriginalValues();

            labBlackTea.setText(strBlackTea + warehouse.blackTea + " szt.");
            labGreenTea.setText(strGreenTea + warehouse.greenTea + " szt.");
            labWhiteTea.setText(strWhiteTea + warehouse.whiteTea + " szt.");
            labCamomile.setText(strCamomile + warehouse.camomile + " szt.");
            labMint.setText(strMint + warehouse.mint + " szt.");
            labSage.setText(strSage + warehouse.sage + " szt.");
            labYerbaArgentine.setText(strYerbaArgentine + warehouse.yerbaArgentine + " szt.");
            labYerbaUruguayan.setText(strYerbaUruguayan + warehouse.yerbaUruguayan + " szt.");
            labYerbaParaguayan.setText(strYerbaParaguayan + warehouse.yerbaParaguayan + " szt.");
        });

        VBox vbTopRight = new VBox(20);
        vbTopRight.setMinWidth(150);
        vbTopRight.setAlignment(Pos.CENTER_LEFT);
        vbTopRight.setPadding(new Insets(55,0,0,30));
        vbTopRight.getChildren().addAll(labPrice, btnClear);

        HBox hbHeader = new HBox();
        hbHeader.setAlignment(Pos.CENTER_LEFT);
        hbHeader.getChildren().addAll(new ImageView("/dev/res/logo.png"), labShopName,
                new ImageView("/dev/res/cart.png"), vbTopRight);

        //BANNER////////////////////////////////////////////////////////////////////////////////////////////////////////
        ImageView banner = new ImageView("/dev/res/banner.png");
        banner.setFitHeight(280);

        //CATEGORIES////////////////////////////////////////////////////////////////////////////////////////////////////
        comboBox = new ComboBox<>();
        comboBox.getItems().addAll(1,2,3,4,5,6,7,8,9,10);
        comboBox.setPromptText("Wybierz ilość");

        Button btnBack = new Button("Wróć");                                                            //BTN_BACK
        btnBack.setAlignment(Pos.TOP_LEFT);
        btnBack.setOnAction(e -> {
            hbTea.getChildren().remove(comboBox);
            hbHerbs.getChildren().remove(comboBox);
            hbMate.getChildren().remove(comboBox);
            comboBox.setValue(null);

            vbMain.getChildren().remove(hbTea);
            vbMain.getChildren().remove(hbHerbs);
            vbMain.getChildren().remove(hbMate);
            vbMain.getChildren().remove(btnBack);

            vbInvoice.getChildren().removeAll(table, btnBack, labInvoice);

            vbMain.getChildren().addAll(banner, hbButtons, hbButtonLabels, hbFoot);

            if (primaryStage.getScene() == sceneInvoice)
                primaryStage.setScene(sceneMain);
        });

        //BUTTON_TEA////////////////////////////////////////////////////////////////////////////////////////////////////
        ImageView imTeaBlack = new ImageView("/dev/res/blackTea.jpg");
        imTeaBlack.setFitWidth(200);
        imTeaBlack.setPreserveRatio(true);

        Button btnTeaBlack = new Button();
        btnTeaBlack.setGraphic(imTeaBlack);
        btnTeaBlack.setOnAction(e -> switchButton(1));

        ImageView imTeaGreen = new ImageView("/dev/res/greenTea.jpg");
        imTeaGreen.setFitWidth(200);
        imTeaGreen.setPreserveRatio(true);

        Button btnTeaGreen = new Button();
        btnTeaGreen.setGraphic(imTeaGreen);
        btnTeaGreen.setOnAction(e -> switchButton(2));

        ImageView imTeaWhite = new ImageView("/dev/res/whiteTea.jpg");
        imTeaWhite.setFitWidth(200);
        imTeaWhite.setPreserveRatio(true);

        Button btnTeaWhite = new Button();
        btnTeaWhite.setGraphic(imTeaWhite);
        btnTeaWhite.setOnAction(e -> switchButton(3));

        GridPane gpTea = new GridPane();
        gpTea.setVgap(15);
        gpTea.setHgap(30);

        gpTea.add(btnTeaBlack,0,0);
        gpTea.add(labBlackTea,1,0);
        gpTea.add(btnTeaGreen,0,1);
        gpTea.add(labGreenTea,1,1);
        gpTea.add(btnTeaWhite,0,2);
        gpTea.add(labWhiteTea,1,2);

        hbTea = new HBox(40);
        hbTea.setAlignment(Pos.TOP_CENTER);
        hbTea.setPadding(new Insets(0,200,0,0));
        hbTea.getChildren().add(gpTea);

        ImageView imBtnTea = new ImageView("/dev/res/btnTea.jpg");
        imBtnTea.setFitWidth(320);
        imBtnTea.setFitHeight(234);

        Button btnTea = new Button();
        btnTea.setGraphic(imBtnTea);

        btnTea.setOnAction(e -> {
            vbMain.getChildren().removeAll(hbFoot, hbButtons, hbButtonLabels, banner);
            hbTea.getChildren().add(comboBox);
            vbMain.getChildren().addAll(btnBack, hbTea);
        });

        //BUTTON_HERBS//////////////////////////////////////////////////////////////////////////////////////////////////
        ImageView imCamomile = new ImageView("/dev/res/camomile.jpg");
        imCamomile.setFitWidth(200);
        imCamomile.setPreserveRatio(true);

        Button btnCamomile = new Button();
        btnCamomile.setGraphic(imCamomile);
        btnCamomile.setOnAction(e -> switchButton(4));

        ImageView imMint = new ImageView("/dev/res/mint.jpg");
        imMint.setFitWidth(200);
        imMint.setPreserveRatio(true);

        Button btnMint = new Button();
        btnMint.setGraphic(imMint);
        btnMint.setOnAction(e -> switchButton(5));

        ImageView imSage = new ImageView("/dev/res/sage.jpg");
        imSage.setFitWidth(200);
        imSage.setPreserveRatio(true);

        Button btnSage = new Button();
        btnSage.setGraphic(imSage);
        btnSage.setOnAction(e -> switchButton(6));

        GridPane gpHerbs = new GridPane();
        gpHerbs.setVgap(15);
        gpHerbs.setHgap(30);
        gpHerbs.setAlignment(Pos.CENTER_LEFT);

        gpHerbs.add(btnCamomile,0,0);
        gpHerbs.add(labCamomile,1,0);
        gpHerbs.add(btnMint,0,1);
        gpHerbs.add(labMint,1,1);
        gpHerbs.add(btnSage,0,2);
        gpHerbs.add(labSage,1,2);

        hbHerbs = new HBox(78);
        hbHerbs.setAlignment(Pos.TOP_CENTER);
        hbHerbs.setPadding(new Insets(0,200,0,0));
        hbHerbs.getChildren().add(gpHerbs);

        ImageView imBtnHerbs = new ImageView("/dev/res/btnHerbs.jpg");
        imBtnHerbs.setFitWidth(320);
        imBtnHerbs.setFitHeight(234);

        Button btnHerbs = new Button();
        btnHerbs.setGraphic(imBtnHerbs);
        btnHerbs.setOnAction(e -> {
            vbMain.getChildren().removeAll(hbFoot, hbButtons, hbButtonLabels, banner);
            hbHerbs.getChildren().add(comboBox);
            vbMain.getChildren().addAll(btnBack, hbHerbs);
        });

        //BUTTON_YERBA//////////////////////////////////////////////////////////////////////////////////////////////////
        ImageView imYerbaArgentine = new ImageView("/dev/res/yerba1.jpg");
        imYerbaArgentine.setFitWidth(200);
        imYerbaArgentine.setPreserveRatio(true);

        Button btnYerbaArgentine = new Button();
        btnYerbaArgentine.setGraphic(imYerbaArgentine);
        btnYerbaArgentine.setOnAction(e -> switchButton(7));

        ImageView imYerbaUruguayan = new ImageView("/dev/res/yerba2.jpg");
        imYerbaUruguayan.setFitWidth(200);
        imYerbaUruguayan.setPreserveRatio(true);

        Button btnYerbaUruguayan = new Button();
        btnYerbaUruguayan.setGraphic(imYerbaUruguayan);
        btnYerbaUruguayan.setOnAction(e -> switchButton(8));

        ImageView imYerbaParaguayan = new ImageView("/dev/res/yerba3.jpg");
        imYerbaParaguayan.setFitWidth(200);
        imYerbaParaguayan.setPreserveRatio(true);

        Button btnYerbaParaguayan = new Button();
        btnYerbaParaguayan.setGraphic(imYerbaParaguayan);
        btnYerbaParaguayan.setOnAction(e -> switchButton(9));

        GridPane gpMate = new GridPane();
        gpMate.setVgap(15);
        gpMate.setHgap(30);
        gpMate.setAlignment(Pos.TOP_RIGHT);

        gpMate.add(btnYerbaArgentine,0,0);
        gpMate.add(labYerbaArgentine,1,0);
        gpMate.add(btnYerbaUruguayan,0,1);
        gpMate.add(labYerbaUruguayan,1,1);
        gpMate.add(btnYerbaParaguayan,0,2);
        gpMate.add(labYerbaParaguayan,1,2);

        hbMate = new HBox(20);
        hbMate.setAlignment(Pos.TOP_CENTER);
        hbMate.setPadding(new Insets(0,200,0,0));
        hbMate.getChildren().add(gpMate);

        ImageView imBtnYerba = new ImageView("/dev/res/btnYerba.jpg");

        Button btnYerba = new Button();
        btnYerba.setGraphic(imBtnYerba);
        btnYerba.setOnAction(e -> {
            vbMain.getChildren().removeAll(hbFoot, hbButtons, hbButtonLabels, banner);
            hbMate.getChildren().add(comboBox);
            vbMain.getChildren().addAll(btnBack, hbMate);
        });

        hbButtons = new HBox(30);
        hbButtons.getChildren().addAll(btnTea, btnHerbs, btnYerba);
        hbButtons.setAlignment(Pos.CENTER);

        //FOOT//////////////////////////////////////////////////////////////////////////////////////////////////////////
        TextField tfQueryInput = new TextField();
        tfQueryInput.setPrefWidth(550);

        tfQueryInput.setOnKeyTyped(e ->{
            if (tfQueryInput.getText().matches("[Nn][Ii]") && e.getCharacter().matches("[Gg]")) {
                Alert dg = new Alert(Alert.AlertType.WARNING);
                dg.setTitle("STOP RIGHT THERE CRIMINAL SCUM");
                dg.setHeaderText("");
                dg.setContentText("THAT'S RACIST YOU CAN'T SAY THE NWORD");
                dg.setResizable(false);
                dg.show();
            }
            else if (tfQueryInput.getText().matches("[Nn][Ii][Gg][Gg]") && e.getCharacter().matches("[Aa]") ||
                     tfQueryInput.getText().matches("[Nn][Ii][Gg][Gg][Ee]") && e.getCharacter().matches("[Rr]")) {
                Alert dg = new Alert(Alert.AlertType.WARNING);
                dg.setTitle("");
                dg.setHeaderText("MRS OBAMA GET DOWN!");
                dg.setContentText("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                dg.setResizable(false);
                dg.show();
                primaryStage.close();
            }
        });

        TextField tfEmailInput = new TextField();

        Button btnValidation = new Button("Wyślij");
        btnValidation.setOnAction(e -> {
            if(!tfQueryInput.getText().isEmpty()) {
                if (!tfEmailInput.getText().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]" +
                        "+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x" +
                        "0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|" +
                        "\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|" +
                        "[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\" +
                        "x09\\x0b\\x0c\\x0e-\\x7f])+)])")) {
                    Alert dg = new Alert(Alert.AlertType.ERROR);
                    dg.setTitle("Błąd");
                    dg.setHeaderText("");
                    dg.setResizable(false);
                    dg.setContentText("Podany email jest nieprawidłowy");
                    dg.show();
                }
                else {
                    try{
                        Connection con;
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;" +
                                "databaseName=*********;"+
                                "user=*********;password=*********;");
                        Statement zapyt = con.createStatement();
                        zapyt.executeUpdate("INSERT INTO QueryTable VALUES" +
                                            "('" + tfQueryInput.getText() + "','" + tfEmailInput.getText() + "')");
                        zapyt.close();
                        con.close();
                        Alert dg = new Alert(Alert.AlertType.INFORMATION);
                        dg.setTitle("");
                        dg.setHeaderText("");
                        dg.setResizable(false);
                        dg.setContentText("Wiadomość została wysłana!");
                        dg.show();
                    }
                    catch(SQLException sqle) {
                        System.out.println(sqle); }
                    catch(ClassNotFoundException d) {
                        System.out.println("Brak sterownika"); }
                }
            }
            else {
                Alert dg = new Alert(Alert.AlertType.WARNING);
                dg.setTitle("");
                dg.setHeaderText("");
                dg.setResizable(false);
                dg.setContentText("Pole \"zapytanie\" jest puste");
                dg.show();
            }
        });

        GridPane gpMail = new GridPane();
        gpMail.setAlignment(Pos.CENTER);
        gpMail.setVgap(20);
        gpMail.setHgap(20);

        gpMail.add(new Label("Zapytanie: "),0,0);
        gpMail.add(tfQueryInput,1,0);
        gpMail.add(new Label("Twój email: "),0,1);
        gpMail.add(tfEmailInput,1,1);
        gpMail.add(btnValidation,1,2);

        GridPane.setHalignment(btnValidation, HPos.CENTER);
        GridPane.setValignment(btnValidation, VPos.CENTER);

        ImageView mail = new ImageView("/dev/res/mail.png");

        final Stage queryStage = new Stage();
        queryStage.initModality(Modality.APPLICATION_MODAL);
        queryStage.initOwner(primaryStage);
        queryStage.setResizable(false);
        Scene queryStageScene = new Scene(gpMail, 700, 180);
        queryStage.setScene(queryStageScene);
        queryStage.setTitle("Zapytanie");

        mail.setOnMouseClicked(e -> {
            tfQueryInput.clear();
            tfEmailInput.clear();
            queryStage.show();
        });

        Label labMailUs = new Label("napisz do nas");
        labMailUs.setMinWidth(700);
        labMailUs.setId("labMailUs");

        //INVOICE
        Button btnInvoice = new Button("Faktura");
        btnInvoice.setMinSize(122,41);
        btnInvoice.setId("btnInvoice");

        labInvoice = new Label();
        labInvoice.setPadding(new Insets(0,0,0,242));

        vbInvoice = new VBox(15);
        vbInvoice.setAlignment(Pos.TOP_CENTER);
        vbInvoice.setPadding(new Insets(70,0,0,0));
                                                                                                          //BTN_INVOICE
        btnInvoice.setOnAction(e -> {
            if (cartList.size() == 0) {
                Alert dg = new Alert(Alert.AlertType.WARNING);
                dg.setTitle("");
                dg.setHeaderText("");
                dg.setResizable(false);
                dg.setContentText("Nie dodałeś żadnego produktu do koszyka");
                dg.show();
            }
            else {
                vbMain.getChildren().removeAll(banner, hbButtons, hbButtonLabels, hbFoot);
                labInvoice.setText("Razem: " + getDottedString(getCartSum()) + " zł");
                vbInvoice.getChildren().addAll(btnBack, getInvoice(), labInvoice);
                primaryStage.setScene(sceneInvoice);
            }
        });

        hbFoot = new HBox(20);
        hbFoot.setAlignment(Pos.CENTER_LEFT);
        hbFoot.setPadding(new Insets(0,0,0,30));
        hbFoot.getChildren().addAll(mail, labMailUs, btnInvoice);

        Label lbTea = new Label("Herbata");
        lbTea.getStyleClass().add("outlineUn");

        Label lbHerbs = new Label("Zioła");
        lbHerbs.getStyleClass().add("outlineDeux");

        Label lbMate = new Label("Yerba Mate");
        lbMate.getStyleClass().add("outlineTrois");

        hbButtonLabels = new HBox(287);
        hbButtonLabels.setAlignment(Pos.CENTER_LEFT);
        hbButtonLabels.setPadding(new Insets(0,0,0,140));
        hbButtonLabels.getChildren().addAll(lbTea, lbHerbs, lbMate);

        vbMain = new VBox(10);
        vbMain.setAlignment(Pos.TOP_CENTER);
        vbMain.getChildren().addAll(hbHeader, banner, hbButtons, hbButtonLabels, hbFoot);

        //INITIAL_VISIBILITY////////////////////////////////////////////////////////////////////////////////////////////
        sceneMain = new Scene(vbMain,1100,900);

        final Stage secretStage = new Stage();
        secretStage.initModality(Modality.APPLICATION_MODAL);
        secretStage.initOwner(primaryStage);
        secretStage.setResizable(false);

        ObservableList<QueryData> queryDataList = FXCollections.observableArrayList();

        TableColumn<QueryData, String> queryColumn = new TableColumn<>("Zapytanie");
        queryColumn.setMinWidth(300);
        queryColumn.setCellValueFactory(new PropertyValueFactory<>("query"));
        queryColumn.setStyle( "-fx-alignment: CENTER-LEFT;");

        TableColumn<QueryData, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setMinWidth(130);
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setStyle( "-fx-alignment: CENTER-LEFT;");

        TableView<QueryData> queryTable = new TableView<>();
        queryTable.setMaxSize(498,350);
        queryTable.setItems(queryDataList);
        queryTable.getColumns().addAll(Arrays.asList(queryColumn, emailColumn));

        Button btnSelectDatabase = new Button("Wyświetl rekordy");
        btnSelectDatabase.setOnAction(e -> {
            try{
                Connection con;
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;" +
                        "databaseName=*********;"+
                        "user=*********;password=*********;");
                Statement zapyt = con.createStatement();
                ResultSet wynik_zapyt = zapyt.executeQuery("SELECT * FROM QueryTable");
                queryDataList.clear();
                while(wynik_zapyt.next())
                    queryDataList.add(new QueryData(wynik_zapyt.getString(2),
                            wynik_zapyt.getString(3)));
                zapyt.close();
                con.close();
            }
            catch(SQLException sqle) {
                System.out.println(sqle); }
            catch(ClassNotFoundException d) {
                System.out.println("Brak sterownika"); }
        });

        Button btnClearDatabase = new Button("Usuń rekordy");
        btnClearDatabase.setOnAction(e -> {
            try{
                Connection con;
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;" +
                        "databaseName=*********;"+
                        "user=*********;password=*********;");
                Statement zapyt = con.createStatement();
                zapyt.executeUpdate("DELETE FROM QueryTable");
                ResultSet wynik_zapyt = zapyt.executeQuery("SELECT * FROM QueryTable");
                queryDataList.clear();
                while(wynik_zapyt.next())
                    queryDataList.add(new QueryData(wynik_zapyt.getString(2),
                            wynik_zapyt.getString(3)));
                zapyt.close();
                con.close();
            }
            catch(SQLException sqle) {
                System.out.println(sqle);}
            catch(ClassNotFoundException d) {
                System.out.println("Brak sterownika");}
        });

        HBox hbSecret = new HBox(15);
        hbSecret.setAlignment(Pos.TOP_LEFT);
        hbSecret.setPadding(new Insets(20,0,0,20));
        hbSecret.getChildren().addAll(btnSelectDatabase, btnClearDatabase, queryTable);
        Scene secretScene = new Scene(hbSecret, 700, 400);
        secretStage.setScene(secretScene);
                                                                                           //it's a secret to everybody
        sceneMain.setOnKeyTyped((KeyEvent e) -> {
            switch (secretCode.length()) {
                case 0:
                    if (e.getCharacter().matches("d")){
                        secretCode += "d";
                    }
                    else
                        secretCode = "";
                    break;
                case 1:
                    if (e.getCharacter().matches("e")) {
                        secretCode += e.getCharacter();
                    }
                    else
                        secretCode = "";
                    break;
                case 2:
                    if (e.getCharacter().matches("v")) {

                        secretStage.show();
                        secretCode = "";
                    }
                    else
                        secretCode = "";
                    break;
            }
        });
        sceneMain.getStylesheets().addAll(this.getClass().getResource("BetterStyle.css").toExternalForm());

        sceneInvoice = new Scene(vbInvoice,1100,900);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Herbaciarnia");
        primaryStage.setScene(sceneMain);
        primaryStage.show();
    }
}