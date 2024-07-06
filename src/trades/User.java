package trades;

import exceptions.BadParameterException;
import exceptions.NullParameterException;
import market.CurrentMarketObserver;
import market.CurrentMarketSide;

import java.util.*;

public class User implements CurrentMarketObserver {

    private String userId;
    private HashMap<String, TradableDTO> tradables;
    private HashMap<String, CurrentMarketSide[]> currentMarkets;

    public User(String userId) throws BadParameterException, NullParameterException {
        setUser(userId);
        this.tradables = new HashMap<>();
        this.currentMarkets = new HashMap<>();
    }

    private void setUser(String userId) throws NullParameterException, BadParameterException {
        boolean hasSpaces = userId.contains(" ");
        boolean hasNumbers = userId.matches(".*\\d+.*");
        boolean hasSpecialCharacters = userId.matches(".*[^a-zA-Z0-9 ].*");

        if (userId == null) {
            throw new NullParameterException("User cannot be null.");
        } else if (userId.isEmpty() || userId.length() != 3 || hasSpaces || hasNumbers || hasSpecialCharacters) {
            throw new BadParameterException("User cannot be empty, have spaces, numbers, or special characters.");
        }
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void addTradable(TradableDTO o) {
        if (o != null) {
            tradables.put(o.id, o);
        }
    }

    public boolean hasTradableWithRemainingQty() {
        for (TradableDTO tradable : tradables.values()) {
            if (tradable.remainingVolume > 0) {
                return true;
            }
        }
        return false;
    }

    public TradableDTO getTradableWithRemainingQty() throws NullParameterException {
        List<TradableDTO> trades = new ArrayList<>();
        for (TradableDTO tradable : tradables.values()) {
            if (tradable.remainingVolume > 0) {
                trades.add(tradable);
            }
        }
        if (trades.isEmpty()) return null;

        Random random = new Random();
        return trades.get(random.nextInt(trades.size()));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("User Id: " + userId + "\n");
        tradables.values().forEach(t -> builder.append(" Product: ").append(t.product)
                .append(", Price: ").append(t.price)
                .append(", OriginalVolume: ").append(t.originalVolume)
                .append(", RemainingVolume: ").append(t.remainingVolume)
                .append(", CancelledVolume: ").append(t.cancelledVolume)
                .append(", FilledVolume: ").append(t.filledVolume)
                .append(", User: ").append(userId)
                .append(", Side: ").append(t.side)
                .append(", Id: ").append(t.id).append("\n"));
        return builder.toString();
    }

    @Override
    public void updateCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        currentMarkets.put(symbol, new CurrentMarketSide[]{buySide, sellSide});
    }

    public String getCurrentMarkets() {
        StringBuilder builder = new StringBuilder();

        for (String symbol : currentMarkets.keySet()) {
            CurrentMarketSide[] sides = currentMarkets.get(symbol);
            builder.append(" ").append(sides[0]).append(" - ").append(sides[1]).append("\n");
        }
        return builder.toString();
    }
}
