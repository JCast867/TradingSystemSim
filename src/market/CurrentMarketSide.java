package market;
import exceptions.DataValidationException;
import prices.*;
public class CurrentMarketSide {

    private Price price;
    private final int volume;

    public CurrentMarketSide(Price price, int volume) throws DataValidationException {
        setPrice(price);
        this.volume = volume;
    }

    private void setPrice(Price price) throws DataValidationException {
        if (price == null) {
            this.price = new Price(0);
        } else {
            this.price = price;
        }
    }


    @Override
    public String toString() {
        return price + "x" + volume;
    }

    public Price getPrice() {
        return price;
    }

    public int getVolume() {
        return volume;
    }
}
