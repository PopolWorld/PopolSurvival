package me.nathanfallet.popolsurvival.utils;

public class JobExperienceTransaction {

    private long amount;

    public JobExperienceTransaction(long amount) {
        this.amount = amount;
    }

    public JobExperienceTransaction() {
        this(0);
    }

    public void add(long experience) {
        this.amount += experience;
    }

    public long getAmount() {
        return amount;
    }
    
}
