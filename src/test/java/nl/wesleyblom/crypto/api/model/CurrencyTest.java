package nl.wesleyblom.crypto.api.model;

import static org.junit.Assert.*;

import nl.wesleyblom.crypto.api.model.Currency.CurrencyBuilder;
import org.junit.Test;

/**
 * Unit tests for {@link Currency}
 *
 * @author Wesley
 * @date 2019-02-12
 */



public class CurrencyTest {

    private static final String TICKER = "XRP";
    private static final String NAME = "Ripple";
    private static final String VALIDATE_EXCEPTION_MESSAGE = "Name or Ticker is invalid";
    private static final int NUMBER_OF_COINS = 1234;
    private static final long MARKET_CAP = 432111123213l;
    private static final String CURRENCY_STRING = "Currency{ticker='XRP', name='Ripple', numberOfCoins=1234, marketCap=432111123213}";

    @Test
    public void createCurrencyReturnsObject() {
        Currency currency = null;
        try {
            currency = new CurrencyBuilder().withTicker(TICKER).withName(NAME).withNumberOfCoins(NUMBER_OF_COINS).withMarketCap(MARKET_CAP).build();
        } catch (Exception e) {
            fail("Exception thrown");
        }
        assertNotNull(currency);
        assertEquals(TICKER, currency.getTicker());
        assertEquals(NAME, currency.getName());
        assertEquals(NUMBER_OF_COINS, currency.getNumberOfCoins());
        assertEquals(MARKET_CAP, currency.getMarketCap());
        assertEquals(CURRENCY_STRING, currency.toString());
    }

    @Test
    public void createCurrencyWithoutNameReturnsException() {
        try {
            Currency currency = new CurrencyBuilder().withTicker(TICKER).build();
            fail("Should throw exception");
        } catch (Exception e) {
            assertEquals(VALIDATE_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void createCurrencyWithNameNullReturnsException() {
        try {
            Currency currency = new CurrencyBuilder().withName(null).build();
            fail("Should throw exception");
        } catch (Exception e) {
            assertEquals(VALIDATE_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void createCurrencyWithLongTickerReturnsException() {
        try {
            Currency currency =
                new CurrencyBuilder()
                    .withTicker("LONGTICKER").withName(NAME).build();
            fail("Should throw exception");
        } catch (Exception e) {
            assertEquals(VALIDATE_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void creasteCurrencyWithOutTickerReturnsException() {
        try {
            Currency currency = new CurrencyBuilder().withName(NAME).build();
            fail("Should throw exception");
        } catch (Exception e) {
            assertEquals(VALIDATE_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

}