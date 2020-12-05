package pt.upskill.projeto2.financemanager.accounts.formats;

import pt.upskill.projeto2.financemanager.accounts.Account;
import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.date.Date;
import pt.upskill.projeto2.financemanager.exceptions.BadDate;

import java.time.LocalDate;

public class FileAccountFormat {

	public static String infoFormat(Account a1){
		String nl = System.getProperty("line.separator");
		Date statDate;
		try {
			statDate = a1.getStartDate();
		} catch (IndexOutOfBoundsException e) {
			throw new BadDate();
		}
		LocalDate localDate = java.time.LocalDate.now();
		return "Account Info - " + localDate.getDayOfMonth() + "-" + localDate.getMonthValue() + "-" + localDate.getYear() + nl
				+ "Account\t" + a1.getId() + "\tCurrency: " + a1.getCurrency() + "\t" + a1.getName() + "\t" + a1.getType() + "\t"
				+ "Start Date\t" + statDate;
	}
	public static String format(Account a1) {
		String nl = System.getProperty("line.separator");
		Date statDate;
		Date endDate;
		try {
			statDate = a1.getStartDate();
		} catch (IndexOutOfBoundsException e) {
			throw new BadDate();
		}
		try {
			endDate = a1.getEndDate();
		} catch (IndexOutOfBoundsException e) {
			throw new BadDate();
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (StatementLine statementLine : a1.getStatementLines()) {
			stringBuilder.append(statementLine.toString()).append(nl);
		}
		return "Account Info - " + new Date().toString() + nl
				+ "Account ;" + a1.getId() + " ;" + a1.getCurrency() + " ;" + a1.getName() + " ;" + a1.getType() + " ;" + nl
				+ "Start Date ;" + statDate + nl
				+ "End Date ;" + endDate + nl
				+ "Date ;Value Date ;Description ;Draft ;Credit ;Accounting balance ;Available balance" + nl
				+ stringBuilder;
	}
}
