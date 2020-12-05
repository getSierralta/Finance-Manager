package pt.upskill.projeto2.financemanager.filters;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.date.Date;

public class InAYearSelector implements Selector<StatementLine> {

	int year;
	public InAYearSelector(int year) {
		this.year = year;
	}

	@Override
	public boolean isSelected(StatementLine item) {
		return item.getDate().getYear() == year;
	}
}
