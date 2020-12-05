package pt.upskill.projeto2.financemanager.accounts.formats;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;

public class SimpleStatementFormat implements StatementLineFormat {

    @Override
    public String fields() {
        return "Date \tDescription \tDraft \tCredit \tAvailable balance ";
    }

    @Override
    public String format(StatementLine statementLine) {
        return statementLine.getDate().toString()+" \t"+statementLine.getDescription()+" \t"
                +statementLine.getDraft()+" \t"+statementLine.getCredit()+" \t"+statementLine.getAvailableBalance();
    }
}
