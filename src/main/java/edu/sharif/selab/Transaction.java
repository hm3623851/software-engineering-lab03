package edu.sharif.selab;

import java.util.Objects;

public class Transaction {

    private final TransactionType type;
    private final int amount;

    public Transaction(TransactionType type, int amount) {
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative");
        }
        this.type = type;
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction that = (Transaction) obj;
        return amount == that.amount && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, amount);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "type=" + type +
                ", amount=" + amount +
                '}';
    }
}
