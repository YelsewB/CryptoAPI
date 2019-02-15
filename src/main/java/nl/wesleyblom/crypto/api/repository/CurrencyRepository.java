package nl.wesleyblom.crypto.api.repository;

import java.util.Optional;
import nl.wesleyblom.crypto.api.model.Currency;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for {@link Currency}
 *
 * @author Wesley
 * @date 2019-02-12
 */


public interface CurrencyRepository extends PagingAndSortingRepository<Currency, String> {

    /**
     * Method to filter {@link Currency} by Ticker
     * Same as the default method {@link #findById(Object)}  but more descriptive.
     *
     * @param ticker The short name of the {@link Currency}
     * @return Returns an {@link Optional<Currency>}
     */
    Optional<Currency> findByTicker(String ticker);

    /**
     * Method to check if a  {@link Currency} with a specific ticker exists
     * Same as the default method {@link #existsById(Object)} but more descriptive.
     *
     * @param ticker The short name of the {@link Currency}
     * @return Returns a boolean
     */
    boolean existsByTicker(String ticker);

    /**
     * Method to delete a {@link Currency} by ticker.
     * Same as the default method {@link #deleteById(Object)} but more descriptive.
     *
     * @param ticker The short name of the {@link Currency}
     */
    void deleteByTicker(String ticker);
}
