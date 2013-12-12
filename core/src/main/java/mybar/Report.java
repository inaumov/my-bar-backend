package mybar;

public class Report {

    private String name;
    private int amount;

    public Report() {
    }

    public Report(String name, int amount) {
        this.setName(name);
        this.setAmount(amount);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Report [name=" + name + ", amount=" + amount + "]";
    }

}