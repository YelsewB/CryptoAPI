package nl.wesleyblom.crypto.api;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.Valid;
import nl.wesleyblom.crypto.api.model.Currency;
import nl.wesleyblom.crypto.api.repository.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint with CRUD operations for {@link Currency}
 *
 * @author Wesley
 * @date 2019-02-12
 */

@RestController
@RequestMapping("/api/currencies")
public class CurrencyRestController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRestController.class);

    @Autowired
    CurrencyRepository currencyRepository;

    /**
     * Endpoint to retrieve all {@link Currency} objects
     * Paging and sorting are possible due to the {@param Pageable}
     *
     * @return
     *       {@link HttpStatus#OK} {@link ResponseEntity<List<Currency>>} containing all {@link Currency} object matching the {@link Pageable} options
     *       {@link HttpStatus#NOT_FOUND} when no {@link Currency} (matching {@link Pageable} options) can be found
     */
    @GetMapping
    public ResponseEntity<List<Currency>> getAll(@PageableDefault(size = 10, sort = "ticker") Pageable pageable) {
        Page<Currency> page = currencyRepository.findAll(pageable);

        if (page.getContent().size() > 0) {
            logger.info("Request received all items. - serving");
            return ResponseEntity.status(HttpStatus.OK).body(page.getContent());
        }

        logger.info("Request received all items - none found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    /**
     * Endpoint to retrieve a specific {@link Currency} specified by ticker
     *
     * @param ticker The short name of the {@link Currency} to be returned
     * @return Returns {@link HttpStatus#OK} with {@link Currency} body when a matching object has been found
     *         Returns {@link HttpStatus#NOT_FOUND} when no {@link Currency}  with matching ticker can be found
     */
    @GetMapping("/{ticker}")
    public ResponseEntity<Currency> getCurrency(@PathVariable String ticker) {
        Optional<Currency> currency = currencyRepository.findByTicker(ticker);
        if (currency.isPresent()) {
            logger.info("Request for known ticker [{}].", ticker);
            return ResponseEntity.ok(currency.get());
        }
        logger.info("Request for unknown ticker [{}].", ticker);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }


    /**
     * Endpoint to create a new currency
     *
     * @param currency  A JSON representation of {@link Currency} with the information to be created
     * @return Returns {@link HttpStatus#BAD_REQUEST} when JSON body is invalid
     *         Returns {@link HttpStatus#CONFLICT} when {@link Currency} with ther same ticker already exist
     *         Returns {@link HttpStatus#CREATED} when {@link Currency} has been created
     */
    @PostMapping
    public ResponseEntity createCurrency(@Valid @RequestBody Currency currency, BindingResult bindingResult) {
        if ( bindingResult.hasErrors() ) {
            logger.info("Bad request to create new Currency: [{}]", currency.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        logger.info("Request to create new Currency: [{}]", currency.toString());

        if (currencyRepository.existsByTicker(currency.getTicker())) {
            logger.info("Currency with ticker [{}] already exists", currency.getTicker());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(currency);
        }

        currencyRepository.save(currency);
        logger.info("Currency with ticker [{}] created.", currency.getTicker());

        return ResponseEntity.status(HttpStatus.CREATED).body(currency);
    }

    /**
     * Endpoint to update an existing {@link Currency} The {@link Currency} will be recreated when the ticker has been changed.
     *
     * @param ticker The short name of the {@link Currency} to be updated
     * @param currency  A JSON representation of {@link Currency} with the new information
     *
     * @return Returns {@link HttpStatus#BAD_REQUEST} when JSON body is invalid
     *         Returns {@link HttpStatus#CONFLICT} when {@link Currency} with the new ticker already exist
     *         Returns {@link HttpStatus#OK} when given {@link Currency} has been updated
     *         Returns {@link HttpStatus#NOT_FOUND} when {@link Currency} does not exist
     */
    @PutMapping("/{ticker}")
    @Transactional
    public ResponseEntity updateCurrency(@PathVariable String ticker, @Valid @RequestBody Currency currency, BindingResult bindingResult) {
        if ( bindingResult.hasErrors() ) {
            logger.info("Bad request to update Currency with Ticker [{}]: [{}]", ticker, currency.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        logger.info("Request to update Currency with ticker [{}]: [{}]", ticker, currency.toString());

        if (currencyRepository.existsByTicker(ticker)) {

            if (!currency.getTicker().equalsIgnoreCase(ticker)) {

                if (currencyRepository.existsByTicker(currency.getTicker())) {
                    logger.info("Currency with ticker [{}] already exists", currency.getTicker());
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(currency);
                }

                currencyRepository.deleteByTicker(ticker);
                logger.info("Currency with ticker [{}] deleted.", ticker);
            }

            currencyRepository.save(currency);
            logger.info("Currency with ticker [{}] updated.", currency.getTicker());

            return ResponseEntity.status(HttpStatus.OK).body(currency);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(currency);
    }

    /**
     * Endpoint to delete a {@link Currency}
     *
     * @param ticker The short name of the {@link Currency} to be deleted
     * @return Returns {@link HttpStatus#OK} on successful deletion
     *         Returns {@link HttpStatus#NOT_FOUND} when {@link Currency} does not exist
     */
    @DeleteMapping("/{ticker}")
    @Transactional
    public ResponseEntity<Currency> updateCurrency(@PathVariable String ticker) {
        logger.info("Request to delete Currency with ticker [{}]", ticker);

        if (currencyRepository.existsByTicker(ticker)) {
            currencyRepository.deleteByTicker(ticker);
            logger.info("Currency with ticker [{}] deleted.", ticker);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

}
