package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.categories.Category;

public class SavingsAccount extends Account {
    public static Category savingsCategory = new Category("Savings");

    public SavingsAccount(long id, String name) {
        super(id, name);
        Account.createFile(this);
    }


    @Override
    public double getInterestRate() {
        return BanksConstants.savingsInterestRate();
    }
}
