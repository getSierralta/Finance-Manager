package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;
import pt.upskill.projeto2.financemanager.exceptions.BadStatementException;

import java.util.Objects;


public class StatementLine implements Comparable<StatementLine> {

    private Date date;
    private Date valueDate;
    private String description;
    private double draft;
    private double credit;
    private double accountingBalance;
    private double availableBalance;
    private Category category;

    public StatementLine(Date date, Date valueDate, String description, double draft, double credit, double accountingBalance, double availableBalance, Category category) throws BadStatementException {
        if (date == null || valueDate == null || description == null || description.equals("")
                || draft > 0 || credit < 0) {
            throw new BadStatementException();
        } else {
            this.date = date;
            this.valueDate = valueDate;
            this.description = description;
            this.draft = draft;
            this.credit = credit;
            this.accountingBalance = accountingBalance;
            this.availableBalance = availableBalance;
            this.category = category;
        }

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDraft() {
        return draft;
    }

    public void setDraft(double draft) {
        this.draft = draft;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getAccountingBalance() {
        return accountingBalance;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public void setAccountingBalance(double accountingBalance) {
        this.accountingBalance = accountingBalance;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatementLine that = (StatementLine) o;
        return Double.compare(that.draft, draft) == 0 &&
                Double.compare(that.credit, credit) == 0 &&
                Double.compare(that.accountingBalance, accountingBalance) == 0 &&
                Double.compare(that.availableBalance, availableBalance) == 0 &&
                Objects.equals(date, that.date) &&
                Objects.equals(valueDate, that.valueDate) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, valueDate, description, draft, credit, accountingBalance, availableBalance, category);
    }

    @Override
    public String toString() {
        return getDate().toString() + " ;" + getValueDate().toString() + " ;" + getDescription() + " ;"
                + getDraft() + " ;" + getCredit() + " ;" + getAccountingBalance() + " ;" + getAvailableBalance();
    }

    @Override
    public int compareTo(StatementLine o) {
        if (date.compareTo(o.date) == 0 && valueDate.compareTo(o.valueDate) == 0 && description.equals(o.description)
                && draft == o.draft && accountingBalance == o.accountingBalance && availableBalance == o.availableBalance) {
            return 0;
        }
        if (date.diffInDays(o.date) == 0) {
            return (int) (availableBalance - o.availableBalance);
        }
        return date.diffInDays(o.date);
    }
}
