package sim;


import exceptions.*;
import gui.Gui;
import market.*;
import trades.*;
import java.util.HashMap;
import prices.*;
import static trades.BookSide.*;


public class TradingSim {

    public static boolean OUTPUT = true;
    public static final HashMap<String, Double> basePrices = new HashMap<>();

    public static void runSim() throws Exception {

        UserManager.getInstance().init(
                new String[]{"ANN", "BOB", "CAT", "DOG", "EGG"});
        User u1 = UserManager.getInstance().getUser("ANN");
        User u2 = UserManager.getInstance().getUser("BOB");
        User u3 = UserManager.getInstance().getUser("CAT");
        User u4 = UserManager.getInstance().getUser("DOG");
        User u5 = UserManager.getInstance().getUser("EGG");
        
        CurrentMarketPublisher.getInstance().
                subscribeCurrentMarket("WMT", UserManager.getInstance().getUser("ANN"));
        CurrentMarketPublisher.getInstance().
                subscribeCurrentMarket("TGT", UserManager.getInstance().getUser("ANN"));

        CurrentMarketPublisher.getInstance().
                subscribeCurrentMarket("TGT", UserManager.getInstance().getUser("BOB"));
        CurrentMarketPublisher.getInstance().
                subscribeCurrentMarket("TSLA", UserManager.getInstance().getUser("BOB"));

        CurrentMarketPublisher.getInstance().
                subscribeCurrentMarket("AMZN", UserManager.getInstance().getUser("CAT"));
        CurrentMarketPublisher.getInstance().
                subscribeCurrentMarket("TGT", UserManager.getInstance().getUser("CAT"));
        CurrentMarketPublisher.getInstance().
                subscribeCurrentMarket("WMT", UserManager.getInstance().getUser("CAT"));

        CurrentMarketPublisher.getInstance().
                subscribeCurrentMarket("TSLA", UserManager.getInstance().getUser("DOG"));

        CurrentMarketPublisher.getInstance().
                subscribeCurrentMarket("WMT", UserManager.getInstance().getUser("EGG"));

        ProductManager.getInstance().addProduct("WMT");
        ProductManager.getInstance().addProduct("TGT");
        ProductManager.getInstance().addProduct("AMZN");
        ProductManager.getInstance().addProduct("TSLA");
        ProductManager.getInstance().addProduct("AAPL");
        ProductManager.getInstance().addProduct("MSFT");
        ProductManager.getInstance().addProduct("PYPL");


        Gui gui = new Gui();
        CurrentMarketPublisher.getInstance().subscribeCurrentMarket("AAPL", gui);
        CurrentMarketPublisher.getInstance().subscribeCurrentMarket("AMZN", gui);
        CurrentMarketPublisher.getInstance().subscribeCurrentMarket("TGT", gui);
        CurrentMarketPublisher.getInstance().subscribeCurrentMarket("WMT", gui);
        CurrentMarketPublisher.getInstance().subscribeCurrentMarket("TSLA", gui);
        CurrentMarketPublisher.getInstance().subscribeCurrentMarket("MSFT", gui);
        CurrentMarketPublisher.getInstance().subscribeCurrentMarket("PYPL", gui);

        basePrices.put("WMT", 60.17);
        basePrices.put("TGT", 177.21);
        basePrices.put("AMZN", 180.38);
        basePrices.put("TSLA", 175.79);
        basePrices.put("AAPL", 171.48);
        basePrices.put("MSFT", 420.72);
        basePrices.put("PYPL", 66.99);

        CurrentMarketPublisher.getInstance().unSubscribeCurrentMarket("TGT", u2);


        long start = System.currentTimeMillis();

        System.out.println("START");
        for (int i = 0; i < 150; i++) {

            Thread.sleep(100);

            if (i % 1_000 == 0)
                System.out.println(i);

            User u = UserManager.getInstance().getRandomUser();

            if (Math.random() < 0.9) {

                String p = ProductManager.getInstance().getRandomProduct();

                BookSide side = (Math.random() < 0.5 ? BUY : SELL);

                int qty = (int) (25 + (Math.random() * 300));
                int v = (int) Math.round(qty / 5.0) * 5;

                Price price = getPrice(p, side);

                try {
                    Order order = new Order(u.getUserId(), p, price, v, side);
                    TradableDTO orderDTO = ProductManager.getInstance().addTradable(order);
                    u.addTradable(orderDTO);
                } catch (DataValidationException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (u.hasTradableWithRemainingQty()) {
                    TradableDTO odto = u.getTradableWithRemainingQty();
                    TradableDTO orderDTO = ProductManager.getInstance().cancel(odto);
                    if (orderDTO != null)
                        u.addTradable(orderDTO);
                    else
                        if (OUTPUT)
                            System.out.println("*** " + odto);
                }
            }
        }
        long end = System.currentTimeMillis();

        System.out.println(u1.getUserId() + ":\n" + u1.getCurrentMarkets());
        System.out.println(u2.getUserId() + ":\n" + u2.getCurrentMarkets());
        System.out.println(u3.getUserId() + ":\n" + u3.getCurrentMarkets());
        System.out.println(u4.getUserId() + ":\n" + u4.getCurrentMarkets());
        System.out.println(u5.getUserId() + ":\n" + u5.getCurrentMarkets());

        CurrentMarketPublisher.getInstance().unSubscribeCurrentMarket("WMT", u1);
        CurrentMarketPublisher.getInstance().unSubscribeCurrentMarket("TGT", u1);

        CurrentMarketPublisher.getInstance().unSubscribeCurrentMarket("TSLA", u2);

        CurrentMarketPublisher.getInstance().unSubscribeCurrentMarket("AMZN", u3);
        CurrentMarketPublisher.getInstance().unSubscribeCurrentMarket("TGT", u3);
        CurrentMarketPublisher.getInstance().unSubscribeCurrentMarket("WMT", u3);

        CurrentMarketPublisher.getInstance().unSubscribeCurrentMarket("TSLA", u4);
        CurrentMarketPublisher.getInstance().unSubscribeCurrentMarket("WMT", u5);

        System.out.println((end - start) + " millis");

        gui.shutdown();
    }

    private static Price getPrice(String symbol, BookSide side) {
        Double basePrice = basePrices.get(symbol); //100.00

        double priceWidth = 0.02;
        double startPoint = 0.01;
        double tickSize = 0.1;

        double gapFromBase = basePrice * priceWidth; //  5.0
        double priceVariance = gapFromBase * (Math.random());

        if (side == BUY) {
            double priceToUse = basePrice * (1 - startPoint);
            priceToUse += priceVariance;
            double priceToTick = Math.round(priceToUse * 1/tickSize) / 10.0;

            return PriceFactory.makePrice((int) (priceToTick * 100));
        } else {
            double priceToUse = basePrice * (1 + startPoint);
            priceToUse -= priceVariance;
            double priceToTick = Math.round(priceToUse * 1/tickSize) / 10.0;

            return PriceFactory.makePrice((int) (priceToTick * 100));
        }
    }
}
