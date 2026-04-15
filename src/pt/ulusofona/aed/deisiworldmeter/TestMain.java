package pt.ulusofona.aed.deisiworldmeter;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class TestMain {

    @Test
    public void testToStringPaisComIdMenor700_1() {
        Pais p = new Pais(20, "ad", "and", "Andorra");
        assertEquals("Andorra | 20 | AD | AND", p.toString());
    }

    @Test
    public void testToStringPaisComIdMenor700_2() {
        Pais p = new Pais(620, "pt", "prt", "Portugal");
        assertEquals("Portugal | 620 | PT | PRT", p.toString());
    }

    @Test
    public void testToStringPaisComIdMenor700_3() {
        Pais p = new Pais(76, "br", "bra", "Brasil");
        assertEquals("Brasil | 76 | BR | BRA", p.toString());
    }

    @Test
    public void testToStringPaisComIdMenor700_4() {
        Pais p = new Pais(4, "af", "afg", "Afeganistão");
        assertEquals("Afeganistão | 4 | AF | AFG", p.toString());
    }

    @Test
    public void testToStringPaisComIdMenor700_5() {
        Pais p = new Pais(8, "al", "alb", "Albânia");
        assertEquals("Albânia | 8 | AL | ALB", p.toString());
    }

    @Test
    public void testToStringPaisComIdMaior700_1() {
        Pais p = new Pais(724, "es", "esp", "Espanha");
        p.numeroDeRegistosPopulacao = 3;
        assertEquals("Espanha | 724 | ES | ESP | 3", p.toString());
    }

    @Test
    public void testToStringPaisComIdMaior700_2() {
        Pais p = new Pais(840, "us", "usa", "Estados Unidos");
        p.numeroDeRegistosPopulacao = 5;
        assertEquals("Estados Unidos | 840 | US | USA | 5", p.toString());
    }

    @Test
    public void testToStringPaisComIdMaior700_3() {
        Pais p = new Pais(710, "za", "zaf", "África do Sul");
        p.numeroDeRegistosPopulacao = 0;
        assertEquals("África do Sul | 710 | ZA | ZAF | 0", p.toString());
    }

    @Test
    public void testToStringPaisComIdMaior700_4() {
        Pais p = new Pais(800, "ug", "uga", "Uganda");
        p.numeroDeRegistosPopulacao = 10;
        assertEquals("Uganda | 800 | UG | UGA | 10", p.toString());
    }

    @Test
    public void testToStringPaisComIdMaior700_5() {
        Pais p = new Pais(716, "zw", "zwe", "Zimbabwe");
        p.numeroDeRegistosPopulacao = 2;
        assertEquals("Zimbabwe | 716 | ZW | ZWE | 2", p.toString());
    }

    @Test
    public void testToStringCidade_1() {
        Cidade c = new Cidade("pt", "lisboa", "14", 517798.0, 38.716667, -9.133333);
        assertEquals("lisboa | PT | 14 | 517798 | (38.716667,-9.133333)", c.toString());
    }

    @Test
    public void testToStringCidade_2() {
        Cidade c = new Cidade("es", "madrid", "08", 3266126.0, 40.4165, -3.7026);
        assertEquals("madrid | ES | 08 | 3266126 | (40.4165,-3.7026)", c.toString());
    }

    @Test
    public void testToStringCidade_3() {
        Cidade c = new Cidade("br", "sao paulo", "27", 11253503.0, -23.5475, -46.63611);
        assertEquals("sao paulo | BR | 27 | 11253503 | (-23.5475,-46.63611)", c.toString());
    }

    @Test
    public void testToStringCidade_4() {
        Cidade c = new Cidade("ad", "andorra la vella", "07", 20430.0, 42.5, 1.5166667);
        assertEquals("andorra la vella | AD | 07 | 20430 | (42.5,1.5166667)", c.toString());
    }

    @Test
    public void testToStringCidade_5() {
        Cidade c = new Cidade("us", "new york", "NY", 8336817.0, 40.7143, -74.006);
        assertEquals("new york | US | NY | 8336817 | (40.7143,-74.006)", c.toString());
    }

    @Test
    public void testLeituraFicheirosSemErros_1() {
        Main.parseFiles(new File("test-files"));
        ArrayList<Object> paises = Main.getObjects(TipoEntidade.PAIS);
        assertEquals(5, paises.size());
    }

    @Test
    public void testLeituraFicheirosSemErros_2() {
        Main.parseFiles(new File("test-files"));
        ArrayList<Object> cidades = Main.getObjects(TipoEntidade.CIDADE);
        assertEquals(7, cidades.size());
    }

    @Test
    public void testLeituraFicheirosSemErros_3() {
        Main.parseFiles(new File("test-files"));
        ArrayList<Object> paises = Main.getObjects(TipoEntidade.PAIS);
        assertEquals("Andorra | 20 | AD | AND", paises.get(0).toString());
    }

    @Test
    public void testLeituraFicheirosSemErros_4() {
        Main.parseFiles(new File("test-files"));
        ArrayList<Object> cidades = Main.getObjects(TipoEntidade.CIDADE);
        assertEquals("andorra la vella | AD | 07 | 20430 | (42.5,1.5166667)", cidades.get(0).toString());
    }

    @Test
    public void testLeituraFicheirosSemErros_5() {
        Main.parseFiles(new File("test-files"));
        ArrayList<Object> invalidos = Main.getObjects(TipoEntidade.INPUT_INVALIDO);
        assertEquals("paises.csv | 5 | 0 | -1", invalidos.get(0).toString());
    }
}