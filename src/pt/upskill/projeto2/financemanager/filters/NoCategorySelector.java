package pt.upskill.projeto2.financemanager.filters;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;

public class NoCategorySelector implements Selector<StatementLine>{

    public boolean isSelected(StatementLine stt1) {
        return stt1.getCategory()==null;
    }
}
