package trades;

import exceptions.BadParameterException;
import exceptions.NullParameterException;
import prices.Price;


// DONE
public class QuoteSide implements Tradable {


    private String user;

    private String product;

    private Price price;

    private BookSide side;

    private String id;

    private int originalVolume;

    private int remainingVolume;

    private int cancelledVolume;

    private int filledVolume;

    public QuoteSide(String user, String product, Price price, int originalVolume, BookSide side)
            throws BadParameterException, NullParameterException {
        setUser(user);
        setProduct(product);
        setPrice(price);
        setSide(side);
        setOriginalVolume(originalVolume);

        this.remainingVolume = originalVolume;
        this.cancelledVolume = 0;
        this.filledVolume = 0;
        this.id = generateId();
    }

    private final void setUser(String user) throws NullParameterException, BadParameterException {
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

    private final void setProduct(String product) throws NullParameterException, BadParameterException {
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

    private final void setPrice(Price price) throws NullParameterException {
        if (price == null) {
            throw new NullParameterException("Price cannot be null.");
        } else {
            this.price = price;
        }
    }

    private final void setSide (BookSide side) throws NullParameterException {
        if (side == null) {
            throw new NullParameterException("Book side cannot be null.");
        } else {
            this.side = side;
        }
    }

    public final String generateId() {
        return user + product + price + System.nanoTime();
    }

    private final void setOriginalVolume(int originalVolume) throws BadParameterException {
        if (originalVolume < 0 || originalVolume > 10000) {
            throw new BadParameterException("Invalid volume.");
        } else {
            this.originalVolume = originalVolume;
        }
    }


    @Override
    public String toString() {
        return product + " quote from " + user + ": " + price + ", Orig Vol: " + originalVolume
                + ", Rem Vol: " + remainingVolume + ", Fill Vol: " + filledVolume +
                ", CXL Vol: " + cancelledVolume + ", ID: " + id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getCancelledVolume() {
        return cancelledVolume;
    }

    @Override
    public void setCancelledVolume(int newVol) {
        this.cancelledVolume = newVol;
    }

    @Override
    public int getRemainingVolume() {
        return remainingVolume;
    }

    @Override
    public void setRemainingVolume(int newVol) {
        this.remainingVolume = newVol;
    }

    @Override
    public TradableDTO makeTradableDTO() {
        return new TradableDTO(user, product, price, side, id, originalVolume, remainingVolume, cancelledVolume, filledVolume);
    }

    @Override
    public Price getPrice() {
        return price;
    }

    @Override
    public void setFilledVolume(int newVol) {
        this.filledVolume = newVol;
    }

    @Override
    public int getFilledVolume() {
        return filledVolume;
    }

    @Override
    public BookSide getSide() {
        return side;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getProduct() {
        return product;
    }

    @Override
    public int getOriginalVolume() {
        return originalVolume;
    }
}
