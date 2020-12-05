package pt.upskill.projeto2.financemanager.gui;


import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.Window;
import pt.upskill.projeto2.financemanager.Main;
import pt.upskill.projeto2.financemanager.PersonalFinanceManager;
import pt.upskill.projeto2.financemanager.accounts.Account;
import pt.upskill.projeto2.financemanager.accounts.DraftAccount;
import pt.upskill.projeto2.financemanager.accounts.SavingsAccount;
import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;
import pt.upskill.projeto2.financemanager.exceptions.BadStatementException;
import pt.upskill.projeto2.utils.Menu;

import java.util.ArrayList;
import java.util.List;

import static pt.upskill.projeto2.financemanager.Main.Opt.*;


/**
 * @author upSkill 2020
 * <p>
 * ...
 */

public class PersonalFinanceManagerUserInterface {

    private final String[] months = new String[]{
            //I was having problems with the null one
            "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    };
    private final List<String> years = new ArrayList<>();

    private Account currentAccount = null;

    public PersonalFinanceManagerUserInterface(
            PersonalFinanceManager personalFinanceManager) {
        this.personalFinanceManager = personalFinanceManager;
    }

    private final PersonalFinanceManager personalFinanceManager;

    private final static BorderPane layout = new BorderPane();

    public void execute(Stage primaryStage) {
        BorderPane border = new BorderPane();
        border.setStyle("-fx-background-color: #FBFEFB;");
        MenuButton button1 = new MenuButton("Global Position", GLOBALPOSITION);
        MenuButton button2 = new MenuButton("Monthly Overview", MONTHLYOVERVIEW);
        MenuButton button3 = new MenuButton("Month Overview", MONTHOVERVIEW);
        MenuButton button4 = new MenuButton("Transactions", TRANSACTIONS);
        MenuButton button5 = new MenuButton("Categories", CATEGORIES);
        MenuButton button6 = new MenuButton("Annual Interest", ANNUALINTEREST);
        MenuButton button7 = new MenuButton("Exit", EXIT);

        VBox menu = new VBox(button1, button2, button3, button4, button5, button6, button7);
        menu.setSpacing(3.0D);
        border.setCenter(layout);
        executeWelcome();
        border.setLeft(menu);
        primaryStage.setTitle("CodersLegacy");
        primaryStage.setScene(new Scene(border, 1200.0D, 900.0D));
        primaryStage.show();
    }


    private class MenuButton extends Button {
        private final Main.Opt opt;

        public MenuButton(String text, Main.Opt opt) {
            super(text);
            this.opt = opt;
            this.setTextAlignment(TextAlignment.CENTER);
            this.setTextFill(Color.rgb(64, 64, 64));
            this.setStyle("-fx-background-color: #20b2aa; -fx-font-size: 20;");
            this.setPrefWidth(250.0D);
            this.setPrefHeight(200.0D);
            this.setCursor(Cursor.HAND);
            this.setOnMouseClicked((e) -> {
                try {
                    activateOption();
                } catch (InterruptedException interruptedException) {
                    //interruptedException.printStackTrace();
                }
            });
            this.setOnMouseEntered((e) -> {
                this.setUnderline(true);
                this.setTextFill(Color.rgb(224, 228, 231));
                this.setStyle("-fx-background-color: #008b8b; -fx-font-size: 25; ");
            });
            this.setOnMouseExited((e) -> {
                this.setUnderline(false);
                this.setTextFill(Color.rgb(64, 64, 64));
                this.setStyle("-fx-background-color: #20b2aa; -fx-font-size: 20;");

            });
        }

        private void activateOption() throws InterruptedException {
            layout.getChildren().removeAll(layout.getChildren());
            switch (opt) {
                case GLOBALPOSITION:
                    executeGlobalPosition();
                    break;
                case MONTHLYOVERVIEW:
                    executeAccounts(0);
                    break;
                case MONTHOVERVIEW:
                    executeAccounts(1);
                    break;
                case TRANSACTIONS:
                    executeAccounts(2);
                    break;
                case CATEGORIES:
                    executeAccounts(3);
                    break;
                case ANNUALINTEREST:
                    executeAnnualInterest();
                    break;
                case EXIT:
                    executeExit();
                    break;
            }
        }
    }

    private void executeWelcome() {
        Label welcomeLabel = new Label("Welcome");
        welcomeLabel.setAlignment(Pos.CENTER);
        HBox box = new HBox(welcomeLabel);
        box.setPrefHeight(400);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-font-size: 100;");
        layout.setTop(box);

    }

    private void executeGlobalPosition() {
        List<Account> listOfAccounts = new ArrayList<>(personalFinanceManager.getAccounts().values());
        GridPane gridPane = new GridPane();
        gridPane.setVgap(50);
        for (int i = 0; i < listOfAccounts.size(); i++) {
            Label idLabel = new Label("\tID:\t" + listOfAccounts.get(i).getId());
            String type = listOfAccounts.get(i).getType().equals("SavingsAccount") ? "Saving Account" : "Draft Account";
            Label typeLabel = new Label("\tType:\t" + type);
            HBox accountInfoDetailsBox = new HBox(idLabel, typeLabel);
            Label balanceLabel = new Label("\tAvailable Balance:\t\t" + listOfAccounts.get(i).getCurrentBalance());
            HBox balanceLabelBox = new HBox(balanceLabel);
            VBox container = new VBox(accountInfoDetailsBox, balanceLabelBox);
            System.out.println("\tID:\t" + listOfAccounts.get(i).getId() + "\tType:\t" + type);
            System.out.println("\tAvailable Balance:\t\t" + listOfAccounts.get(i).getCurrentBalance());
            gridPane.add(container, 0, i);
        }
        layout.setCenter(gridPane);

        Button addAccount = new Button("Add Account");
        addAccount.setStyle("-fx-background-color: #20b2aa;");
        addAccount.setOnMouseClicked(event -> newAccount());
        addAccount.setPadding(new Insets(20));
        addAccount.setCursor(Cursor.HAND);
        HBox hBox = new HBox(addAccount);
        hBox.setPadding(new Insets(0, 0, 20, 800));
        layout.setBottom(hBox);
    }

    private void newAccount() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(40, 40, 40, 40));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        Label headerLabel = new Label("New Account");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gridPane.add(headerLabel, 0, 0, 2, 1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setMargin(headerLabel, new Insets(20, 0, 20, 0));
        gridPane.add(new Label("Account Name: "), 0, 1);
        TextField nameField = new TextField();
        nameField.setPrefHeight(40);
        gridPane.add(nameField, 1, 1);
        gridPane.add(new Label("Select type: "), 0, 2);
        CheckBox typeBox = new CheckBox("Saving Account");
        typeBox.setPrefHeight(40);
        gridPane.add(typeBox, 1, 2);
        gridPane.add(new Label("ID : "), 0, 3);
        TextField IDField = new TextField();
        IDField.setPrefHeight(40);
        gridPane.add(IDField, 1, 3);
        Button submitButton = new Button("Submit");
        submitButton.setPrefHeight(40);
        submitButton.setDefaultButton(true);
        submitButton.setPrefWidth(100);
        gridPane.add(submitButton, 0, 4, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(20, 0, 20, 0));
        layout.setCenter(gridPane);
        submitButton.setOnAction(event -> {
            if (nameField.getText().isEmpty() || IDField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR,"Form Error!", "All fields are obligatory", gridPane.getScene().getWindow());
                return;
            }
            try {
                long id = Long.parseLong(IDField.getText().replaceAll("[^\\d]", ""));
                boolean accountType = typeBox.isSelected();
                Account newAccount = accountType ? new SavingsAccount(id, nameField.getText()) : new DraftAccount(id, nameField.getText());
                newAccount.setType(accountType ? "SavingsAccount" : "DraftAccount");
                personalFinanceManager.getAccounts().put(id, newAccount);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR,"Parse error", "Id most only be numbers", gridPane.getScene().getWindow());
            }
            executeGlobalPosition();
        });
    }

    public static void showAlert(Alert.AlertType type, String message, String content, Window owner) {
        Alert alert = new Alert(type);
        alert.setTitle(message);
        alert.setContentText(content);
        alert.initOwner(owner);
        alert.show();
    }

    private void newStatement() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(40, 40, 40, 40));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        Label headerLabel = new Label("New Statement");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gridPane.add(headerLabel, 0, 0, 2, 1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setMargin(headerLabel, new Insets(20, 0, 20, 0));
        gridPane.add(new Label("Description: "), 0, 1);
        TextField descriptionField = new TextField();
        descriptionField.setPrefHeight(40);
        gridPane.add(descriptionField, 1, 1);
        gridPane.add(new Label("Draft: "), 0, 2);
        TextField draftField = new TextField();
        draftField.setPrefHeight(40);
        gridPane.add(draftField, 1, 2);
        gridPane.add(new Label("Credit: "), 0, 3);
        TextField creditField = new TextField();
        creditField.setPrefHeight(40);
        gridPane.add(creditField, 1, 3);
        // Add Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setPrefHeight(40);
        submitButton.setDefaultButton(true);
        submitButton.setPrefWidth(100);
        gridPane.add(submitButton, 0, 4, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(20, 0, 20, 0));
        layout.setCenter(gridPane);
        submitButton.setOnAction(event -> {
            if (descriptionField.getText().isEmpty() || draftField.getText().isEmpty() || creditField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR,"Form error", "Please fill up the fields", gridPane.getScene().getWindow());
            } else {
                try {
                    double draft;
                    double credit;
                    draft = Long.parseLong(draftField.getText());
                    try {
                        credit = Long.parseLong(creditField.getText());
                        if (draft > 0) {
                            showAlert(Alert.AlertType.ERROR,"??", "Draft has to be a negative number", gridPane.getScene().getWindow());
                        }else {
                            if (credit < 0) {
                                showAlert(Alert.AlertType.ERROR, "??", "Credit has to be a positive number", gridPane.getScene().getWindow());
                            }else {
                                double balance = currentAccount.getCurrentBalance() + draft;
                                balance += credit;
                                try {
                                    currentAccount.addStatementLineWithoutSaving(new StatementLine(new Date(), new Date(), descriptionField.getText().trim(), draft, credit, balance, balance, null));
                                    showAlert(Alert.AlertType.CONFIRMATION, ":)", "Statement added successfully!", gridPane.getScene().getWindow());
                                } catch (BadStatementException e) {
                                    showAlert(Alert.AlertType.ERROR, ":(", "Couldn't add the statement", gridPane.getScene().getWindow());
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR,"Parse error", "Credit most only be numbers", gridPane.getScene().getWindow());
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR,"Parse error", "Draft most only be numbers", gridPane.getScene().getWindow());
                }
            }
        });
    }

    private void executeAccounts(int i) {
        BorderPane borderPane = new BorderPane();
        List<Button> buttons = new ArrayList<>();
        for (Account account : personalFinanceManager.getAccounts().values()) {
            Button button = new Button(account.getName());
            button.setPrefHeight(100);
            button.setPrefWidth(((float) 950 / personalFinanceManager.getAccounts().size()));
            int amount = 50 / personalFinanceManager.getAccounts().size();
            button.setStyle("-fx-font-size: " + amount + "; -fx-background-color: #008b8b;");
            button.setTextFill(Color.rgb(64, 64, 64));
            button.setOnMouseClicked((e) -> activateAccount(account, borderPane, i));
            button.setOnMouseExited((e) -> {
                button.setUnderline(true);
                button.setStyle("-fx-background-color: #008b8b; -fx-font-size: " + amount + ";");
            });
            button.setOnMouseEntered((e) -> {
                button.setUnderline(false);
                button.setStyle("-fx-background-color: #008b8b; -fx-font-size: " + amount * 1.5 + ";");

            });
            buttons.add(button);
        }
        Button[] buttons1 = buttons.toArray(new Button[0]);
        HBox hBox = new HBox(buttons1);
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(hBox);
        borderPane.setTop(hBox);
        layout.setCenter(borderPane);
    }

    private void activateAccount(Account account, BorderPane borderPane, int i) {
        currentAccount = account;
        years.clear();
        try {
            for (int j = account.getStartDate().getYear(); j <= account.getEndDate().getYear(); j++) {
                years.add(String.valueOf(j));
            }
        } catch (NullPointerException e) {
            borderPane.setCenter(null);
        }

        switch (i) {
            case 0:
                getYear(borderPane);
                break;
            case 1:
                getMonth(borderPane);
                break;
            case 2:
                getMovements(borderPane);
                break;
            case 3:
                getCategories(borderPane);
                break;
            default:
                break;
        }
    }

    private void getYear(BorderPane borderPane) {
        VBox vBox = new VBox();
        VBox vBox2 = new VBox();
        vBox2.prefHeight(50);
        if (currentAccount != null) {
            int year = new Date().getYear();
            try {
                GridPane gridPane = new GridPane();
                gridPane.setVgap(20);
                gridPane.setHgap(20);
                gridPane.setStyle("-fx-font-size: 16;");
                gridPane.setPadding(new Insets(50));
                gridPane.setGridLinesVisible(true);
                gridPane.add(new Label("\t" + "MONTH"), 1, 0);
                gridPane.add(new Label("   DRAFT"), 2, 0);
                gridPane.add(new Label("    CREDIT"), 3, 0);
                gridPane.add(new Label(" DIFFERENCE "), 4, 0);
                gridPane.add(new Label(" MONTH CLOSED WITH "), 5, 0);
                double closedWith = 0;
                double lastBalance = 0;
                double balance;
                for (int i = 1; i < 13; i++) {
                    Date date = new Date(1, i, year);
                    Label monthsDifference = new Label("\t" + ((float) currentAccount.totalForMonthDifference(i, year)) + "\t");
                    monthsDifference.setTextFill(currentAccount.totalForMonth(i, year) >= 0 ? Color.GREEN : Color.RED);
                    if (currentAccount.statementsInAMonthInclusive(date).size() > 0) {
                        balance = currentAccount.statementsInAMonthInclusive(date).get(currentAccount.statementsInAMonthInclusive(date).size() - 1).getAvailableBalance();
                        lastBalance = balance;
                    } else {
                        balance = lastBalance;
                    }
                    Label monthsBalance = new Label("\t" + balance + "\t");
                    monthsBalance.setTextFill(balance >= 0 ? Color.GREEN : Color.RED);
                    gridPane.add(new Label("\t" + months[i - 1] + "\t"), 1, i);
                    gridPane.add(new Label("\t" + currentAccount.totalForMonth(i, year) + "\t"), 2, i);
                    gridPane.add(new Label("\t" + currentAccount.totalForMonthCredit(i, year) + "\t"), 3, i);
                    gridPane.add(monthsDifference, 4, i);
                    gridPane.add(monthsBalance, 5, i);
                    if (i == 12) {
                        closedWith = balance;
                    }
                }
                vBox.getChildren().add(gridPane);
                GridPane gridPane1 = new GridPane();
                Label yearDifference = new Label("\t" + ((float) currentAccount.totalForYearDifference(year)) + "\t");
                yearDifference.setTextFill(currentAccount.totalForYearDifference(year) >= 0 ? Color.GREEN : Color.RED);
                Label closedWithLabel = new Label("\t" + closedWith + "\t");
                closedWithLabel.setTextFill(closedWith >= 0 ? Color.GREEN : Color.RED);
                Label estimatedAverage = new Label("\t" + Math.round(currentAccount.estimatedAverageBalance()) + "\t");
                estimatedAverage.setTextFill(Math.round(currentAccount.estimatedAverageBalance()) >= 0 ? Color.GREEN : Color.RED);
                gridPane1.add(new Label("YEAR"), 0, 0);
                gridPane1.add(new Label("DRAFTS"), 1, 0);
                gridPane1.add(new Label("CREDIT"), 2, 0);
                gridPane1.add(new Label("DIFFERENCE"), 3, 0);
                gridPane1.add(new Label("YEAR CLOSED WITH"), 4, 0);
                gridPane1.add(new Label("ESTIMATED AVERAGE BALANCE"), 5, 0);
                gridPane1.add(new Label(String.valueOf(year)), 0, 1);
                gridPane1.add(new Label(String.valueOf(currentAccount.totalDraftsForYear(year))), 1, 1);
                gridPane1.add(new Label(String.valueOf(currentAccount.totalCreditForYear(year))), 2, 1);
                gridPane1.add(yearDifference, 3, 1);
                gridPane1.add(closedWithLabel, 4, 1);
                gridPane1.add(estimatedAverage, 5, 1);
                gridPane1.setVgap(20);
                gridPane1.setHgap(20);
                vBox2.setPadding(new Insets(30));
                vBox2.getChildren().add(gridPane1);
            } catch (NullPointerException | NumberFormatException e) {
                vBox.getChildren().add(getNoSelection());
            }
        }
        borderPane.setCenter(vBox);
        borderPane.setBottom(vBox2);
    }

    private void getMonth(BorderPane borderPane) {
        VBox vBox = new VBox();
        VBox vBox2 = new VBox();
        vBox2.prefHeight(50);
        if (currentAccount != null) {
            try {
                String month = Menu.requestSelection("Select a month", months);
                try {
                    String year = Menu.requestSelection("Select a Year", years.toArray((new String[0])));
                    try {
                        Date date = new Date(1, Date.stringToInt(month), Integer.parseInt(year));
                        if (currentAccount.statementsInAMonthInclusive(date).size() > 0) {
                            List<StatementLine> statementLines = new ArrayList<>(currentAccount.statementsInAMonthInclusive(date));
                            vBox.getChildren().add(getStatements(statementLines));
                            vBox.setPadding(new Insets(30));
                            double totalForMonth = statementLines.get(statementLines.size() - 1).getAccountingBalance() - statementLines.get(0).getAccountingBalance();
                            Label balanceLabel = new Label("\tTotal draft:\t" + currentAccount.totalForMonth(Date.stringToInt(month), Integer.parseInt(year)) + "\tMonth Difference:\t");
                            Label totalForMonthLabel = new Label(String.valueOf(totalForMonth));
                            if (totalForMonth > 0) {
                                totalForMonthLabel.setTextFill(Color.GREEN);
                                totalForMonthLabel.setUnderline(true);
                            } else if (totalForMonth < 0) {
                                totalForMonthLabel.setTextFill(Color.RED);
                                totalForMonthLabel.setUnderline(true);
                            }
                            HBox box2 = new HBox(balanceLabel, totalForMonthLabel);
                            box2.setAlignment(Pos.CENTER_RIGHT);
                            box2.setStyle("-fx-font-size: 20;");
                            box2.setPadding(new Insets(10));
                            vBox2.getChildren().add(box2);
                        } else {
                            vBox.getChildren().add(emptyBox());
                        }
                    } catch (NumberFormatException e) {
                        vBox.getChildren().add(getNoSelection());
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    vBox.getChildren().add(emptyBox());
                }
            } catch (NullPointerException e) {
                vBox.getChildren().add(getNoSelection());
            }
        }
        borderPane.setCenter(vBox);
        borderPane.setBottom(vBox2);
    }

    private HBox emptyBox() {
        Label emptyLabel = new Label("So much emptiness :(");
        emptyLabel.setAlignment(Pos.CENTER);
        HBox box = new HBox(emptyLabel);
        box.setPrefHeight(400);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-font-size: 70;");
        return box;
    }

    private HBox getNoSelection() {
        Label badLabel = new Label("You didn't selected anything D:");
        badLabel.setAlignment(Pos.CENTER);
        HBox box = new HBox(badLabel);
        box.setPrefHeight(700);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-font-size: 50;");
        return box;
    }

    private GridPane getStatements(List<StatementLine> statementLines) {
        personalFinanceManager.getAccounts().forEach((aLong, account) -> account.autoCategorizeStatements(personalFinanceManager.getCategories()));
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setGridLinesVisible(true);
        gridPane.setStyle("-fx-font-size: 13;");
        gridPane.add(new Label("\tDate:\t"), 1, 0);
        gridPane.add(new Label("\tDescription:\t"), 2, 0);
        gridPane.add(new Label("\tDraft:\t"), 3, 0);
        gridPane.add(new Label("\tCredit:\t"), 4, 0);
        gridPane.add(new Label("\tAccounting:\t"), 5, 0);
        gridPane.add(new Label("\tAvailable:\t"), 6, 0);
        gridPane.add(new Label("\tCategory:\t"), 7, 0);
        int i = 1;
        for (StatementLine statementLine : statementLines) {
            gridPane.add(new Label("\t" + statementLine.getDate().toString() + "\t"), 1, i);
            gridPane.add(new Label("\t" + statementLine.getDescription() + "\t"), 2, i);
            gridPane.add(new Label("\t" + statementLine.getDraft() + "\t"), 3, i);
            gridPane.add(new Label("\t" + statementLine.getCredit() + "\t"), 4, i);
            gridPane.add(new Label("\t" + statementLine.getAccountingBalance() + "\t"), 5, i);
            gridPane.add(new Label("\t" + statementLine.getAvailableBalance() + "\t"), 6, i);
            String category =  statementLine.getCategory() == null ? "null" : statementLine.getCategory().getName();
            gridPane.add(new Label("\t" +category+ "\t"), 7, i);
            Button remove = new Button("-");
            remove.setStyle("-fx-background-color: #ff4d4d;");
            remove.setOnMouseClicked(event -> deleteStatement(statementLine, currentAccount));
            Button edit = new Button("edit");
            edit.setStyle("-fx-background-color: #0099cc;");
            edit.setOnMouseClicked(event -> editStatement(statementLine, currentAccount));
            gridPane.add(remove, 8, i);
            gridPane.add(edit, 9, i);
            i++;
        }
        return gridPane;
    }

    private void deleteStatement(StatementLine statementLine, Account currentAccount) {
        boolean option = Menu.yesOrNoInput("Are you sure you want to delete this statement?");
        if (option) {
            currentAccount.removeStatement(statementLine);
            showAlert(Alert.AlertType.CONFIRMATION,"Statement removed", "refresh to see results", layout.getScene().getWindow());
        } else {
            showAlert(Alert.AlertType.INFORMATION,"Nothing", "Happened", layout.getScene().getWindow());
        }
    }

    private void editStatement(StatementLine statementLine, Account currentAccount) {
        boolean option = Menu.yesOrNoInput("Are you sure you want to change the category of this statement?");
        if (option) {
            String category = Menu.requestInput("Insert the new Category");
            String tag = Menu.requestInput("Insert the new Tag");
            if (currentAccount.getType().equals("SavingsAccount")) {
                showAlert(Alert.AlertType.WARNING,"Couldn't edit statement", "savings accounts can only have the Category, \"Savings\"", layout.getScene().getWindow());
            } else {
                if (currentAccount.editStatement(statementLine, category, tag)) {
                    Category category1 = new Category(category.toUpperCase());
                    category1.addTag(tag.toUpperCase());
                    personalFinanceManager.getCategories().add(category1);
                    showAlert(Alert.AlertType.CONFIRMATION,"Statement edited", "refresh to see results", layout.getScene().getWindow());
                } else {
                    showAlert(Alert.AlertType.CONFIRMATION,"Couldn't edit statement", "try again...", layout.getScene().getWindow());
                }
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Nothing", "Happened", layout.getScene().getWindow());
        }
    }

    private void getCategories(BorderPane borderPane) {
        borderPane.setCenter(null);
        VBox vBox = new VBox();
        try {
            for (Category category : personalFinanceManager.getCategories()) {
                Button button = new Button("\t" + category.getName());
                button.setStyle("-fx-background-color: black; -fx-font-size: 15;");
                button.setPrefSize(150, 30);
                button.setTextFill(Color.WHITE);
                button.setCursor(Cursor.HAND);
                button.setOnMouseClicked((e) -> getCategories(borderPane, category));
                VBox vBox1 = new VBox(button);
                for (String tag : category.getTags()) {
                    Label label = new Label("->" + tag);
                    label.setStyle("-fx-font-size: 15;");
                    label.setPrefSize(150, 30);
                    label.setTextFill(Color.GREEN);
                    vBox1.getChildren().add(label);
                }
                vBox.getChildren().add(vBox1);
                vBox.setStyle("-fx-background-color: black;");
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        borderPane.setLeft(vBox);
    }

    private void getCategories(BorderPane borderPane, Category category) {
        VBox vBox = new VBox(new Label("\t" + category.getName().toUpperCase() + ":"));
        try {
            String month = Menu.requestSelection("Select a month", months);
            String year = Menu.requestSelection("Select a Year", years.toArray((new String[0])));
            try {
                Date date = new Date(1, Date.stringToMonth(month), Integer.parseInt(year));
                vBox.setPadding(new Insets(10));
                GridPane gridPane1 = new GridPane();
                gridPane1.add(new Label("DRAFTS"), 0, 0);
                gridPane1.add(new Label("CREDIT"), 1, 0);
                gridPane1.add(new Label("AMOUNT OF MOVEMENTS"), 2, 0);
                gridPane1.add(new Label(String.valueOf(currentAccount.totalDraftsForCategorySince(category, date))), 0, 1);
                gridPane1.add(new Label(String.valueOf(currentAccount.totalCreditsForCategorySince(category, date))), 1, 1);
                List<StatementLine> statementLines = currentAccount.totalStatementsForCategorySince(category, date);
                gridPane1.add(new Label(String.valueOf(statementLines.size())), 2, 1);
                gridPane1.setVgap(20);
                gridPane1.setHgap(20);
                GridPane statementGridPane = getStatements(statementLines);
                statementGridPane.setStyle("-fx-font-size: 11;");
                vBox.getChildren().add(statementGridPane);
                vBox.getChildren().add(gridPane1);
                vBox.setSpacing(50);
            } catch (NumberFormatException e) {
                borderPane.setCenter(getNoSelection());
            }
        } catch (Exception e) {
            vBox.getChildren().add(getNoSelection());
        }
        borderPane.setCenter(vBox);
    }

    private void getMovements(BorderPane borderPane) {
        List<StatementLine> statementLines = new ArrayList<>(currentAccount.getStatementLines());
        VBox vBox = new VBox(getStatements(statementLines));
        vBox.setPadding(new Insets(20));
        borderPane.setCenter(vBox);
        Button statementButton = new Button("Add Statement");
        statementButton.setStyle("-fx-background-color: #20b2aa;");
        statementButton.setOnMouseClicked(event -> newStatement());
        statementButton.setPadding(new Insets(20));
        statementButton.setCursor(Cursor.HAND);
        HBox hBox2 = new HBox(statementButton);
        hBox2.setPadding(new Insets(0, 0, 20, 800));
        borderPane.setBottom(hBox2);
    }

    private void executeAnnualInterest() {
        VBox vBox = new VBox();
        for (Account account : personalFinanceManager.getAccounts().values()) {
            HBox hBox = new HBox(new Label(account.getName() + "\t"), new Label("ID -" + account.getId() + "\t"));
            HBox hBox1 = new HBox(new Label("Estimated average Balance: " + Math.round(account.estimatedAverageBalance()) + "\t"), new Label("Interest rate " + account.getInterestRate() + "\t"),
                    new Label("Annual Interest: " + Math.round(account.getAnnualInterest())));
            VBox vBox1 = new VBox(hBox, hBox1);
            vBox.getChildren().add(vBox1);
        }
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(20));
        layout.setCenter(vBox);

    }

    private void executeExit() throws InterruptedException {
        //TODO ask josé why this is behaving the way that it is
        //As in it closes and it doesn't show the label
        layout.getChildren().removeAll(layout.getChildren());
        String[] strings = new String[]{"YES", "NO", "CANCEL"};
        String string = null;
        while (string == null) {
            string = Menu.requestSelection("Do you wish to save?", strings);
        }
        Label label;
        switch (string) {
            case "YES":
                personalFinanceManager.getAccounts().forEach((aLong, account) -> Account.saveAccount(account));
                //This should save the categories but isn't
                Category.writeCategories(personalFinanceManager.getCategories());
                label = new Label("Saving...");
                break;
            case "NO":
                label = new Label("Closing the program...");
                break;
            case "CANCEL":
                label = new Label("Nothing happened");
                break;
            default:
                label = new Label("You didn't selected anything");
                break;
        }
        label.setAlignment(Pos.CENTER);
        HBox box = new HBox(label);
        box.setPrefHeight(700);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-font-size: 50;");
        layout.setCenter(box);
        String text = label.getText();
        if (text.equals("Saving...") || text.equals("Closing the program...")) {
            Thread.sleep(300);
            System.exit(0);
        }
    }
}

