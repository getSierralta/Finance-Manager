package pt.upskill.projeto2.financemanager.accounts;

public class DraftAccount extends Account {

    public DraftAccount(long id, String name) {
        super(id, name);
        Account.createFile(this);
    }


}
