package pt.upskill.projeto2.financemanager.filters;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.date.Date;

public class InAMonthSelector implements Selector<StatementLine> {

	Date date;
	public InAMonthSelector(Date date) {
		this.date = date;
	}

	@Override
	public boolean isSelected(StatementLine item) {
		if (item.getDate().getMonth().compareTo(date.getMonth()) == 0 && item.getDate().getYear() == date.getYear()){
			return true;
		}else{
			return false;
		}
	}
}
