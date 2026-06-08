package pt.ulusofona.aed.deisiworldmeter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static ArrayList<Pais> listaDePaises = new ArrayList<>();
    static ArrayList<Cidade> listaDeCidades = new ArrayList<>();
    static ArrayList<RegistoPopulacao> listaDePopulacao = new ArrayList<>();

    static HashMap<Integer, Pais> paisPorId = new HashMap<>();
    static HashMap<String, Pais> paisPorAlfa2 = new HashMap<>();
    static HashMap<String, Pais> paisPorAlfa3 = new HashMap<>();
    static HashMap<String, ArrayList<Cidade>> cidadesPorPais = new HashMap<>();
    static HashMap<Integer, ArrayList<RegistoPopulacao>> populacaoPorPais = new HashMap<>();

    static EstatisticasFicheiro estatisticasPaises;
    static EstatisticasFicheiro estatisticasCidades;
    static EstatisticasFicheiro estatisticasPopulacao;

    public static boolean parseFiles(File pasta) {
        listaDePaises.clear();
        listaDeCidades.clear();
        listaDePopulacao.clear();
        paisPorId.clear();
        paisPorAlfa2.clear();
        paisPorAlfa3.clear();
        cidadesPorPais.clear();
        populacaoPorPais.clear();

        File ficheiroPaises = new File(pasta, "paises.csv");
        File ficheiroCidades = new File(pasta, "cidades.csv");
        File ficheiroPopulacao = new File(pasta, "populacao.csv");

        if (!ficheiroPaises.exists() || !ficheiroCidades.exists() || !ficheiroPopulacao.exists()) {
            return false;
        }

        estatisticasPaises = new EstatisticasFicheiro("paises.csv");
        estatisticasCidades = new EstatisticasFicheiro("cidades.csv");
        estatisticasPopulacao = new EstatisticasFicheiro("populacao.csv");


        lerFicheiroPaises(ficheiroPaises);
        lerFicheiroCidades(ficheiroCidades);
        lerFicheiroPopulacao(ficheiroPopulacao);

        ArrayList<Pais> paisesComCidades = new ArrayList<>();
        for (int i = 0; i < listaDePaises.size(); i++) {
            Pais p = listaDePaises.get(i);
            String alfa2 = p.codigoAlfa2.toLowerCase();
            if (cidadesPorPais.containsKey(alfa2) && !cidadesPorPais.get(alfa2).isEmpty()) {
                paisesComCidades.add(p);
            } else {
                estatisticasPaises.linhasValidas--;
                estatisticasPaises.linhasInvalidas++;
                if (estatisticasPaises.primeiraLinhaInvalida == -1
                        || p.numeroLinha < estatisticasPaises.primeiraLinhaInvalida) {
                    estatisticasPaises.primeiraLinhaInvalida = p.numeroLinha;
                }
            }
        }
        listaDePaises = paisesComCidades;

        return true;
    }

    public static Result execute(String comando) {
        if (comando == null || comando.trim().isEmpty()) {
            return new Result(false, "Comando invalido", null);
        }

        String[] partes = comando.trim().split(" ", 2);
        String nomeComando = partes[0].toUpperCase();
        String parametros = partes.length > 1 ? partes[1].trim() : "";

        Result resultado;
        switch (nomeComando) {
            case "COUNT_CITIES":
                resultado = countCities(Integer.parseInt(parametros));
                break;
            case "GET_CITIES_BY_COUNTRY": {
                String[] args = parametros.split(" ", 2);
                int n = Integer.parseInt(args[0]);
                String nomePais = args[1].trim();
                resultado = getCitiesByCountry(n, nomePais);
                break;
            }
            case "SUM_POPULATIONS":
                resultado = sumPopulations(parametros);
                break;
            case "GET_HISTORY": {
                String[] args = parametros.split(" ");
                int anoInicio = Integer.parseInt(args[0]);
                int anoFim = Integer.parseInt(args[1]);
                String nomePais = parametros.split(" ", 3)[2].trim();
                resultado = getHistory(anoInicio, anoFim, nomePais);
                break;
            }
            case "GET_MISSING_HISTORY": {
                String[] args = parametros.split(" ");
                int anoInicio = Integer.parseInt(args[0]);
                int anoFim = Integer.parseInt(args[1]);
                resultado = getMissingHistory(anoInicio, anoFim);
                break;
            }
            case "GET_MOST_POPULOUS":
                resultado = getMostPopulous(Integer.parseInt(parametros));
                break;
            case "GET_TOP_CITIES_BY_COUNTRY": {
                String[] args = parametros.split(" ", 2);
                int n = Integer.parseInt(args[0]);
                String nomePais = args[1].trim();
                resultado = getTopCitiesByCountry(n, nomePais);
                break;
            }
            case "GET_DUPLICATE_CITIES":
                resultado = getDuplicateCities(Integer.parseInt(parametros));
                break;
            case "GET_COUNTRIES_GENDER_GAP":
                resultado = getCountriesGenderGap(Double.parseDouble(parametros));
                break;
            case "GET_TOP_POPULATION_INCREASE": {
                String[] args = parametros.split(" ");
                int anoInicio = Integer.parseInt(args[0]);
                int anoFim = Integer.parseInt(args[1]);
                resultado = getTopPopulationIncrease(anoInicio, anoFim);
                break;
            }
            case "GET_COUNTRIES_LOSING_POPULATION": {
                String[] args = parametros.split(" ");
                int anoInicio = Integer.parseInt(args[0]);
                int anoFim = Integer.parseInt(args[1]);
                resultado = getCountriesLosingPopulation(anoInicio, anoFim);
                break;
            }
            case "GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES":
                resultado = getDuplicateCitiesDifferentCountries(Integer.parseInt(parametros));
                break;
            case "GET_CITIES_AT_DISTANCE": {
                String[] args = parametros.split(" ", 2);
                int distancia = Integer.parseInt(args[0]);
                String nomePais = args[1].trim();
                resultado = getCitiesAtDistance(distancia, nomePais);
                break;
            }
            case "GET_CITIES_AT_DISTANCE2": {
                String[] args = parametros.split(" ", 2);
                int distancia = Integer.parseInt(args[0]);
                String nomePais = args[1].trim();
                resultado = getCitiesAtDistance2(distancia, nomePais);
                break;
            }
            case "INSERT_CITY": {
                try {
                    String[] args = parametros.split("\\s+");
                    if (args.length < 4) {
                        resultado = new Result(false, "Parametros invalidos", null);
                        break;
                    }
                    String alfa2 = args[0].trim();
                    double populacao = Double.parseDouble(args[args.length - 1].trim());
                    String regiao = args[args.length - 2].trim();
                    StringBuilder nomeCidadeSb = new StringBuilder();
                    for (int i = 1; i < args.length - 2; i++) {
                        if (i > 1) {
                            nomeCidadeSb.append(" ");
                        }
                        nomeCidadeSb.append(args[i]);
                    }
                    String nomeCidade = nomeCidadeSb.toString().trim();
                    resultado = insertCity(alfa2, nomeCidade, regiao, populacao);
                } catch (Exception e) {
                    resultado = new Result(false, "Parametros invalidos", null);
                }
                break;
            }
            case "REMOVE_COUNTRY":
                resultado = removeCountry(parametros.trim());
                break;
            default:
                return new Result(false, "Comando invalido", null);
        }

        return resultado;
    }

    private static void lerFicheiroPaises(File ficheiro) {
        try {
            Scanner leitor = new Scanner(ficheiro);

            if (leitor.hasNextLine()) {
                leitor.nextLine();
            }

            int numeroLinha = 1;

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                numeroLinha++;

                String[] partes = linha.split(",");

                if (partes.length < 4) {
                    estatisticasPaises.registarLinhaInvalida(numeroLinha);
                    continue;
                }

                try {
                    int id = Integer.parseInt(partes[0].trim());
                    String codigoAlfa2 = partes[1].trim();
                    String codigoAlfa3 = partes[2].trim();
                    String nome = partes[3].trim();

                    if (codigoAlfa2.isEmpty() || codigoAlfa3.isEmpty() || nome.isEmpty()) {
                        estatisticasPaises.registarLinhaInvalida(numeroLinha);
                        continue;
                    }

                    if (paisExisteComId(id) || paisExisteComAlfa2(codigoAlfa2) || paisExisteComAlfa3(codigoAlfa3)) {
                        estatisticasPaises.registarLinhaInvalida(numeroLinha);
                        continue;
                    }

                    Pais novoPais = new Pais(id, codigoAlfa2, codigoAlfa3, nome);
                    novoPais.numeroLinha = numeroLinha;
                    listaDePaises.add(novoPais);
                    paisPorId.put(id, novoPais);
                    paisPorAlfa2.put(codigoAlfa2.toLowerCase(), novoPais);
                    paisPorAlfa3.put(codigoAlfa3.toLowerCase(), novoPais);
                    estatisticasPaises.linhasValidas++;

                } catch (NumberFormatException e) {
                    estatisticasPaises.registarLinhaInvalida(numeroLinha);
                }
            }
            leitor.close();

        } catch (FileNotFoundException e) {
            System.out.println("Erro ao ler o ficheiro de países: " + e.getMessage());
        }
    }

    private static void lerFicheiroCidades(File ficheiro) {
        try {
            Scanner leitor = new Scanner(ficheiro);

            if (leitor.hasNextLine()) {
                leitor.nextLine();
            }

            int numeroLinha = 1;

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                numeroLinha++;

                String[] partes = linha.split(",");

                if (partes.length < 6) {
                    estatisticasCidades.registarLinhaInvalida(numeroLinha);
                    continue;
                }

                try {
                    String codigoAlfa2 = partes[0].trim();
                    String nome = partes[1].trim();
                    String codigoRegiao = partes[2].trim();
                    String populacaoTexto = partes[3].trim();
                    double latitude = Double.parseDouble(partes[4].trim());
                    double longitude = Double.parseDouble(partes[5].trim());


                    if (populacaoTexto.isEmpty()) {
                        estatisticasCidades.registarLinhaInvalida(numeroLinha);
                        continue;
                    }

                    double populacao = Double.parseDouble(populacaoTexto);

                    if (codigoAlfa2.isEmpty()) {
                        estatisticasCidades.registarLinhaInvalida(numeroLinha);
                        continue;
                    }

                    if (!paisExisteComAlfa2(codigoAlfa2)) {
                        estatisticasCidades.registarLinhaInvalida(numeroLinha);
                        continue;
                    }

                    Cidade novaCidade = new Cidade(codigoAlfa2, nome, codigoRegiao, populacao, latitude, longitude);
                    listaDeCidades.add(novaCidade);

                    if (!cidadesPorPais.containsKey(codigoAlfa2.toLowerCase())) {
                        cidadesPorPais.put(codigoAlfa2.toLowerCase(), new ArrayList<>());
                    }
                    cidadesPorPais.get(codigoAlfa2.toLowerCase()).add(novaCidade);

                    estatisticasCidades.linhasValidas++;


                } catch (NumberFormatException e) {
                    estatisticasCidades.registarLinhaInvalida(numeroLinha);
                }
            }
            leitor.close();

        } catch (FileNotFoundException e) {
            System.out.println("Erro ao ler o ficheiro de cidades: " + e.getMessage());
        }
    }

    private static void lerFicheiroPopulacao(File ficheiro) {
        try {
            Scanner leitor = new Scanner(ficheiro);

            if (leitor.hasNextLine()) {
                leitor.nextLine();
            }

            int numeroLinha = 1;

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                numeroLinha++;

                String[] partes = linha.split(",");

                if (partes.length < 5) {
                    estatisticasPopulacao.registarLinhaInvalida(numeroLinha);
                    continue;
                }

                try {
                    int id = Integer.parseInt(partes[0].trim());
                    int ano = Integer.parseInt(partes[1].trim());
                    long populacaoMasculina = Long.parseLong(partes[2].trim());
                    long populacaoFeminina = Long.parseLong(partes[3].trim());
                    double densidade = Double.parseDouble(partes[4].trim());

                    if (!paisExisteComId(id)) {
                        estatisticasPopulacao.registarLinhaInvalida(numeroLinha);
                        continue;
                    }

                    Pais paisDoRegisto = paisPorId.get(id);
                    String alfaDoRegisto = paisDoRegisto.codigoAlfa2.toLowerCase();
                    if (!cidadesPorPais.containsKey(alfaDoRegisto) || cidadesPorPais.get(alfaDoRegisto).isEmpty()) {
                        estatisticasPopulacao.registarLinhaInvalida(numeroLinha);
                        continue;
                    }

                    RegistoPopulacao registo = new RegistoPopulacao(id, ano, populacaoMasculina, populacaoFeminina, densidade);
                    listaDePopulacao.add(registo);

                    if (!populacaoPorPais.containsKey(id)) {
                        populacaoPorPais.put(id, new ArrayList<>());
                    }
                    populacaoPorPais.get(id).add(registo);

                    estatisticasPopulacao.linhasValidas++;

                    Pais pais = paisPorId.get(id);
                    if (pais != null) {
                        pais.numeroDeRegistosPopulacao++;
                    }

                } catch (NumberFormatException e) {
                    estatisticasPopulacao.registarLinhaInvalida(numeroLinha);
                }
            }
            leitor.close();

        } catch (FileNotFoundException e) {
            System.out.println("Erro ao ler o ficheiro de população: " + e.getMessage());
        }
    }

    private static boolean paisExisteComAlfa2(String codigoAlfa2) {
        return paisPorAlfa2.containsKey(codigoAlfa2.toLowerCase());
    }

    private static boolean paisExisteComId(int id) {
        return paisPorId.containsKey(id);
    }

    public static ArrayList<Object> getObjects(TipoEntidade tipo) {
        ArrayList<Object> resultado = new ArrayList<>();

        if (tipo == TipoEntidade.PAIS) {
            for (int i = 0; i < listaDePaises.size(); i++) {
                resultado.add(listaDePaises.get(i));
            }
        } else if (tipo == TipoEntidade.CIDADE) {
            for (int i = 0; i < listaDeCidades.size(); i++) {
                resultado.add(listaDeCidades.get(i));
            }
        } else if (tipo == TipoEntidade.INPUT_INVALIDO) {
            resultado.add(estatisticasPaises);
            resultado.add(estatisticasCidades);
            resultado.add(estatisticasPopulacao);
        }

        return resultado;
    }

    private static boolean paisExisteComAlfa3(String codigoAlfa3) {
        return paisPorAlfa3.containsKey(codigoAlfa3.toLowerCase());
    }

    private static Result countCities(int populacaoMinima) {
        int contador = 0;
        for (int i = 0; i < listaDeCidades.size(); i++) {
            if (listaDeCidades.get(i).populacao >= populacaoMinima) {
                contador++;
            }
        }
        return new Result(true, null, String.valueOf(contador));
    }

    private static Result getCitiesByCountry(int n, String nomePais) {
        Pais pais = null;
        for (int i = 0; i < listaDePaises.size(); i++) {
            if (listaDePaises.get(i).nome.equalsIgnoreCase(nomePais)) {
                pais = listaDePaises.get(i);
                break;
            }
        }

        if (pais == null) {
            return new Result(false, "Comando invalido", null);
        }

        ArrayList<Cidade> cidades = cidadesPorPais.get(pais.codigoAlfa2.toLowerCase());

        if (cidades == null || cidades.isEmpty()) {
            return new Result(false, "Comando invalido", null);
        }

        StringBuilder sb = new StringBuilder();
        int limite = Math.min(n, cidades.size());
        for (int i = 0; i < limite; i++) {
            sb.append(cidades.get(i).nome).append("\n");
        }

        return new Result(true, null, sb.toString());
    }

    private static Result sumPopulations(String parametros) {
        String[] nomesPaises = parametros.split(",");
        long totalPopulacao = 0;

        for (int i = 0; i < nomesPaises.length; i++) {
            String nomePais = nomesPaises[i].trim();

            Pais pais = null;
            for (int j = 0; j < listaDePaises.size(); j++) {
                if (listaDePaises.get(j).nome.equalsIgnoreCase(nomePais)) {
                    pais = listaDePaises.get(j);
                    break;
                }
            }

            if (pais == null) {
                return new Result(true, null, "Pais invalido: " + nomePais);
            }

            ArrayList<RegistoPopulacao> registos = populacaoPorPais.get(pais.id);
            if (registos == null) {
                continue;
            }

            for (int j = 0; j < registos.size(); j++) {
                if (registos.get(j).ano == 2024) {
                    totalPopulacao += registos.get(j).getPopulacaoTotal();
                    break;
                }
            }
        }

        return new Result(true, null, String.valueOf(totalPopulacao));
    }

    private static Result getHistory(int anoInicio, int anoFim, String nomePais) {
        Pais pais = null;
        for (int i = 0; i < listaDePaises.size(); i++) {
            if (listaDePaises.get(i).nome.equalsIgnoreCase(nomePais)) {
                pais = listaDePaises.get(i);
                break;
            }
        }

        if (pais == null) {
            return new Result(false, "Comando invalido", null);
        }

        ArrayList<RegistoPopulacao> registos = populacaoPorPais.get(pais.id);
        if (registos == null || registos.isEmpty()) {
            return new Result(false, "Comando invalido", null);
        }

        StringBuilder sb = new StringBuilder();

        for (int ano = anoInicio; ano <= anoFim; ano++) {
            for (int i = 0; i < registos.size(); i++) {
                RegistoPopulacao registo = registos.get(i);
                if (registo.ano == ano) {
                    long mascK = registo.populacaoMasculina / 1000;
                    long femK = registo.populacaoFeminina / 1000;
                    sb.append(ano).append(":").append(mascK).append("k:").append(femK).append("k").append("\n");
                    break;
                }
            }
        }

        if (sb.length() == 0) {
            return new Result(false, "Comando invalido", null);
        }

        return new Result(true, null, sb.toString());
    }

    private static Result getMissingHistory(int anoInicio, int anoFim) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < listaDePaises.size(); i++) {
            Pais pais = listaDePaises.get(i);
            ArrayList<RegistoPopulacao> registos = populacaoPorPais.get(pais.id);

            boolean temFalha = false;
            for (int ano = anoInicio; ano <= anoFim; ano++) {
                boolean temAno = false;

                if (registos != null) {
                    for (int j = 0; j < registos.size(); j++) {
                        if (registos.get(j).ano == ano) {
                            temAno = true;
                            break;
                        }
                    }
                }

                if (!temAno) {
                    temFalha = true;
                    break;
                }
            }

            if (temFalha) {
                sb.append(pais.codigoAlfa2.toLowerCase()).append(":").append(pais.nome).append("\n");
            }
        }

        if (sb.length() == 0) {
            return new Result(true, null, "");
        }

        return new Result(true, null, sb.toString());
    }

    private static Result getMostPopulous(int n) {
        ArrayList<Cidade> maisPopulosas = new ArrayList<>();

        for (int i = 0; i < listaDePaises.size(); i++) {
            Pais pais = listaDePaises.get(i);
            ArrayList<Cidade> cidades = cidadesPorPais.get(pais.codigoAlfa2.toLowerCase());

            if (cidades == null || cidades.isEmpty()) {
                continue;
            }

            Cidade maisPopulosa = cidades.get(0);
            for (int j = 1; j < cidades.size(); j++) {
                if (cidades.get(j).populacao > maisPopulosa.populacao) {
                    maisPopulosa = cidades.get(j);
                }
            }
            maisPopulosas.add(maisPopulosa);
        }

        Cidade[] array = maisPopulosas.toArray(new Cidade[0]);
        Arrays.sort(array, new java.util.Comparator<Cidade>() {
            public int compare(Cidade c1, Cidade c2) {
                return Double.compare(c2.populacao, c1.populacao);
            }
        });
        maisPopulosas = new ArrayList<>(Arrays.asList(array));

        StringBuilder sb = new StringBuilder();
        int limite = Math.min(n, maisPopulosas.size());
        for (int i = 0; i < limite; i++) {
            Cidade c = maisPopulosas.get(i);
            Pais pais = paisPorAlfa2.get(c.codigoAlfa2DoPais.toLowerCase());
            String nomePais = pais != null ? pais.nome : c.codigoAlfa2DoPais;
            sb.append(nomePais).append(":").append(c.nome).append(":").append((long) c.populacao).append("\n");
        }

        return new Result(true, null, sb.toString());
    }

    private static Result getTopCitiesByCountry(int n, String nomePais) {
        Pais pais = null;
        for (int i = 0; i < listaDePaises.size(); i++) {
            if (listaDePaises.get(i).nome.equalsIgnoreCase(nomePais)) {
                pais = listaDePaises.get(i);
                break;
            }
        }

        if (pais == null) {
            return new Result(true, null, "");
        }

        ArrayList<Cidade> cidades = cidadesPorPais.get(pais.codigoAlfa2.toLowerCase());
        if (cidades == null || cidades.isEmpty()) {
            return new Result(true, null, "");
        }

        ArrayList<Cidade> filtradas = new ArrayList<>();
        for (int i = 0; i < cidades.size(); i++) {
            if (cidades.get(i).populacao >= 10000) {
                filtradas.add(cidades.get(i));
            }
        }

        Cidade[] array = filtradas.toArray(new Cidade[0]);
        Arrays.sort(array, new java.util.Comparator<Cidade>() {
            public int compare(Cidade c1, Cidade c2) {
                int popK1 = (int) c1.populacao / 1000;
                int popK2 = (int) c2.populacao / 1000;
                if (popK1 != popK2) {
                    return Integer.compare(popK2, popK1);
                }
                return c1.nome.compareToIgnoreCase(c2.nome);
            }
        });

        int limite = (n == -1 || n > array.length) ? array.length : n;
        if (limite <= 0) {
            return new Result(true, null, "");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < limite; i++) {
            Cidade c = array[i];
            sb.append(c.nome).append(":").append((int) c.populacao / 1000).append("K\n");
        }

        return new Result(true, null, sb.toString());
    }

    private static Result getDuplicateCities(int minPopulacao) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, Boolean> nomesVistos = new HashMap<>();

        for (int i = 0; i < listaDeCidades.size(); i++) {
            Cidade c = listaDeCidades.get(i);
            if (c.populacao < minPopulacao) {
                continue;
            }
            String nomeLower = c.nome.toLowerCase();

            if (!nomesVistos.containsKey(nomeLower)) {
                nomesVistos.put(nomeLower, true);
            } else {
                Pais pais = paisPorAlfa2.get(c.codigoAlfa2DoPais.toLowerCase());
                String nomePais = pais != null ? pais.nome : c.codigoAlfa2DoPais;
                sb.append(c.nome).append(" (").append(nomePais).append(",").append(c.codigoRegiao).append(")").append("\n");
            }
        }

        return new Result(true, null, sb.toString());
    }

    private static Result getCountriesGenderGap(double minGap) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < listaDePaises.size(); i++) {
            Pais pais = listaDePaises.get(i);
            ArrayList<RegistoPopulacao> registos = populacaoPorPais.get(pais.id);

            if (registos == null) {
                continue;
            }

            RegistoPopulacao registo2024 = null;
            for (int j = 0; j < registos.size(); j++) {
                if (registos.get(j).ano == 2024) {
                    registo2024 = registos.get(j);
                    break;
                }
            }

            if (registo2024 == null) {
                continue;
            }

            long masc = registo2024.populacaoMasculina;
            long fem = registo2024.populacaoFeminina;
            long total = masc + fem;

            if (total == 0) {
                continue;
            }

            double gap = (Math.abs(masc - fem) / (double) total) * 100;

            if (gap >= minGap) {
                long gapArredondado = Math.round(gap * 100);
                String gapStr = (gapArredondado / 100) + "." + String.format("%02d", gapArredondado % 100);
                sb.append(pais.nome).append(":").append(gapStr).append("\n");
            }
        }

        if (sb.length() == 0) {
            return new Result(true, null, "Sem resultados");
        }
        return new Result(true, null, sb.toString());
    }

    private static Result getTopPopulationIncrease(int anoInicio, int anoFim) {
        ArrayList<String> nomesPaises = new ArrayList<>();
        ArrayList<Double> percentagens = new ArrayList<>();
        ArrayList<int[]> pares = new ArrayList<>();

        for (int i = 0; i < listaDePaises.size(); i++) {
            Pais pais = listaDePaises.get(i);
            ArrayList<RegistoPopulacao> registos = populacaoPorPais.get(pais.id);

            if (registos == null) {
                continue;
            }

            ArrayList<RegistoPopulacao> registosNoIntervalo = new ArrayList<>();
            for (int j = 0; j < registos.size(); j++) {
                int ano = registos.get(j).ano;
                if (ano >= anoInicio && ano <= anoFim) {
                    registosNoIntervalo.add(registos.get(j));
                }
            }

            for (int a = 0; a < registosNoIntervalo.size(); a++) {
                for (int b = 0; b < registosNoIntervalo.size(); b++) {
                    RegistoPopulacao rA = registosNoIntervalo.get(a);
                    RegistoPopulacao rB = registosNoIntervalo.get(b);
                    if (rA.ano >= rB.ano) {
                        continue;
                    }

                    long popA = rA.getPopulacaoTotal();
                    long popB = rB.getPopulacaoTotal();

                    if (popB == 0) {
                        continue;
                    }

                    double aumento = ((double)(popB - popA) / popB) * 100;

                    if (aumento < 0) {
                        continue;
                    }

                    nomesPaises.add(pais.nome);
                    percentagens.add(aumento);
                    pares.add(new int[]{rA.ano, rB.ano});
                }
            }
        }

        Integer[] indices = new Integer[nomesPaises.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        final ArrayList<Double> percentagensFinais = percentagens;
        Arrays.sort(indices, new java.util.Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                return Double.compare(percentagensFinais.get(b), percentagensFinais.get(a));
            }
        });

        int limite = Math.min(5, indices.length);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < limite; i++) {
            int idx = indices[i];
            double perc = percentagens.get(idx);
            long percArredondada = Math.round(perc * 100);
            String percStr = (percArredondada / 100) + "." + String.format("%02d", percArredondada % 100);
            sb.append(nomesPaises.get(idx))
                    .append(":").append(pares.get(idx)[0])
                    .append("-").append(pares.get(idx)[1])
                    .append(":").append(percStr).append("%").append("\n");
        }

        return new Result(true, null, sb.toString());
    }

    private static Result getDuplicateCitiesDifferentCountries(int minPopulacao) {
        HashMap<String, ArrayList<Cidade>> cidadesPorNome = new HashMap<>();

        for (int i = 0; i < listaDeCidades.size(); i++) {
            Cidade c = listaDeCidades.get(i);
            if (c.populacao < minPopulacao) {
                continue;
            }
            String nomeLower = c.nome.toLowerCase();
            if (!cidadesPorNome.containsKey(nomeLower)) {
                cidadesPorNome.put(nomeLower, new ArrayList<>());
            }
            cidadesPorNome.get(nomeLower).add(c);
        }

        StringBuilder sb = new StringBuilder();

        for (String nomeLower : cidadesPorNome.keySet()) {
            ArrayList<Cidade> grupo = cidadesPorNome.get(nomeLower);

            if (grupo.size() < 2) {
                continue;
            }

            ArrayList<String> paisesDistintos = new ArrayList<>();
            for (int i = 0; i < grupo.size(); i++) {
                String nomePais = null;
                Pais p = paisPorAlfa2.get(grupo.get(i).codigoAlfa2DoPais.toLowerCase());
                if (p != null) {
                    nomePais = p.nome;
                }
                if (nomePais != null && !paisesDistintos.contains(nomePais)) {
                    paisesDistintos.add(nomePais);
                }
            }

            if (paisesDistintos.size() < 2) {
                continue;
            }

            String[] paisesArray = paisesDistintos.toArray(new String[0]);
            Arrays.sort(paisesArray);
            paisesDistintos = new ArrayList<>(Arrays.asList(paisesArray));

            sb.append(grupo.get(0).nome).append(": ");

            for (int i = 0; i < paisesDistintos.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(paisesDistintos.get(i));
            }
            sb.append("\n");
        }

        return new Result(true, null, sb.toString());
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private static Result getCitiesAtDistance(int distancia, String nomePais) {
        Pais pais = null;
        for (int i = 0; i < listaDePaises.size(); i++) {
            if (listaDePaises.get(i).nome.equalsIgnoreCase(nomePais)) {
                pais = listaDePaises.get(i);
                break;
            }
        }

        if (pais == null) {
            return new Result(false, "Comando invalido", null);
        }

        ArrayList<Cidade> cidades = cidadesPorPais.get(pais.codigoAlfa2.toLowerCase());
        if (cidades == null || cidades.isEmpty()) {
            return new Result(true, null, "");
        }

        double limiteInferior = distancia - 0.999;
        double limiteSuperior = distancia + 0.999;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < cidades.size(); i++) {
            for (int j = i + 1; j < cidades.size(); j++) {
                Cidade c1 = cidades.get(i);
                Cidade c2 = cidades.get(j);

                double dist = haversine(c1.latitude, c1.longitude, c2.latitude, c2.longitude);

                if (dist >= limiteInferior && dist <= limiteSuperior) {
                    if (c1.nome.compareTo(c2.nome) <= 0) {
                        sb.append(c1.nome).append("->").append(c2.nome);
                    } else {
                        sb.append(c2.nome).append("->").append(c1.nome);
                    }
                    sb.append("\n");
                }
            }
        }

        return new Result(true, null, sb.toString());
    }

    private static Result getCitiesAtDistance2(int distancia, String nomePais) {
        Pais pais = null;
        for (int i = 0; i < listaDePaises.size(); i++) {
            if (listaDePaises.get(i).nome.equalsIgnoreCase(nomePais)) {
                pais = listaDePaises.get(i);
                break;
            }
        }

        if (pais == null) {
            for (Pais p : paisPorId.values()) {
                if (p.nome.equalsIgnoreCase(nomePais)) {
                    pais = p;
                    break;
                }
            }
        }

        if (pais == null) {
            return new Result(false, "Pais invalido", null);
        }

        String alfa2Pais = pais.codigoAlfa2.toLowerCase();
        ArrayList<Cidade> cidadesDoPais = cidadesPorPais.get(alfa2Pais);
        if (cidadesDoPais == null || cidadesDoPais.isEmpty()) {
            return new Result(true, null, "");
        }

        double limiteInferior = distancia - 0.999;
        double limiteSuperior = distancia + 0.999;
        double maxLatDelta = (limiteSuperior / 111.0) + 0.01;

        ArrayList<String> pares = new ArrayList<>();

        for (int i = 0; i < cidadesDoPais.size(); i++) {
            Cidade c1 = cidadesDoPais.get(i);
            double maxLonDelta = (limiteSuperior / (111.0 * Math.max(0.01, Math.cos(Math.toRadians(c1.latitude))))) + 0.01;

            for (int j = 0; j < listaDeCidades.size(); j++) {
                Cidade c2 = listaDeCidades.get(j);
                if (c2.codigoAlfa2DoPais.equalsIgnoreCase(alfa2Pais)) {
                    continue;
                }

                if (Math.abs(c1.latitude - c2.latitude) > maxLatDelta) {
                    continue;
                }
                if (Math.abs(c1.longitude - c2.longitude) > maxLonDelta) {
                    continue;
                }

                double dist = haversine(c1.latitude, c1.longitude, c2.latitude, c2.longitude);

                if (dist >= limiteInferior && dist <= limiteSuperior) {
                    String par;
                    if (c1.nome.compareTo(c2.nome) <= 0) {
                        par = c1.nome + "->" + c2.nome;
                    } else {
                        par = c2.nome + "->" + c1.nome;
                    }
                    pares.add(par);
                }
            }
        }

        String[] arr = pares.toArray(new String[0]);
        Arrays.sort(arr);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]).append("\n");
        }

        return new Result(true, null, sb.toString());
    }

    private static Result insertCity(String alfa2, String nomeCidade, String regiao, double populacao) {
        Pais pais = paisPorAlfa2.get(alfa2.toLowerCase());
        if (pais == null) {
            for (Pais p : paisPorId.values()) {
                if (p.codigoAlfa2.equalsIgnoreCase(alfa2)) {
                    pais = p;
                    break;
                }
            }
        }
        if (pais == null) {
            for (int i = 0; i < listaDePaises.size(); i++) {
                if (listaDePaises.get(i).codigoAlfa2.equalsIgnoreCase(alfa2)) {
                    pais = listaDePaises.get(i);
                    break;
                }
            }
        }
        if (pais == null) {
            return new Result(true, null, "Pais invalido");
        }

        Cidade novaCidade = new Cidade(alfa2, nomeCidade, regiao, populacao, 0.0, 0.0);
        listaDeCidades.add(novaCidade);

        String alfa2Lower = alfa2.toLowerCase();
        if (!cidadesPorPais.containsKey(alfa2Lower)) {
            cidadesPorPais.put(alfa2Lower, new ArrayList<>());
        }
        cidadesPorPais.get(alfa2Lower).add(novaCidade);

        if (!listaDePaises.contains(pais)) {
            listaDePaises.add(pais);
        }

        return new Result(true, null, "Inserido com sucesso");
    }

    private static Result removeCountry(String nomePais) {
        Pais pais = null;
        for (int i = 0; i < listaDePaises.size(); i++) {
            if (listaDePaises.get(i).nome.equalsIgnoreCase(nomePais)) {
                pais = listaDePaises.get(i);
                break;
            }
        }

        if (pais == null) {
            for (Pais p : paisPorId.values()) {
                if (p.nome.equalsIgnoreCase(nomePais)) {
                    pais = p;
                    break;
                }
            }
        }

        if (pais == null) {
            return new Result(true, null, "Pais invalido");
        }

        String alfa2Lower = pais.codigoAlfa2.toLowerCase();
        for (int i = listaDeCidades.size() - 1; i >= 0; i--) {
            if (listaDeCidades.get(i).codigoAlfa2DoPais.equalsIgnoreCase(pais.codigoAlfa2)) {
                listaDeCidades.remove(i);
            }
        }

        cidadesPorPais.remove(alfa2Lower);
        paisPorAlfa2.remove(alfa2Lower);
        paisPorAlfa3.remove(pais.codigoAlfa3.toLowerCase());
        paisPorId.remove(pais.id);

        populacaoPorPais.remove(pais.id);

        ArrayList<RegistoPopulacao> registosAPagar = new ArrayList<>();
        for (int i = 0; i < listaDePopulacao.size(); i++) {
            if (listaDePopulacao.get(i).idPais == pais.id) {
                registosAPagar.add(listaDePopulacao.get(i));
            }
        }
        listaDePopulacao.removeAll(registosAPagar);

        listaDePaises.remove(pais);

        return new Result(true, null, "Removido com sucesso");
    }

    private static class PaisPerda {
        Pais pais;
        long perda;
        PaisPerda(Pais pais, long perda) {
            this.pais = pais;
            this.perda = perda;
        }
    }

    private static Result getCountriesLosingPopulation(int anoInicio, int anoFim) {
        if (anoInicio >= anoFim) {
            return new Result(true, null, "");
        }

        ArrayList<PaisPerda> resultados = new ArrayList<>();

        for (int i = 0; i < listaDePaises.size(); i++) {
            Pais pais = listaDePaises.get(i);
            ArrayList<RegistoPopulacao> registos = populacaoPorPais.get(pais.id);
            if (registos == null) {
                continue;
            }

            long popInicio = -1;
            long popFim = -1;
            for (int j = 0; j < registos.size(); j++) {
                RegistoPopulacao reg = registos.get(j);
                if (reg.ano == anoInicio) {
                    popInicio = reg.getPopulacaoTotal();
                } else if (reg.ano == anoFim) {
                    popFim = reg.getPopulacaoTotal();
                }
            }

            if (popInicio < 0 || popFim < 0) {
                continue;
            }
            if (popInicio <= popFim) {
                continue;
            }

            resultados.add(new PaisPerda(pais, popInicio - popFim));
        }

        PaisPerda[] arr = resultados.toArray(new PaisPerda[0]);
        Arrays.sort(arr, new java.util.Comparator<PaisPerda>() {
            public int compare(PaisPerda a, PaisPerda b) {
                if (a.perda != b.perda) {
                    return Long.compare(b.perda, a.perda);
                }
                return a.pais.nome.compareToIgnoreCase(b.pais.nome);
            }
        });

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i].pais.nome).append(":").append(arr[i].perda).append("\n");
        }

        return new Result(true, null, sb.toString());
    }

    public static void main(String[] args) {
        System.out.println("Welcome to DEISI World Meter");

        long inicio = System.currentTimeMillis();
        boolean resultado = parseFiles(new File("."));
        long fim = System.currentTimeMillis();

        if (!resultado) {
            System.out.println("Erro: ficheiros não encontrados!");
            return;
        }

        System.out.println("Loaded files in " + (fim - inicio) + " ms");
        System.out.println();
        System.out.println("------------------------");
        System.out.println("Commands available:");
        System.out.println("  COUNT_CITIES <min_population>");
        System.out.println("  GET_CITIES_BY_COUNTRY <num-results> <country-name>");
        System.out.println("  SUM_POPULATIONS <countries-list>");
        System.out.println("  GET_HISTORY <year-start> <year-end> <country_name>");
        System.out.println("  GET_MISSING_HISTORY <year-start> <year-end>");
        System.out.println("  GET_MOST_POPULOUS <num-results>");
        System.out.println("  GET_TOP_CITIES_BY_COUNTRY <num-results> <country-name>");
        System.out.println("  GET_DUPLICATE_CITIES <min_population>");
        System.out.println("  GET_COUNTRIES_GENDER_GAP <min-gender-gap>");
        System.out.println("  GET_TOP_POPULATION_INCREASE <year-start> <year-end>");
        System.out.println("  GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES <min-population>");
        System.out.println("  GET_CITIES_AT_DISTANCE <distance> <country-name>");
        System.out.println("  GET_CITIES_AT_DISTANCE2 <distance> <country-name>");
        System.out.println("  INSERT_CITY <alfa2> <city-name> <region> <population>");
        System.out.println("  REMOVE_COUNTRY <country-name>");
        System.out.println("  GET_COUNTRIES_LOSING_POPULATION <year-start> <year-end>");
        System.out.println("  HELP");
        System.out.println("  QUIT");
        System.out.println("------------------------");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String linha = scanner.nextLine().trim();

            if (linha.equalsIgnoreCase("quit")) {
                break;
            }

            if (linha.equalsIgnoreCase("help")) {
                System.out.println("COUNT_CITIES <min_population>");
                System.out.println("GET_CITIES_BY_COUNTRY <num-results> <country-name>");
                System.out.println("SUM_POPULATIONS <countries-list>");
                System.out.println("GET_HISTORY <year-start> <year-end> <country_name>");
                System.out.println("GET_MISSING_HISTORY <year-start> <year-end>");
                System.out.println("GET_MOST_POPULOUS <num-results>");
                System.out.println("GET_TOP_CITIES_BY_COUNTRY <num-results> <country-name>");
                System.out.println("GET_DUPLICATE_CITIES <min_population>");
                System.out.println("GET_COUNTRIES_GENDER_GAP <min-gender-gap>");
                System.out.println("GET_TOP_POPULATION_INCREASE <year-start> <year-end>");
                System.out.println("GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES <min-population>");
                System.out.println("GET_CITIES_AT_DISTANCE <distance> <country-name>");
                System.out.println("INSERT_CITY <alfa2> <city-name> <region> <population>");
                System.out.println("REMOVE_COUNTRY <country-name>");
                continue;
            }

            if (linha.isEmpty()) {
                continue;
            }

            long tempoInicio = System.currentTimeMillis();
            Result result = execute(linha);
            long tempoFim = System.currentTimeMillis();

            if (result.success) {
                System.out.println(result.result);
            } else {
                System.out.println(result.error);
            }

            System.out.println("(took " + (tempoFim - tempoInicio) + " ms)");
        }

        scanner.close();
    }
}