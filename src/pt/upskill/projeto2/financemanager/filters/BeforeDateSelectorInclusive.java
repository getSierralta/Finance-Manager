package pt.upskill.projeto2.financemanager.filters;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.date.Date;

public class BeforeDateSelectorInclusive implements Selector<StatementLine>{
    Date date;

    public BeforeDateSelectorInclusive(Date date) {
        this.date = date;
    }

    public boolean isSelected(StatementLine stt1) {
        return stt1.getDate().before(date);
    }
}
