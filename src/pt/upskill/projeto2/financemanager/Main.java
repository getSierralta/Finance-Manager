package pt.upskill.projeto2.financemanager;

import javafx.application.Application;
import javafx.stage.Stage;
import pt.upskill.projeto2.financemanager.gui.PersonalFinanceManagerUserInterface;


/**
 * @author upSkill 2020
 * <p>
 * ...
 */

public class Main extends Application {
    public enum Opt{
        MONTHLYOVERVIEW, MONTHOVERVIEW, GLOBALPOSITION, EXIT, CATEGORIES, TRANSACTIONS, ANNUALINTEREST
    }


    public static void main(String[] args)  {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PersonalFinanceManager personalFinanceManager = new PersonalFinanceManager();
        PersonalFinanceManagerUserInterface gui = new PersonalFinanceManagerUserInterface(
                personalFinanceManager);
        gui.execute(primaryStage);
    }

}
