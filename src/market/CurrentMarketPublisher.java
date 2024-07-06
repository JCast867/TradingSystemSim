package market;

import exceptions.InvalidPriceOperation;

import java.util.ArrayList;
import java.util.HashMap;

public final class CurrentMarketPublisher {

    private volatile static CurrentMarketPublisher instance;
    private final HashMap<String, ArrayList<CurrentMarketObserver>> filters = new HashMap<>();

    private CurrentMarketPublisher() {

    }

    public static CurrentMarketPublisher getInstance() {
        if (instance == null) {
            synchronized (CurrentMarketPublisher.class) {
                if (instance == null) {
                    instance = new CurrentMarketPublisher();
                }
            }
        }
        return instance;
    }


    public void subscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) {
        if (!filters.containsKey(symbol)) {
            filters.put(symbol, new ArrayList<>());
        }
        filters.get(symbol).add(cmo);
    }

    public void unSubscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) {
        if (!filters.containsKey(symbol)) {
            return;
        }
        filters.get(symbol).remove(cmo);
    }

    public void acceptCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) throws InvalidPriceOperation {
        if (!filters.containsKey(symbol)) {
            return;
        }
        for (CurrentMarketObserver cmo : filters.get(symbol)) {
            cmo.updateCurrentMarket(symbol, buySide, sellSide);
        }
    }
}
