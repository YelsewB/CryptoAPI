package nl.wesleyblom.crypto.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.wesleyblom.crypto.api.model.Currency;
import nl.wesleyblom.crypto.api.repository.CurrencyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * Unit tests for {@link CurrencyRestController}
 *
 * @author Wesley
 * @date 2019-02-13
 */

@RunWith(SpringRunner.class)
@WebMvcTest(CurrencyRestController.class)
@EnableSpringDataWebSupport
public class CurrencyRestControllerTest {

    private static final String TICKER_XRP = "XRP";
    private static final String NAME_RIPPLE = "Ripple";
    private static final String API_URL_XRP = "/api/currencies/XRP";
    private static final String API_URL = "/api/currencies";

    private static final String JSON_INVALID_MARKETCAP = "{\"ticker\":\"XRP\",\"name\":\"Ripple\",\"numberOfCoins\":\"123\",\"marketCap\":\"fdsa\"}";
    private static final String JSON_INVALID_NUMBER = "{\"ticker\":\"XRP\",\"name\":\"Ripple\",\"numberOfCoins\":\"fdsa\",\"marketCap\":0}";
    private static final String JSON_INVALID_TICKER = "{\"ticker\":\"LONGTICKERNAME\",\"name\":\"Ripple\",\"numberOfCoins\":0,\"marketCap\":0}";
    private static final String JSON_EMPTY_TICKER = "{\"ticker\":\"\",\"name\":\"Ripple\",\"numberOfCoins\":0,\"marketCap\":0}";
    private static final String JSON_NO_NAME = "{\"ticker\":\"XRP\",\"numberOfCoins\":0,\"marketCap\":0}";
    private static final String TICKER = "ticker";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CurrencyRepository currencyRepository;

