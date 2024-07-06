package prices;


import prices.Price;

import java.util.HashMap;


// DONE
abstract public class PriceFactory {

    private static HashMap<Integer, Price> priceHash = new HashMap<>();

    public static Price makePrice(int value) {
        if (priceHash.containsKey(value)) {
            return priceHash.get(value);
        }
        Price p = new Price(value);
        priceHash.put(value, p);
        return p;
    }

    public static Price makePrice(String stringValueIn) {
        stringValueIn = stringValueIn.replace("$", "").replace(",", "");

        double amount = Double.parseDouble(stringValueIn);
        int newAmount = (int) (amount * 100);

        if (priceHash.containsKey(newAmount)) {
            return priceHash.get(newAmount);
        }
        Price p = new Price(newAmount);
        priceHash.put(newAmount, p);
        return p;
    }
}
