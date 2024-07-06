package trades;

import exceptions.BadParameterException;
import exceptions.NullParameterException;
import prices.Price;


// DONE
public class Quote {

    private String user;

    private String product;

    private QuoteSide buySide;

    private QuoteSide sellSide;


    public Quote(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume, String userName)
            throws BadParameterException, NullParameterException {
        setUser(userName);
        setProduct(symbol);

        this.buySide = new QuoteSide(userName, symbol, buyPrice, buyVolume, BookSide.BUY);
        this.sellSide = new QuoteSide(userName, symbol, sellPrice, sellVolume, BookSide.SELL);
    }


    // from Order class
    public void setUser(String user) throws NullParameterException, BadParameterException {
        boolean hasSpaces = user.contains(" ");
        boolean hasNumbers = user.matches(".*\\d+.*");
        boolean hasSpecialCharacters = user.matches(".*[^a-zA-Z0-9 ].*");

        if (user == null) {
            throw new NullParameterException("User cannot be null.");
        } else if (user.isEmpty() || user.length() != 3 || hasSpaces || hasNumbers || hasSpecialCharacters) {
            throw new BadParameterException("User cannot be empty, have spaces, numbers, or special characters.");
        }
        this.user = user;

    }

    // from Order class
    public void setProduct(String product) throws NullParameterException, BadParameterException {
        boolean hasSpaces = product.contains(" ");
        boolean hasSpecialCharacters = product.matches(".*[^a-zA-Z0-9 .].*");

        if (product == null) {
            throw new NullParameterException("Product cannot be null.");
        } else if (product.isEmpty() || product.length() > 5 || hasSpaces || hasSpecialCharacters) {
            throw new BadParameterException("Product cannot be empty, greater than 5 characters, have spaces, and have special characters.");
        } else {
            this.product = product;
        }
    }




    public QuoteSide getQuoteSide(BookSide sideIn) {
        if (sideIn == BookSide.BUY) {
            return buySide;
        } else if (sideIn == BookSide.SELL) {
            return sellSide;
        }
        return null;
    }

    public String getSymbol() {
        return product;
    }

    public String getUser() {
        return user;
    }
}
