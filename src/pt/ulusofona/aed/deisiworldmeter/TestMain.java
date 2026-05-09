package pt.ulusofona.aed.deisiworldmeter;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class TestMain {

    @Test
    public void testCountCities() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("COUNT_CITIES 100000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("COUNT_CITIES 500000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetCitiesByCountry() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_CITIES_BY_COUNTRY 2 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        assertNull(result.error);

        result = Main.execute("GET_CITIES_BY_COUNTRY 3 PaisQueNaoExiste");
        assertNotNull(result);
        assertFalse(result.success);
        assertNotNull(result.error);
    }

    @Test
    public void testSumPopulations() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("SUM_POPULATIONS Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("SUM_POPULATIONS Portugal,Espanha");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetHistory() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_HISTORY 2020 2024 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_HISTORY 1800 1801 Portugal");
        assertNotNull(result);
    }

    @Test
    public void testGetMissingHistory() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_MISSING_HISTORY 2020 2024");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_MISSING_HISTORY 1950 2024");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetMostPopulous() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_MOST_POPULOUS 5");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_MOST_POPULOUS 1");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetTopCitiesByCountry() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_TOP_CITIES_BY_COUNTRY 3 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_TOP_CITIES_BY_COUNTRY -1 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_TOP_CITIES_BY_COUNTRY 3 PaisInexistente");
        assertNotNull(result);
        assertFalse(result.success);
    }

    @Test
    public void testGetDuplicateCities() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_DUPLICATE_CITIES 100000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_DUPLICATE_CITIES 0");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetCountriesGenderGap() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_COUNTRIES_GENDER_GAP 0");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_COUNTRIES_GENDER_GAP 5");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetTopPopulationIncrease() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_TOP_POPULATION_INCREASE 2000 2024");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_TOP_POPULATION_INCREASE 1950 2000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetDuplicateCitiesDifferentCountries() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES 0");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES 100000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetCitiesAtDistance() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_CITIES_AT_DISTANCE 27 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_CITIES_AT_DISTANCE 100 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_CITIES_AT_DISTANCE 50 PaisInexistente");
        assertNotNull(result);
        assertFalse(result.success);
    }

    @Test
    public void testInsertCity() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("INSERT_CITY pt Lisboa 14 500000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_TOP_CITIES_BY_COUNTRY 1 Portugal");
        assertNotNull(result);
        assertTrue(result.success);

        result = Main.execute("INSERT_CITY xx Lisboa 14 500000");
        assertNotNull(result);
        assertFalse(result.success);
    }

    @Test
    public void testRemoveCountry() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("REMOVE_COUNTRY Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_TOP_CITIES_BY_COUNTRY 3 Portugal");
        assertNotNull(result);
        assertFalse(result.success);

        result = Main.execute("REMOVE_COUNTRY PaisInexistente");
        assertNotNull(result);
        assertFalse(result.success);
    }

}