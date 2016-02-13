package mybar;

public class History {

    private String name;
    private int amount;

    public History() {
    }

    public History(String name, int amount) {
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
        return "History [name=" + name + ", amount=" + amount + "]";
    }

}