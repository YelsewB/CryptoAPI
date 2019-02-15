package nl.wesleyblom.crypto.api.repository;

import static org.junit.Assert.*;

import java.util.Optional;
import nl.wesleyblom.crypto.api.model.Currency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit tests for {@link CurrencyRepository}
 *
 * @author Wesley
 * @date 2019-02-12
 */

@RunWith(SpringRunner.class)
@DataJpaTest
public class CurrencyRepositoryTest {

    private static final String TICKER_ABC = "ABC";
    private static final String NAME_ABC = "Alphabet";
    private static final long MARKET_CAP = 10092910;
    private static final long NUMBER_OF_COINS = 1235667890;
    private static final String TICKER_XRP = "XRP";

    @Autowired
    CurrencyRepository currencyRepository;

    @Test
    public void allrecordsShouldBeAvailable() {
        assertEquals(4, currencyRepository.count());
    }

    @Test
    public void createCurrencyShouldReturnData() throws Exception {
        Currency currency = new Currency.CurrencyBuilder()
            .withTicker(TICKER_ABC)
            .withName(NAME_ABC)
            .withMarketCap(MARKET_CAP)
            .withNumberOfCoins(NUMBER_OF_COINS)
            .build();

        currencyRepository.save(currency);

        Optional<Currency> abcCurrency = currencyRepository.findByTicker(TICKER_ABC);

        assertTrue(abcCurrency.isPresent());

        Currency newCurrency = abcCurrency.get();
        assertEquals(TICKER_ABC, currency.getTicker());
        assertEquals(NAME_ABC, currency.getName());
        assertEquals(MARKET_CAP, currency.getMarketCap());
        assertEquals(NUMBER_OF_COINS, currency.getNumberOfCoins());
    }

    @Test
    public void getExistingCurrencyShouldReturnData() throws Exception {

        Optional<Currency> currencyOptional = currencyRepository.findByTicker(TICKER_XRP);

        assertTrue(currencyOptional.isPresent());

        Currency currency = currencyOptional.get();
        assertEquals(TICKER_XRP, currency.getTicker());
        assertEquals("Ripple", currency.getName());
        assertEquals(64750000000l, currency.getMarketCap());
        assertEquals(38590000000l, currency.getNumberOfCoins());
    }

    @Test
    public void getNonExistingCurrencyShouldReturnData() throws Exception {

        Optional<Currency> currencyOptional = currencyRepository.findByTicker("doe");

        assertFalse(currencyOptional.isPresent());
    }

    @Test
    public void findByTickerAndDeleteShouldRemove() {
        Optional<Currency> abcCurrencyBefore = currencyRepository.findByTicker(TICKER_XRP);
        assertTrue(abcCurrencyBefore.isPresent());

        currencyRepository.deleteByTicker(TICKER_XRP);

        Optional<Currency> abcCurrencyAfter = currencyRepository.findByTicker(TICKER_XRP);
        assertFalse(abcCurrencyAfter.isPresent());
        assertEquals(3, currencyRepository.count());
    }
}