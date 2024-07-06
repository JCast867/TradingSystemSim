package prices;
import exceptions.InvalidPriceOperation;
import java.util.Objects;


// DONE
public class Price implements Comparable<Price> {

    private final int amount;

    public Price(int amount) {
        this.amount = amount;
    }

    public boolean isNegative() {
        if (amount < 0) {
            return true;
        } else {
            return false;
        }
    }

    public Price add(Price p) throws InvalidPriceOperation {
        if (p == null) {
            throw new InvalidPriceOperation("You cannot add a null value.");
        }
        return new Price(this.amount + p.amount);
    }

    public Price subtract(Price p) throws InvalidPriceOperation {
        if (p == null) {
            throw new InvalidPriceOperation("You cannot subtract null.");
        }
        return new Price(this.amount - p.amount);
    }

    public Price multiply (int n) {
        return new Price(this.amount * n);
    }

    public boolean greaterOrEqual (Price p) throws InvalidPriceOperation {
        if (p == null) {
            throw new InvalidPriceOperation("Nothing to compare to.");
        }
        if (this.amount >= p.amount) {
            return true;
        } else {
            return false;
        }
    }

    public boolean lessOrEqual (Price p) throws InvalidPriceOperation {
        if (p == null) {
            throw new InvalidPriceOperation("Nothing to compare to.");
        }
        if (this.amount <= p.amount) {
            return true;
        } else {
            return false;
        }
    }

    public boolean greaterThan (Price p) throws InvalidPriceOperation {
        if (p == null) {
            throw new InvalidPriceOperation("Nothing to compare to.");
        }
        if (this.amount > p.amount) {
            return true;
        } else {
            return false;
        }
    }

    public boolean lessThan (Price p) throws InvalidPriceOperation {
        if (p == null) {
            throw new InvalidPriceOperation("Nothing to compare to.");
        }
        if (this.amount < p.amount) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return amount == price.amount;
    }

    @Override
    public int hashCode() {

        return Objects.hash(amount);
    }

    @Override
    public int compareTo (Price p) {

        return this.amount - p.amount;
    }

    @Override
    public String toString () {
        double value = this.amount / 100.0;
        return String.format("$%,.2f", value);
    }
}
