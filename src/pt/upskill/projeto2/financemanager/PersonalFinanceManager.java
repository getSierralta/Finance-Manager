package pt.upskill.projeto2.financemanager;

import pt.upskill.projeto2.financemanager.accounts.Account;
import pt.upskill.projeto2.financemanager.accounts.DraftAccount;
import pt.upskill.projeto2.financemanager.accounts.SavingsAccount;
import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class PersonalFinanceManager {

    private final HashMap<Long, Account> accounts = new HashMap<>();
    private List<Category> categories = new ArrayList<>();


    public PersonalFinanceManager() {
        readAccounts();
        readStatements();
        readCategories();
    }

    private void readCategories(){
        categories = Category.readCategories(new File("account_info/categories"));
        accounts.forEach(((aLong, account) -> account.autoCategorizeStatements(categories)));
    }

    private void readAccounts() {
        for (File file : Objects.requireNonNull(new File("account_info").listFiles())) {
            try {
                Account account = Account.newAccount(file);
                accounts.put(account.getId(), Account.newAccount(file));
            } catch (Exception e) {
               // System.out.println("Couldn't create account " + file + " because of "+e.getMessage());
            }
        }

    }

    private void readStatements() {
        for (File file : Objects.requireNonNull(new File("statements").listFiles())) {
            try {
                Scanner scanner = new Scanner(file);
                long id = 0;
                int i = -1;
                while (scanner.hasNext()) {
                    i++;
                    String[] split = scanner.nextLine().split(";");
                    if (split.length == 5) {
                        try {
                            id = Long.parseLong(split[1].replaceAll("[^\\d.]", ""));
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            //System.out.println("Couldn't parse the id ");
                            break;
                        }

                        if (accounts.get(id) == null) {
                            if (split[4].trim().equals("DraftAccount")){
                                DraftAccount draftAccount = new DraftAccount(id, split[3]);
                                draftAccount.setType("DraftAccount");
                                accounts.put(id, draftAccount);
                            }else if(split[4].trim().equals("SavingsAccount")){
                                SavingsAccount savingsAccount = new SavingsAccount(id, split[3]);
                                savingsAccount.setType("");
                                accounts.put(id, savingsAccount);
                            }

                        }
                    }
                    if (split.length == 7 && i >6) {
                        String[] split1 = split[0].split("[^0-9]");
                        String[] split2 = split[1].split("[^0-9]");
                        Date date = null;
                        Date valueDate = null;
                        if (split1.length > 0 && split2.length > 0) {
                            try {
                                date = new Date(Integer.parseInt(split1[0].replaceAll("[^0-9.]", "")), Integer.parseInt(split1[1].replaceAll("[^0-9.]", "")), Integer.parseInt(split1[2].replaceAll("[^0-9.]", "")));
                            } catch (NumberFormatException e) {
                                //System.out.println("Couldn't add the statement #" + i + " of the file, because of:\t"+e.getMessage());
                                continue;
                            }
                            try {
                                valueDate = new Date(Integer.parseInt(split2[0].replaceAll("[^0-9.]", "")), Integer.parseInt(split2[1].replaceAll("[^0-9.]", "")), Integer.parseInt(split2[2].replaceAll("[^0-9.]", "")));
                            } catch (NumberFormatException e) {
                                //System.out.println("Couldn't add the statement #" + i + " of the file, because of:\t"+e.getMessage());
                                continue;
                            }
                        }
                        Category category = new Category("null");
                        if (accounts.get(id) instanceof SavingsAccount) {
                            category = SavingsAccount.savingsCategory;
                        }
                        double draft;
                        double credit;
                        try {
                            draft = Double.parseDouble(split[3].trim());
                        } catch (NumberFormatException e) {
                            draft = 0;
                        }
                        try {
                            credit = Double.parseDouble(split[4].trim());
                        } catch (NumberFormatException e) {
                            credit = 0;
                        }
                        accounts.get(id).addStatementLineWithoutSaving(new StatementLine(date, valueDate, split[2].trim(), draft,
                                credit, Double.parseDouble(split[5].trim()), Double.parseDouble(split[6].trim()), category));
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<Long, Account> getAccounts() {
        return accounts;
    }

    public List<Category> getCategories() {
        return categories;
    }

}
