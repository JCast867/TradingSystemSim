package trades;

import exceptions.BadParameterException;
import exceptions.DataValidationException;
import exceptions.InvalidPriceOperation;
import exceptions.NullParameterException;
import market.CurrentMarketTracker;
import prices.Price;

import static trades.BookSide.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductBook {

    private String product;
    private final ProductBookSide buySide;

    private final ProductBookSide sellSide;

    public ProductBook(String product) throws BadParameterException, NullParameterException {
        setProduct(product);

        this.buySide = new ProductBookSide(BUY);
        this.sellSide = new ProductBookSide(SELL);

    }


    private void setProduct(String product) throws NullParameterException, BadParameterException {
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











    public TradableDTO add(Tradable t) throws BadParameterException, DataValidationException, NullParameterException, InvalidPriceOperation {
        ProductBookSide side = t.getSide() == BookSide.BUY ? buySide : sellSide;
        TradableDTO dto = side.add(t);
        tryTrade();
        updateMarket();
        return dto;
    }


    public TradableDTO[] add(Quote qte) throws BadParameterException, DataValidationException, NullParameterException {
        TradableDTO buyDTO = buySide.add(qte.getQuoteSide(BookSide.BUY));
        TradableDTO sellDTO = sellSide.add(qte.getQuoteSide(BookSide.SELL));
        tryTrade();
        return new TradableDTO[] {buyDTO, sellDTO};
    }


    public TradableDTO cancel(BookSide side, String orderId) throws BadParameterException, DataValidationException, NullParameterException, InvalidPriceOperation {
        if (side == BUY) {
            TradableDTO holder = buySide.cancel(orderId);
            updateMarket();
            return holder;
            //return buySide.cancel(orderId);
        } else { //side == SELL
            TradableDTO holder = sellSide.cancel(orderId);
            updateMarket();
            return holder;
           // return sellSide.cancel(orderId);
        }
    }


    public void tryTrade() throws BadParameterException, DataValidationException, NullParameterException {
        while (buySide.topOfBookPrice() != null && sellSide.topOfBookPrice() != null &&
                buySide.topOfBookPrice().compareTo(sellSide.topOfBookPrice()) >= 0) {
            int volumeToTrade = Math.min(buySide.topOfBookVolume(), sellSide.topOfBookVolume());
            buySide.tradeOut(buySide.topOfBookPrice(), volumeToTrade);
            sellSide.tradeOut(sellSide.topOfBookPrice(), volumeToTrade);
        }
    }





    public TradableDTO[] removeQuotesForUser(String userName) throws BadParameterException, DataValidationException, NullParameterException, InvalidPriceOperation {
        TradableDTO[] dtos = new TradableDTO[2];
        dtos[0] = buySide.removeQuotesForUsers(userName);
        dtos[1] = sellSide.removeQuotesForUsers(userName);
        updateMarket();
        return dtos;
    }

    private void updateMarket() throws InvalidPriceOperation, DataValidationException {

        CurrentMarketTracker.getInstance().
                updateMarket(product, buySide.topOfBookPrice(), buySide.topOfBookVolume(),
                        sellSide.topOfBookPrice(), sellSide.topOfBookVolume());
    }

    @Override
    public String toString() {
        return "Product: " + product + "\n" + buySide.toString() + "\n" + sellSide.toString();
    }
}
