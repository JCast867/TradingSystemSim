/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import prices.*;

/**
 *
 * @author hieldc
 */
public class UserDisplayManager {

    private final MarketDisplay marketDisplay;
    public UserDisplayManager() {
        marketDisplay = new MarketDisplay();
    }
    public void showMarketDisplay() {
        marketDisplay.setVisible(true);
    }

    public void updateMarketData(String product, Price bp, int bv, Price sp, int sv) {
        marketDisplay.updateMarketData(product, bp, bv, sp, sv);
    }

    public void updateTicker(String product, Price p, char direction) {
        marketDisplay.updateTicker(product, p, direction);
    }

    public void shutdown() {
        marketDisplay.shutdown();
    }
}
