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
        assertTrue(result.success);
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
    public void testGetCitiesAtDistance2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_CITIES_AT_DISTANCE2 50 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_CITIES_AT_DISTANCE2 200 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_CITIES_AT_DISTANCE2 50 PaisInexistente");
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
        assertTrue(result.success);
        assertEquals("Pais invalido", result.result);
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
        assertTrue(result.success);

        result = Main.execute("REMOVE_COUNTRY PaisInexistente");
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Pais invalido", result.result);
    }

    @Test
    public void testCreativeCommand() {
        assertTrue(Main.parseFiles(new File("test-files/getCountriesLosingPopulation")));

        Result result = Main.execute("GET_COUNTRIES_LOSING_POPULATION 2000 2010");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
        String[] linhas = result.result.split("\n");
        assertArrayEquals(new String[] {
                "Alphaland:300000",
                "Betaland:200000"
        }, linhas);

        result = Main.execute("GET_COUNTRIES_LOSING_POPULATION 2010 2020");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
        linhas = result.result.split("\n");
        assertArrayEquals(new String[] {
                "Alphaland:100000",
                "Gammaland:100000"
        }, linhas);

        result = Main.execute("GET_COUNTRIES_LOSING_POPULATION 2020 2000");
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("", result.result);
    }

    @Test
    public void testCountCities2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("COUNT_CITIES 0");
        assertNotNull(result);
        assertTrue(result.success);
        int total = Integer.parseInt(result.result);
        assertTrue(total > 0);

        result = Main.execute("COUNT_CITIES 1000000000");
        assertNotNull(result);
        assertTrue(result.success);
        int poucas = Integer.parseInt(result.result);
        assertTrue(poucas <= total);
    }

    @Test
    public void testGetCitiesByCountry2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_CITIES_BY_COUNTRY 100 Espanha");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_CITIES_BY_COUNTRY 1 Brasil");
        assertNotNull(result);
        assertTrue(result.success);
        String[] linhas = result.result.split("\n");
        assertEquals(1, linhas.length);
    }

    @Test
    public void testSumPopulations2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("SUM_POPULATIONS PaisQueNaoExiste");
        assertNotNull(result);
        assertTrue(result.success);
        assertTrue(result.result.startsWith("Pais invalido"));

        result = Main.execute("SUM_POPULATIONS Portugal,Espanha,Brasil");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetHistory2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_HISTORY 2020 2024 PaisInexistente");
        assertNotNull(result);
        assertFalse(result.success);

        result = Main.execute("GET_HISTORY 2020 2020 Espanha");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetMissingHistory2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_MISSING_HISTORY 2024 2024");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_MISSING_HISTORY 1900 2024");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetMostPopulous2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_MOST_POPULOUS 1000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_MOST_POPULOUS 2");
        assertNotNull(result);
        assertTrue(result.success);
        String[] linhas = result.result.split("\n");
        assertTrue(linhas.length <= 2);
    }

    @Test
    public void testGetTopCitiesByCountry2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_TOP_CITIES_BY_COUNTRY 1 Espanha");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_TOP_CITIES_BY_COUNTRY -1 Brasil");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetDuplicateCities2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_DUPLICATE_CITIES 1000000000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_DUPLICATE_CITIES 50000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetCountriesGenderGap2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_COUNTRIES_GENDER_GAP 50");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_COUNTRIES_GENDER_GAP 1");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetTopPopulationIncrease2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_TOP_POPULATION_INCREASE 2020 2024");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_TOP_POPULATION_INCREASE 2024 2024");
        assertNotNull(result);
        assertTrue(result.success);
    }

    @Test
    public void testGetDuplicateCitiesDifferentCountries2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES 50000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES 1000000000");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetCitiesAtDistance3() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_CITIES_AT_DISTANCE 10 Espanha");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_CITIES_AT_DISTANCE 500 Andorra");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testGetCitiesAtDistance2_2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_CITIES_AT_DISTANCE2 10 Espanha");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);

        result = Main.execute("GET_CITIES_AT_DISTANCE2 500 Andorra");
        assertNotNull(result);
        assertTrue(result.success);
        assertNotNull(result.result);
    }

    @Test
    public void testInsertCity2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("INSERT_CITY es Toledo 05 85000");
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Inserido com sucesso", result.result);

        Result antes = Main.execute("COUNT_CITIES 0");
        int totalAntes = Integer.parseInt(antes.result);
        Main.execute("INSERT_CITY br Salvador 27 2900000");
        Result depois = Main.execute("COUNT_CITIES 0");
        int totalDepois = Integer.parseInt(depois.result);
        assertEquals(totalAntes + 1, totalDepois);
    }

    @Test
    public void testRemoveCountry2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("REMOVE_COUNTRY Espanha");
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Removido com sucesso", result.result);

        result = Main.execute("REMOVE_COUNTRY Espanha");
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Pais invalido", result.result);
    }

    @Test
    public void testCreativeCommand2() {
        assertTrue(Main.parseFiles(new File("test-files/getCountriesLosingPopulation")));

        Result result = Main.execute("GET_COUNTRIES_LOSING_POPULATION 2000 2020");
        assertNotNull(result);
        assertTrue(result.success);
        String[] linhas = result.result.split("\n");
        assertArrayEquals(new String[] {
                "Alphaland:400000"
        }, linhas);

        result = Main.execute("GET_COUNTRIES_LOSING_POPULATION 2010 2010");
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("", result.result);
    }

}