    private JacksonTester<Currency> jsonCurrency;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void willRetrieveByTickerWhenExists() throws Exception {
        Currency currency = new Currency.CurrencyBuilder().withTicker(TICKER_XRP).withName(NAME_RIPPLE).build();
        given(currencyRepository.findByTicker(TICKER_XRP)).willReturn(Optional.of(currency));

        MockHttpServletResponse response = mvc.perform(get(API_URL_XRP).accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(response.getContentAsString(), jsonCurrency.write(currency).getJson());
    }

    @Test
    public void willErrorWhenCurrencyNotExists() throws Exception {
        MockHttpServletResponse response = mvc.perform(get(API_URL_XRP).accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void updateExistingCurrencyWithValidInfoReturns200() throws Exception {
        Currency currency = new Currency.CurrencyBuilder().withTicker(TICKER_XRP).withName(NAME_RIPPLE).build();

        given(currencyRepository.existsByTicker(TICKER_XRP)).willReturn(true);

        MockHttpServletResponse response =
            mvc.perform(put(API_URL_XRP).contentType(MediaType.APPLICATION_JSON).content(jsonCurrency.write(currency).getJson())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(response.getContentAsString(), jsonCurrency.write(currency).getJson());
    }

    @Test
    public void updateExistingCurrencyWithLongTickerReturns400() throws Exception {
        given(currencyRepository.existsByTicker(TICKER_XRP)).willReturn(true);

        MockHttpServletResponse response =
            mvc.perform(put(API_URL_XRP).contentType(MediaType.APPLICATION_JSON).content(JSON_INVALID_TICKER).accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void updateExistingCurrencyWithInvalidNumberOfCoinsReturns400() throws Exception {
        given(currencyRepository.existsByTicker(TICKER_XRP)).willReturn(true);

        MockHttpServletResponse response =
            mvc.perform(put(API_URL_XRP).contentType(MediaType.APPLICATION_JSON).content(JSON_INVALID_NUMBER).accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void updateExistingCurrencyWithInvalidMarketCapReturns400() throws Exception {
        given(currencyRepository.existsByTicker(TICKER_XRP)).willReturn(true);

        MockHttpServletResponse response =
            mvc.perform(put(API_URL_XRP).contentType(MediaType.APPLICATION_JSON).content(JSON_INVALID_MARKETCAP).accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void updateNonExistingCurrencyReturns404() throws Exception {
        Currency currency = new Currency.CurrencyBuilder().withTicker(TICKER_XRP).withName(NAME_RIPPLE).build();

        given(currencyRepository.existsByTicker(TICKER_XRP)).willReturn(false);

        MockHttpServletResponse response =
            mvc.perform(put(API_URL_XRP).contentType(MediaType.APPLICATION_JSON).content(jsonCurrency.write(currency).getJson())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void updateExistingCurrencyWithExistingNewTickerReturns409() throws Exception {
        Currency currency = new Currency.CurrencyBuilder().withTicker(TICKER_XRP).withName(NAME_RIPPLE).build();

        given(currencyRepository.existsByTicker("OLD")).willReturn(true);
        given(currencyRepository.existsByTicker(TICKER_XRP)).willReturn(true);

        MockHttpServletResponse response =
            mvc.perform(put("/api/currencies/OLD").contentType(MediaType.APPLICATION_JSON).content(jsonCurrency.write(currency).getJson())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    public void renameExistingCurrencyWithNewTickerReturns200() throws Exception {
        Currency currency = new Currency.CurrencyBuilder().withTicker(TICKER_XRP).withName(NAME_RIPPLE).build();

        given(currencyRepository.existsByTicker("OLD")).willReturn(true);
        given(currencyRepository.existsByTicker(TICKER_XRP)).willReturn(false);

        MockHttpServletResponse response =
            mvc.perform(put("/api/currencies/OLD").contentType(MediaType.APPLICATION_JSON).content(jsonCurrency.write(currency).getJson())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void deleteExistingCurrencyReturns200() throws Exception {
        given(currencyRepository.existsByTicker(TICKER_XRP)).willReturn(true);

        MockHttpServletResponse response = mvc.perform(delete(API_URL_XRP).accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void deleteNonExistingCurrencyReturns404() throws Exception {
        given(currencyRepository.existsByTicker(TICKER_XRP)).willReturn(false);

        MockHttpServletResponse response = mvc.perform(delete(API_URL_XRP).accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void createValidCurrencyReturns201() throws Exception {
        Currency currency = new Currency.CurrencyBuilder().withTicker(TICKER_XRP).withName(NAME_RIPPLE).build();

        MockHttpServletResponse response =
            mvc.perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(jsonCurrency.write(currency).getJson())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(response.getContentAsString(), jsonCurrency.write(currency).getJson());
    }

    @Test
    public void createDuplicateCurrencyReturns409() throws Exception {
        Currency currency = new Currency.CurrencyBuilder().withTicker(TICKER_XRP).withName(NAME_RIPPLE).build();

        given(currencyRepository.existsByTicker(TICKER_XRP)).willReturn(true);

        MockHttpServletResponse response =
            mvc.perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(jsonCurrency.write(currency).getJson())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals(response.getContentAsString(), jsonCurrency.write(currency).getJson());
    }

    @Test
    public void createCurrencyWithLongTickerReturns400() throws Exception {
        MockHttpServletResponse response =
            mvc.perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(JSON_INVALID_TICKER).accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void createCurrencyWithInvalidNumberOfCoinsReturns400() throws Exception {
        MockHttpServletResponse response =
            mvc.perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(JSON_INVALID_NUMBER).accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void createCurrencyWithInvalidMarketCapReturns400() throws Exception {
        MockHttpServletResponse response =
            mvc.perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(JSON_INVALID_MARKETCAP).accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void createCurrencyWithEmptyTickerReturns400() throws Exception {
        MockHttpServletResponse response =
            mvc.perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(JSON_EMPTY_TICKER).accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void createCurrencyWithoutNameReturns400() throws Exception {
        MockHttpServletResponse response =
            mvc.perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(JSON_NO_NAME).accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void getAllWithCurrenciesReturns200() throws Exception {
        Currency currencyXRP = new Currency.CurrencyBuilder().withTicker(TICKER_XRP).withName(NAME_RIPPLE).build();

        List<Currency> currencyList = new ArrayList<>();
        currencyList.add(currencyXRP);

        Page<Currency> page = new PageImpl<>(currencyList);

        given(currencyRepository.findAll(PageRequest.of(0, 10, new Sort(Direction.ASC, TICKER)))).willReturn(page);

        MockHttpServletResponse response = mvc.perform(get(API_URL).accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void getAllWithoutCurrenciesReturn404() throws Exception {
        List<Currency> currencyList = new ArrayList<>();

        Page<Currency> page = new PageImpl<>(currencyList);

        given(currencyRepository.findAll(PageRequest.of(0, 10, new Sort(Direction.ASC, TICKER)))).willReturn(page);

        MockHttpServletResponse response = mvc.perform(get(API_URL).accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}