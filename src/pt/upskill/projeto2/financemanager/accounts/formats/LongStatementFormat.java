package pt.upskill.projeto2.financemanager.accounts.formats;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;

public class LongStatementFormat implements StatementLineFormat {


    @Override
    public String fields() {
        return "Date \tValue Date \tDescription \tDraft \tCredit \tAccounting balance \tAvailable balance ";
    }


    @Override
    public String format(StatementLine statementLine) {

        return statementLine.getDate().toString() + " \t" + statementLine.getValueDate().toString() + " \t" + statementLine.getDescription() + " \t"
                + statementLine.getDraft() + " \t" + statementLine.getCredit() + " \t" + statementLine.getAccountingBalance() + " \t" + statementLine.getAvailableBalance();
    }
}
