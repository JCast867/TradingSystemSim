package trades;

import exceptions.BadParameterException;
import exceptions.DataValidationException;
import exceptions.NullParameterException;
import prices.Price;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.*;

public class ProductBookSide {

    private BookSide side;
    private final HashMap<Price, ArrayList<Tradable>> bookEntries;

    public ProductBookSide(BookSide side) throws NullParameterException {
        setSide(side);

        this.bookEntries = new HashMap<>();
    }


    private void setSide(BookSide side) throws NullParameterException {
        if (side == null) {
            throw new NullParameterException("Book side cannot be null.");
        }
        this.side = side;
    }






    public TradableDTO add(Tradable o) {
        Price p = o.getPrice();

        if (!bookEntries.containsKey(p)) {
            ArrayList<Tradable> trade = new ArrayList<>();
            bookEntries.put(p, trade);
            trade.add(o);

        } else {
            ArrayList<Tradable> trade;
            trade = bookEntries.get(p);
            trade.add(o);
        }
        return o.makeTradableDTO();
    }

    public TradableDTO cancel(String tradableId) throws BadParameterException, DataValidationException, NullParameterException {
        for (Price price : bookEntries.keySet()) {
            List<Tradable> tradables = bookEntries.get(price);
            for (Tradable tradable : tradables) {
                if (tradable.getId().equals(tradableId)) {
                    tradables.remove(tradable);
                    tradable.setCancelledVolume(tradable.getRemainingVolume());
                    tradable.setRemainingVolume(0);
                    UserManager.getInstance().addToUser(tradable.getUser(),tradable.makeTradableDTO());
                    if (tradables.isEmpty()) {
                        bookEntries.remove(price);
                    }
                    return tradable.makeTradableDTO();
                }
            }
        }
        return null;
    }

    public TradableDTO removeQuotesForUsers(String userName) throws BadParameterException, NullParameterException, DataValidationException {
        final TradableDTO[] cancelledQuote = {null};
        for (Price price : new ArrayList<>(bookEntries.keySet())) {
            ArrayList<Tradable> tradables = bookEntries.get(price);
            tradables.removeIf(tradable -> {
                boolean match = tradable.getUser().equals(userName) && tradable instanceof QuoteSide;
                if (match) {
                    cancelledQuote[0] = tradable.makeTradableDTO();
                    tradable.setCancelledVolume(tradable.getRemainingVolume());
                    tradable.setRemainingVolume(0);
                    try {
                        UserManager.getInstance().addToUser(tradable.getUser(), tradable.makeTradableDTO());
                    } catch (BadParameterException e) {
                        throw new RuntimeException(e);
                    } catch (NullParameterException e) {
                        throw new RuntimeException(e);
                    } catch (DataValidationException e) {
                        throw new RuntimeException(e);
                    }
                }
                return match;
            });
            if (tradables.isEmpty()) {
                bookEntries.remove(price);
            }
        }
        return cancelledQuote[0];
    }

    public Price topOfBookPrice() {
        if (bookEntries.isEmpty()) {
            return null;
        } else if (side == BookSide.BUY) {
            return Collections.max(bookEntries.keySet());
        } else /*if (side == BookSide.SELL) */{
            return Collections.min(bookEntries.keySet());
        }
    }

    public int topOfBookVolume() {
        Price topPrice = topOfBookPrice();
        if (topPrice == null) {
            return 0;
        }
        ArrayList<Tradable> topOrders = bookEntries.get(topPrice);
        return topOrders.stream().mapToInt(Tradable::getRemainingVolume).sum();
    }

    // Method that processes a trade on one side of the book
    public void tradeOut(Price price, int volume) throws BadParameterException, DataValidationException, NullParameterException {
        int remainingVol = volume;
        ArrayList<Tradable> ordersAtPrice = bookEntries.get(price);

        while (remainingVol > 0 && !ordersAtPrice.isEmpty()) {
            Tradable firstOrder = ordersAtPrice.get(0);
            int firstOrderRemainingVol = firstOrder.getRemainingVolume();

            if (firstOrderRemainingVol <= remainingVol) {
                remainingVol -= firstOrderRemainingVol;
                firstOrder.setFilledVolume(firstOrder.getFilledVolume() + firstOrderRemainingVol);
                firstOrder.setRemainingVolume(0);
                UserManager.getInstance().addToUser(firstOrder.getUser(), firstOrder.makeTradableDTO());
                ordersAtPrice.remove(0);
                System.out.println("FULL FILL: " + firstOrder);
            } else {
                firstOrder.setFilledVolume(firstOrder.getFilledVolume() + remainingVol);
                firstOrder.setRemainingVolume(firstOrderRemainingVol - remainingVol);
                UserManager.getInstance().addToUser(firstOrder.getUser(), firstOrder.makeTradableDTO());
                remainingVol = 0;
                System.out.println("PARTIAL FILL: " + firstOrder);
            }
        }

        if (ordersAtPrice.isEmpty()) {
            bookEntries.remove(price);
        }
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Side: ").append(side).append("\n");
        bookEntries.forEach((price, tradables) -> {
            sb.append(" Price: ").append(price).append("\n");
            tradables.forEach(tradable -> sb.append("  ").append(tradable).append("\n"));
        });
        return sb.toString();
    }



}
