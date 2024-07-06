package trades;


import exceptions.BadParameterException;
import exceptions.DataValidationException;
import exceptions.InvalidPriceOperation;
import exceptions.NullParameterException;
import static trades.BookSide.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public final class ProductManager {

    private static ProductManager instance;
    private HashMap<String, ProductBook> productBooks = new HashMap<>();



    


    public static synchronized ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }


    public void addProduct(String symbol) throws DataValidationException, BadParameterException, NullParameterException {
        boolean hasSpaces = symbol.contains(" ");
        boolean hasSpecialCharacters = symbol.matches(".*[^a-zA-Z0-9 .].*");

        if (symbol == null || symbol.isEmpty() || symbol.length() > 5 || hasSpaces || hasSpecialCharacters) {
            throw new DataValidationException("Symbol cannot be empty, null, greater than 5 characters, have spaces, and have special characters.");
        }
        productBooks.put(symbol, new ProductBook(symbol));
    }

    public ProductBook getProductBook(String symbol) throws DataValidationException {
        if (!productBooks.containsKey(symbol)) {
            throw new DataValidationException("The symbol doesn't exist.");
        }
        return productBooks.get(symbol);
    }

    public String getRandomProduct() throws DataValidationException {
        if (productBooks.isEmpty()) {
            throw new DataValidationException("No products exist.");
        }
        Object[] products = productBooks.keySet().toArray();
        return (String) products[new Random().nextInt(products.length)];
    }

    public TradableDTO addTradable(Tradable o) throws DataValidationException, BadParameterException, NullParameterException, InvalidPriceOperation {
        if (o == null) {
            throw new DataValidationException("Tradable is null");
        }
        ProductBook pb = getProductBook(o.getProduct());
        TradableDTO dto = pb.add(o);
        UserManager.getInstance().addToUser(o.getUser(), dto);
        return dto;
    }

    public TradableDTO[] addQuote(Quote q) throws DataValidationException, BadParameterException, NullParameterException, InvalidPriceOperation {
        if (q == null) {
            throw new DataValidationException("Quote is null");
        }
        ProductBook pb = getProductBook(q.getSymbol());
        pb.removeQuotesForUser(q.getUser());

        TradableDTO buyDTO = addTradable(q.getQuoteSide(BUY));
        TradableDTO sellDTO = addTradable(q.getQuoteSide(SELL));

        return new TradableDTO[]{buyDTO, sellDTO};
    }

    public TradableDTO cancel(TradableDTO o) throws DataValidationException, BadParameterException, NullParameterException, InvalidPriceOperation {
        if (o == null) {
            throw new DataValidationException("TradableDTO can't be null");
        }
        ProductBook pb = getProductBook(o.product);

        return pb.cancel(o.side, o.id);
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws DataValidationException, BadParameterException, NullParameterException, InvalidPriceOperation {
        if (symbol == null || user == null || getProductBook(symbol) == null) {
            throw new DataValidationException("Symbol and user cannot be null.");
        }
        ProductBook pb = getProductBook(symbol);
        return pb.removeQuotesForUser(user);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        productBooks.values().forEach(book -> builder.append(book.toString()).append("\n"));
        return builder.toString();
    }

    public ArrayList<String> getProductList() {
        return new ArrayList<>(productBooks.keySet());
    }
}
