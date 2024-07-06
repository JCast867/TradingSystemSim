package market;

import exceptions.DataValidationException;
import exceptions.InvalidPriceOperation;
import prices.Price;

public final class CurrentMarketTracker {

    private volatile static CurrentMarketTracker instance;

    public static CurrentMarketTracker getInstance() {
        if (instance == null) {
            synchronized (CurrentMarketTracker.class) {
                if (instance == null) {
                    instance = new CurrentMarketTracker();
                }
            }
        }
        return instance;
    }

    public void updateMarket(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws InvalidPriceOperation, DataValidationException {

        Price marketWidth; //new Price(0); = buyPrice.subtract(sellPrice);
        if (sellPrice == null || buyPrice == null) {
            marketWidth = new Price(0);
        } else {
            marketWidth = sellPrice.subtract(buyPrice);
        }
        //marketWidth = sellPrice.subtract(buyPrice);


        CurrentMarketSide buy = new CurrentMarketSide(buyPrice, buyVolume);
        CurrentMarketSide sell = new CurrentMarketSide(sellPrice, sellVolume);

        System.out.println("*********** Current Market ***********");
        System.out.println("* " + symbol + " " + buy + " - " + sell + " [" + marketWidth + "]*");
        System.out.println("**************************************");

        CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buy, sell);
    }



}
