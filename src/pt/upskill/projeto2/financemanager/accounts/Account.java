package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.accounts.formats.FileAccountFormat;
import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;
import pt.upskill.projeto2.financemanager.date.Month;
import pt.upskill.projeto2.financemanager.exceptions.BadFormatException;
import pt.upskill.projeto2.financemanager.exceptions.UnknownAccountException;
import pt.upskill.projeto2.financemanager.filters.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Account {
    private final long id;
    private String name;
    private Date startDate;
    private String currency;
    private Date endDate;
    private List<StatementLine> statementLines = new ArrayList<>();
    private double balance = 0.001;
    private File file;
    private String type;

    public Account(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public double getInterestRate() {
        return BanksConstants.normalInterestRate();
    }

    public long getId() {
        return id;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String additionalInfo() {
        return "";
    }

    void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    void setCurrency(String currency) {
        this.currency = currency;
    }

    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<StatementLine> getStatementLines() {
        return statementLines;
    }

    public Date getEndDate() {
        if (statementLines.size() == 0) {
            readStatements();
        }
        Collections.sort(statementLines);
        if (statementLines.size() > 0) {
            endDate = statementLines.get(statementLines.size() - 1).getDate();
        }
        return endDate;
    }

    public Date getStartDate() {
        if (statementLines.size() == 0) {
            readStatements();
        }
        Collections.sort(statementLines);
        if (statementLines.size() > 0) {
            startDate = statementLines.get(0).getDate();
        }
        return startDate;
    }

    public StatementLine getLastBalance() {
        if (statementLines.size()>0){
            return statementLines.get(statementLines.size()-1);
        }
        return null;
    }

    public static Account newAccount(File file) throws UnknownAccountException {
        long id = 0;
        String currency = null;
        String name = null;
        String type = null;
        Date startDate = null;
        Date endDate = null;
        try {
            Scanner scanner = new Scanner(file);
            int i = 0;
            while (scanner.hasNext()) {
                String[] split = scanner.nextLine().split(";");
                switch (i) {
                    case 1:
                        try {
                            id = Long.parseLong(split[1].replaceAll("[^\\d.]", ""));
                        } catch (NumberFormatException e) {
                            throw new BadFormatException();
                        }
                        currency = split[2].trim();
                        name = split[3].trim();
                        type = split[4].trim();
                        break;
                    case 2:
                        String[] split1 = split[1].split("[^0-9]");
                        try {
                            startDate = new Date(Integer.parseInt(split1[0].replaceAll("[^0-9.]", "")), Integer.parseInt(split1[1].replaceAll("[^0-9.]", "")), Integer.parseInt(split1[2].replaceAll("[^0-9.]", "")));
                        } catch (NumberFormatException e) {
                            throw new BadFormatException();
                        }
                        break;
                    case 3:
                        String[] split2 = split[1].split("[^0-9]");
                        try {
                            endDate = new Date(Integer.parseInt(split2[0].replaceAll("[^0-9.]", "")), Integer.parseInt(split2[1].replaceAll("[^0-9.]", "")), Integer.parseInt(split2[2].replaceAll("[^0-9.]", "")));
                        } catch (NumberFormatException e) {
                            throw new BadFormatException();
                        }
                        break;
                }
                i++;
                if (i > 3) {
                    break;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (type != null) {
            if (type.equals("SavingsAccount")) {
                SavingsAccount savingsAccount = new SavingsAccount(id, name);
                savingsAccount.setCurrency(currency);
                savingsAccount.setStartDate(startDate);
                savingsAccount.setEndDate(endDate);
                savingsAccount.setFile(file);
                savingsAccount.setType(type);
                return savingsAccount;
            } else if (type.equals("DraftAccount")) {
                DraftAccount draftAccount = new DraftAccount(id, name);
                draftAccount.setCurrency(currency);
                draftAccount.setStartDate(startDate);
                draftAccount.setEndDate(endDate);
                draftAccount.setFile(file);
                draftAccount.setType(type);
                return draftAccount;
            } else {
                throw new UnknownAccountException();
            }
        } else {
            throw new UnknownAccountException();
        }
    }

    public double getCurrentBalance() {
        if (statementLines.size() == 0) {
            readStatements();
        }
        Collections.sort(statementLines);
        if (statementLines.size() > 1) {
            balance = statementLines.get(statementLines.size() - 1).getAccountingBalance();
        } else if (statementLines.size() > 0) {
            balance = statementLines.get(0).getAccountingBalance();
        }

        return balance;
    }

    public double estimatedAverageBalance() {
        int currentYear = new Date().getYear();
        if (getLastBalance() != null){
            if (getLastBalance().getDate().getYear() < currentYear){
                return getCurrentBalance();
            }
        }else {
            return 0.0;
        }

        Date lastYearDate = new Date(31,12, currentYear-1);
        AfterDateSelector selector = new AfterDateSelector(lastYearDate);
        BeforeDateSelector beforeDateSelector = new BeforeDateSelector(lastYearDate);
        Filter<StatementLine, AfterDateSelector> filter = new Filter<>(selector);
        Filter<StatementLine, BeforeDateSelector> filter1 = new Filter<>(beforeDateSelector);
        List<StatementLine> yearStatement = (List<StatementLine>) filter.apply(this.statementLines);
        List<StatementLine> lastYearStatements = (List<StatementLine>) filter1.apply(this.statementLines);

        StatementLine lastStatement = lastYearStatements.get(lastYearStatements.size() - 1);
        double balanceTotal = 0.0;
        int diffDays;
        for (int i = 0; i < yearStatement.size(); i++){
            StatementLine statementLine = yearStatement.get(i);
            if (i == 0){
                diffDays = statementLine.getDate().diffInDays(lastYearDate);
            }else {
                diffDays = statementLine.getDate().diffInDays(lastStatement.getDate());
            }
            balanceTotal += lastStatement.getAvailableBalance()* diffDays;

            lastStatement = yearStatement.get(i);
        }
        balanceTotal += lastStatement.getAvailableBalance()*(lastYearDate.diffInDays(new Date())-1);
        int numTotalDias = lastYearDate.diffInDays(new Date())-1;
        return balanceTotal/numTotalDias;
        /*
        //Of the year we are in
        double balance = 0;
        getCurrentBalance();
        LocalDate localDate = java.time.LocalDate.now();
        Date today = new Date(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());
        AfterDateSelectorInclusive selector = new AfterDateSelectorInclusive(new Date(1, 1, localDate.getYear()));
        Filter<StatementLine, AfterDateSelectorInclusive> filter = new Filter<>(selector);
        List<StatementLine> statementLines = (List<StatementLine>) filter.apply(this.statementLines);
        if (statementLines.size() > 0) {
            System.out.println(statementLines.size());
            int multiply;
            int daysOfTheYear = 365;
            boolean flag = false;
            if (today.getYear() % 4 == 0) {
                daysOfTheYear = 366;
            }
            int i = 0;
            for (StatementLine statementLine : statementLines) {
                //This flag avoids IndexOutOfBoundException
                if (flag) {
                    multiply = statementLine.getDate().diffInDays(statementLines.get(i - 1).getDate());
                    if (multiply > 0) {
                        balance += statementLine.getAccountingBalance() * multiply;
                    } else if (multiply == 0) {
                        balance += ((statementLine.getAccountingBalance() + statementLines.get(i - 1).getAccountingBalance()) / 2);
                    }
                }
                balance += statementLine.getAccountingBalance();
                flag = true;
                i++;
            }
            return (float) (balance / (daysOfTheYear - new Date(1, 1, today.getYear() + 1).diffInDays(today)));
        } else {
            return (float) this.balance;
        }*/
    }


    void readStatements() {
        if (statementLines.size() > 1) {
            statementLines.clear();
        }
        try {
            Scanner scanner = new Scanner(getFile());
            int i = 0;
            while (scanner.hasNext()) {
                String[] split = scanner.nextLine().split(";");
                if (i > 4) {
                    String[] split1 = split[0].split("[^0-9]");
                    String[] split2 = split[1].split("[^0-9]");
                    Date date;
                    Date valueDate;
                    try {
                        date = new Date(Integer.parseInt(split1[0].replaceAll("[^0-9.]", "")), Integer.parseInt(split1[1].replaceAll("[^0-9.]", "")), Integer.parseInt(split1[2].replaceAll("[^0-9.]", "")));
                    } catch (NumberFormatException e) {
                        throw new BadFormatException();
                    }
                    try {
                        valueDate = new Date(Integer.parseInt(split2[0].replaceAll("[^0-9.]", "")), Integer.parseInt(split2[1].replaceAll("[^0-9.]", "")), Integer.parseInt(split2[2].replaceAll("[^0-9.]", "")));
                    } catch (NumberFormatException e) {
                        throw new BadFormatException();
                    }
                    Category category = new Category("null");
                    if (this instanceof SavingsAccount) {
                        category = SavingsAccount.savingsCategory;
                    }
                    try {
                        statementLines.add(new StatementLine(date, valueDate, split[2].trim(), Double.parseDouble(split[3].trim()),
                                Double.parseDouble(split[4].trim()), Double.parseDouble(split[5].trim()), Double.parseDouble(split[6].trim()), category));
                    } catch (NumberFormatException e) {
                        throw new BadFormatException();
                    }
                }
                i++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            throw new BadFormatException();
        }
        fixStatements(statementLines);
    }

    private void fixStatements(List<StatementLine> newStatementLines) {
        //This is not taking into account the difference into accounting and available
        Collections.sort(newStatementLines);
        if (newStatementLines.size() > 0) {
            double oldBalance = newStatementLines.get(0).getAvailableBalance();
            for (StatementLine statementLine : newStatementLines) {
                if (statementLine.getAvailableBalance() != oldBalance) {
                    if (statementLine.getDraft() == 0 && oldBalance + statementLine.getCredit() != statementLine.getAvailableBalance()) {
                        statementLine.setAccountingBalance(round(oldBalance + statementLine.getCredit()));
                        statementLine.setAvailableBalance(round(oldBalance + statementLine.getCredit()));
                    } else if (statementLine.getCredit() == 0 && oldBalance + statementLine.getDraft() != statementLine.getAvailableBalance()) {
                        statementLine.setAccountingBalance(round(oldBalance + statementLine.getDraft()));
                        statementLine.setAvailableBalance(round(oldBalance + statementLine.getDraft()));
                    } else if (oldBalance + statementLine.getDraft() + statementLine.getCredit() != statementLine.getAvailableBalance()) {
                        statementLine.setAvailableBalance(round((oldBalance + statementLine.getCredit()) + statementLine.getDraft()));
                        statementLine.setAccountingBalance(round((oldBalance + statementLine.getCredit()) + statementLine.getDraft()));
                    }
                }
                oldBalance = statementLine.getAvailableBalance();
            }
        }
    }
    private double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(3, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public void addStatementLine(StatementLine statementLine) {
        if (checkStatement(statementLine)) {
            try {
                PrintWriter writer = new PrintWriter(file);
                readStatements();
                if (this instanceof SavingsAccount){
                    statementLine.setCategory(SavingsAccount.savingsCategory);
                }
                statementLines.add(statementLine);
                Collections.sort(statementLines);
                writer.println(FileAccountFormat.format(this));
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addStatementLineWithoutSaving(StatementLine statementLine) {
        if (checkStatement(statementLine)) {
            readStatements();
            statementLines.add(statementLine);
            Collections.sort(statementLines);
        }
    }

    private boolean checkStatement(StatementLine statementLine) {
        if (statementLines.size() == 0) {
            readStatements();
        }
        if (statementLines.size() > 0) {
            for (StatementLine statementLine1 : statementLines) {
                if (statementLine1.compareTo(statementLine) == 0) {
                    return false;
                }
            }
        }
        return true;
    }


    public void removeStatementLinesBefore(Date date) {
        readStatements();
        BeforeDateSelector selector = new BeforeDateSelector(date);
        Filter<StatementLine, BeforeDateSelector> filter = new Filter<>(selector);
        statementLines = (List<StatementLine>) filter.apply(statementLines);
        /*List<StatementLine> toRemove = new ArrayList<>();
        for (StatementLine statementLine : statementLines) {
            if (statementLine.getDate().before(date)) {
                toRemove.add(statementLine);
            } hello darkness my old friend i come to talk with you again
        }*/
        //statementLines.removeAll(toRemove);
        Collections.sort(statementLines);

    }

    public List<StatementLine> statementsInAMonthInclusive(Date date) {
        readStatements();
        InAMonthSelector selector = new InAMonthSelector(date);
        Filter<StatementLine, InAMonthSelector> filter = new Filter<>(selector);
        List<StatementLine> newStatementLines = (List<StatementLine>) filter.apply(statementLines);
        if (newStatementLines.size() > 0) {
            Collections.sort(newStatementLines);
            fixStatements(newStatementLines);
        }
        return newStatementLines;
    }

    public double totalDraftsForCategorySince(Category category, Date date) {
        double draft = 0;
        for (StatementLine statementLine : statementLines) {
            if (statementLine.getCategory() != null) {
                if (statementLine.getCategory().getName().equals(category.getName()) && statementLine.getDate().after(date)) {
                    draft += statementLine.getDraft();
                }
            }
        }
        return draft;
    }
    public List<StatementLine> totalStatementsForCategorySince(Category category, Date date) {
        List<StatementLine> statementL = new ArrayList<>();
        for (StatementLine statementLine : statementLines) {
            if (statementLine.getCategory() != null) {
                if (statementLine.getCategory().getName().equals(category.getName()) && statementLine.getDate().after(date)) {
                    statementL.add(statementLine);
                }
            }
        }
        return statementL;
    }

    public double totalForMonthDifference(int month, int year) {
        List<StatementLine> statementLines = statementsInAMonthInclusive(new Date(1, month, year));
        if (statementLines.size() > 1) {
            return statementLines.get(statementLines.size() - 1).getAvailableBalance() - statementLines.get(0).getAvailableBalance();
        }
        return 0;
    }

    public double totalForMonthCredit(int month, int year) {
        double credit = 0;
        Month month1 = Date.intToMonth(month);
        for (StatementLine statementLine : statementLines) {
            if (statementLine.getDate().getMonth().compareTo(month1) == 0 && statementLine.getDate().getYear() == year) {
                credit += statementLine.getCredit();
            }
        }
        return credit;
    }

    public double totalForMonth(int month, int year) {
        double draft = 0;
        Month month1 = Date.intToMonth(month);
        for (StatementLine statementLine : statementLines) {
            if (statementLine.getDate().getMonth().compareTo(month1) == 0 && statementLine.getDate().getYear() == year) {
                draft += statementLine.getDraft();
            }
        }
        return draft;
    }

    public void autoCategorizeStatements(List<Category> categories) {
        for (StatementLine statementLine : statementLines) {
            for (Category category : categories) {
                if (category.hasTag(statementLine.getDescription())) {
                    statementLine.setCategory(category);
                }
            }
        }
    }

    public static void saveAccount(Account account) {
        try {
            PrintWriter printWriter = new PrintWriter(account.getFile());
            printWriter.println(FileAccountFormat.format(account));
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected static void createFile(Account account) {
        File file = new File("account_info/" + account.id + ".csv");
        try {
            file.createNewFile();
            account.setFile(file);
            account.readStatements();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double totalForYearDifference(int year) {
        InAYearSelector selector = new InAYearSelector(year);
        Filter<StatementLine, InAYearSelector> filter = new Filter<>(selector);
        List<StatementLine> newStatementLines = (List<StatementLine>) filter.apply(statementLines);
        if (newStatementLines.size() > 1){
            return newStatementLines.get(newStatementLines.size()-1).getAvailableBalance()-newStatementLines.get(0).getAvailableBalance();
        }
        return 0;
    }

    public double totalDraftsForYear(int year) {
        double draft = 0;
        for (int i = 1; i < 13; i++){
            draft += totalForMonth(i,year);
        }
        return draft;
    }

    public double totalCreditForYear(int year) {
        double credit = 0;
        for (int i = 1; i < 13; i++){
            credit += totalForMonthCredit(i,year);
        }
        return credit;
    }

    public void removeStatement(StatementLine statementLine) {
        statementLines.remove(statementLine);
        fixStatements(statementLines);
    }

    public boolean editStatement(StatementLine statementLine, String category, String tag) {

        for (int i = 0; i < statementLines.size(); i++){
            if (statementLines.get(i).equals(statementLine)) {
                Category category1 = new Category(category.toUpperCase());
                category1.addTag(tag.toUpperCase());
                statementLines.get(i).setCategory(category1);
                return true;
            }
        }
        return false;
    }

    public double totalCreditsForCategorySince(Category category, Date date) {
        double credit = 0;
        for (StatementLine statementLine : statementLines) {
            if (statementLine.getCategory() != null) {
                if (statementLine.getCategory().getName().equals(category.getName()) && statementLine.getDate().after(date)) {
                    credit += statementLine.getCredit();
                }
            }
        }
        return credit;
    }

    public double getAnnualInterest() {
        return estimatedAverageBalance() * getInterestRate();
    }
}