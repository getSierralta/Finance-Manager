package pt.upskill.projeto2.financemanager.filters;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.date.Date;

public class AfterDateSelectorInclusive implements Selector<StatementLine>{
    Date date;

    public AfterDateSelectorInclusive(Date date) {
        this.date = date;
    }

    public boolean isSelected(StatementLine stt1) {
        if (stt1.getDate().compareTo(date) == 0 ){
            return true;
        }
        return stt1.getDate().after(date);
    }
}
