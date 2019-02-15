package nl.wesleyblom.crypto.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Currency model. Also the Entity for the database mapping
 *
 * @author Wesley
 * @date 2019-02-12
 */


@Entity
public class Currency {

    @Id
    @Column(unique = true, length = 5)
    @Size(min = 1, max = 5, message = "Ticker needs a length between 1 and 5")
    @NotNull
    @NotBlank
    private String ticker;
    @Column(length = 25)
    @Size(min = 1, max = 25, message = "Name needs a length between 1 and 25")
    @NotNull
    @NotBlank
    private String name;
    private long numberOfCoins;
    private long marketCap;

    /**
     * empty constructor to make JPA happy :-)
     */
    private Currency() {
    }

    /**
     * Constructor for a Currency object. Can be created trough {@link CurrencyBuilder}
     *
     * @param ticker The short name of the Currency
     * @param name The full name of the Currency
     * @param numberOfCoins The amount of coins available for this Currency
     * @param marketCap The market cap for this Currency
     */
    private Currency(String ticker, String name, long numberOfCoins, long marketCap) {
        this.ticker = ticker;
        this.name = name;
        this.numberOfCoins = numberOfCoins;
        this.marketCap = marketCap;
    }

    /**
     * @return Returns the short name of the Currency
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * @return Returns the full name of the Currency
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the amount of coins available for the Currency
     */
    public long getNumberOfCoins() {
        return numberOfCoins;
    }

    /**
     * @return Returns the market cap of the Currency
     */
    public long getMarketCap() {
        return marketCap;
    }

    @Override
    public String toString() {
        return "Currency{" +
            "ticker='" + ticker + '\'' +
            ", name='" + name + '\'' +
            ", numberOfCoins=" + numberOfCoins +
            ", marketCap=" + marketCap +
            '}';
    }

    /**
     * Builder for a {@link Currency} object
     */
    public static class CurrencyBuilder {

        private String ticker;
        private String name;
        private long numberOfCoins = 0;
        private long marketCap = 0;

        /**
         * Sets the short name for the {@link Currency} object to be created
         *
         * @param ticker The short name of the Currency
         * @return The {@link CurrencyBuilder} object with the ticker set
         */
        public CurrencyBuilder withTicker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        /**
         * Sets the name for the {@link Currency} object to be created
         *
         * @param name The full name of the Currency
         * @return The {@link CurrencyBuilder} object with the name set
         */
        public CurrencyBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the number of coins available for the {@link Currency} object to be created
         *
         * @param numberOfCoins The amount of coins available for this Currency
         * @return The {@link CurrencyBuilder} object with the number of coins set
         */
        public CurrencyBuilder withNumberOfCoins(long numberOfCoins) {
            this.numberOfCoins = numberOfCoins;
            return this;
        }

        /**
         * Sets the market cap for the {@link Currency} object to be created
         *
         * @param marketCap The marketcap for this Currency
         * @return The {@link CurrencyBuilder} object with the market cap set
         */
        public CurrencyBuilder withMarketCap(long marketCap) {
            this.marketCap = marketCap;
            return this;
        }

        /**
         * Calling this method will return a {@link Currency} object with the given arguments.
         *
         * @return The created {@link Currency} object
         * @throws IllegalArgumentException when required parameters are not valid.
         */
        public Currency build() throws IllegalArgumentException {
            if (validate()) {
                return new Currency(ticker, name, numberOfCoins, marketCap);
            }
            throw new IllegalArgumentException("Name or Ticker is invalid");
        }

        /**
         * Validates if required parameters are valid
         *
         * ticker: Can not be null Can not be empty Max length of 5
         *
         * name: Can not be null Can not be empty Max length of 25
         *
         * @return true or false depending on the validation
         */
        private boolean validate() {
            // Ticker can not be empty
            if (ticker == null || ticker.isEmpty() || ticker.length() > 5) {
                return false;
            }
            // Name can not be empty
            if (name == null || name.isEmpty() || name.length() > 25) {
                return false;
            }
            return true;
        }
    }
}
