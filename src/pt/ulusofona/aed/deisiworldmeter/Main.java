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
                paisPorId.remove(p.id);
                paisPorAlfa2.remove(p.codigoAlfa2.toLowerCase());
                paisPorAlfa3.remove(p.codigoAlfa3.toLowerCase());
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
            case "INSERT_CITY": {
                String[] args = parametros.split(" ");
                String alfa2 = args[0].trim();
                String nomeCidade = args[1].trim();
                String regiao = args[2].trim();
                double populacao = Double.parseDouble(args[3].trim());
                resultado = insertCity(alfa2, nomeCidade, regiao, populacao);
                break;
            }
            case "REMOVE_COUNTRY":
                resultado = removeCountry(parametros.trim());
                break;
            default:
                return new Result(false, "Comando invalido", null);
        }

        if (resultado.success && resultado.result != null && resultado.result.contains("\n")) {
            resultado.result = resultado.result + "\n";
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
        // Encontrar o país pelo nome
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
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(cidades.get(i).nome);
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
                continue;
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
        // Encontrar o país pelo nome
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
        boolean primeiraLinha = true;

        for (int ano = anoInicio; ano <= anoFim; ano++) {
            for (int i = 0; i < registos.size(); i++) {
                RegistoPopulacao registo = registos.get(i);
                if (registo.ano == ano) {
                    long mascK = registo.populacaoMasculina / 1000;
                    long femK = registo.populacaoFeminina / 1000;
                    if (!primeiraLinha) {
                        sb.append("\n");
                    }
                    sb.append(ano).append(":").append(mascK).append("k:").append(femK).append("k");
                    primeiraLinha = false;
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
        boolean primeiraLinha = true;

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
                if (!primeiraLinha) {
                    sb.append("\n");
                }
                sb.append(pais.codigoAlfa2.toLowerCase()).append(":").append(pais.nome);
                primeiraLinha = false;
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
            if (i > 0) {
                sb.append("\n");
            }
            Cidade c = maisPopulosas.get(i);
            Pais pais = paisPorAlfa2.get(c.codigoAlfa2DoPais.toLowerCase());
            String nomePais = pais != null ? pais.nome : c.codigoAlfa2DoPais;
            sb.append(nomePais).append(":").append(c.nome).append(":").append((long) c.populacao);
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
            return new Result(false, "Comando invalido", null);
        }

        ArrayList<Cidade> cidades = cidadesPorPais.get(pais.codigoAlfa2.toLowerCase());
        if (cidades == null || cidades.isEmpty()) {
            return new Result(false, "Comando invalido", null);
        }

        ArrayList<Cidade> ordenadas = new ArrayList<>(cidades);

        Cidade[] array = ordenadas.toArray(new Cidade[0]);
        Arrays.sort(array, new java.util.Comparator<Cidade>() {
            public int compare(Cidade c1, Cidade c2) {
                long popK1 = (long) c1.populacao / 1000;
                long popK2 = (long) c2.populacao / 1000;
                if (popK2 != popK1) {
                    return Long.compare(popK2, popK1);
                }
                return c1.nome.compareTo(c2.nome);
            }
        });
        ordenadas = new ArrayList<>(Arrays.asList(array));

        int limite = (n == -1) ? ordenadas.size() : Math.min(n, ordenadas.size());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < limite; i++) {
            if (i > 0) {
                sb.append("\n");
            }
            Cidade c = ordenadas.get(i);
            long popK = (long) c.populacao / 1000;
            sb.append(c.nome).append(":").append(popK).append("K");
        }

        return new Result(true, null, sb.toString());
    }

    private static Result getDuplicateCities(int minPopulacao) {
        StringBuilder sb = new StringBuilder();
        boolean primeiraLinha = true;

        for (int i = 0; i < listaDePaises.size(); i++) {
            Pais pais = listaDePaises.get(i);
            ArrayList<Cidade> cidades = cidadesPorPais.get(pais.codigoAlfa2.toLowerCase());

            if (cidades == null || cidades.isEmpty()) {
                continue;
            }

            HashMap<String, ArrayList<Cidade>> cidadesPorNome = new HashMap<>();
            for (int j = 0; j < cidades.size(); j++) {
                Cidade c = cidades.get(j);
                if (c.populacao < minPopulacao) {
                    continue;
                }
                String nomeLower = c.nome.toLowerCase();
                if (!cidadesPorNome.containsKey(nomeLower)) {
                    cidadesPorNome.put(nomeLower, new ArrayList<>());
                }
                cidadesPorNome.get(nomeLower).add(c);
            }

            for (String nomeLower : cidadesPorNome.keySet()) {
                ArrayList<Cidade> duplicadas = cidadesPorNome.get(nomeLower);
                if (duplicadas.size() < 2) {
                    continue;
                }
                for (int j = 0; j < duplicadas.size(); j++) {
                    if (!primeiraLinha) {
                        sb.append("\n");
                    }
                    Cidade c = duplicadas.get(j);
                    sb.append(c.nome).append(" (").append(pais.nome).append(",").append(c.codigoRegiao).append(")");
                    primeiraLinha = false;
                }
            }
        }

        return new Result(true, null, sb.toString());
    }

    private static Result getCountriesGenderGap(double minGap) {
        StringBuilder sb = new StringBuilder();
        boolean primeiraLinha = true;

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
                if (!primeiraLinha) {
                    sb.append("\n");
                }
                long gapTruncado = (long) (gap * 100);
                String gapStr = (gapTruncado / 100) + "." + String.format("%02d", gapTruncado % 100);
                sb.append(pais.nome).append(":").append(gapStr);
                primeiraLinha = false;
            }
        }

        if (sb.length() == 0) {
            return new Result(true, null, "Sem resultados");
        }
        return new Result(true, null, sb.toString());
    }

    private static Result getTopPopulationIncrease(int anoInicio, int anoFim) {
        ArrayList<Pais> paisesAumento = new ArrayList<>();
        ArrayList<Double> percentagens = new ArrayList<>();

        for (int i = 0; i < listaDePaises.size(); i++) {
            Pais pais = listaDePaises.get(i);
            ArrayList<RegistoPopulacao> registos = populacaoPorPais.get(pais.id);

            if (registos == null) {
                continue;
            }

            RegistoPopulacao registoInicio = null;
            RegistoPopulacao registoFim = null;

            for (int j = 0; j < registos.size(); j++) {
                if (registos.get(j).ano == anoInicio) {
                    registoInicio = registos.get(j);
                }
                if (registos.get(j).ano == anoFim) {
                    registoFim = registos.get(j);
                }
            }

            if (registoInicio == null || registoFim == null) {
                continue;
            }

            long popInicio = registoInicio.getPopulacaoTotal();
            long popFim = registoFim.getPopulacaoTotal();

            if (popFim == 0) {
                continue;
            }

            double aumento = ((double)(popFim - popInicio) / popFim) * 100;

            if (aumento < 0) {
                continue;
            }

            paisesAumento.add(pais);
            percentagens.add(aumento);
        }

        Integer[] indices = new Integer[paisesAumento.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        final ArrayList<Double> percentagensFinais = percentagens;
        Arrays.sort(indices, new java.util.Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                return Double.compare(percentagensFinais.get(b), percentagensFinais.get(a));
            }
        });

        ArrayList<Pais> paisesOrdenados = new ArrayList<>();
        ArrayList<Double> percentagensOrdenadas = new ArrayList<>();
        for (int i = 0; i < indices.length; i++) {
            paisesOrdenados.add(paisesAumento.get(indices[i]));
            percentagensOrdenadas.add(percentagens.get(indices[i]));
        }

        int limite = Math.min(5, paisesOrdenados.size());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < limite; i++) {
            if (i > 0) {
                sb.append("\n");
            }
            double perc = percentagensOrdenadas.get(i);
            long percArredondada = Math.round(perc * 100);
            String percStr = (percArredondada / 100) + "." + String.format("%02d", percArredondada % 100);
            sb.append(paisesOrdenados.get(i).nome)
                    .append(":").append(anoInicio)
                    .append("-").append(anoFim)
                    .append(":").append(percStr).append("%");
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
        boolean primeiraLinha = true;

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

            if (!primeiraLinha) {
                sb.append("\n");
            }

            sb.append(grupo.get(0).nome).append(": ");

            for (int i = 0; i < paisesDistintos.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(paisesDistintos.get(i));
            }
            primeiraLinha = false;
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
            return new Result(true, null, "");
        }

        ArrayList<Cidade> cidades = cidadesPorPais.get(pais.codigoAlfa2.toLowerCase());
        if (cidades == null || cidades.isEmpty()) {
            return new Result(true, null, "");
        }

        double limiteInferior = distancia - 0.999;
        double limiteSuperior = distancia + 0.999;

        StringBuilder sb = new StringBuilder();
        boolean primeiraLinha = true;

        for (int i = 0; i < cidades.size(); i++) {
            for (int j = i + 1; j < cidades.size(); j++) {
                Cidade c1 = cidades.get(i);
                Cidade c2 = cidades.get(j);

                double dist = haversine(c1.latitude, c1.longitude, c2.latitude, c2.longitude);

                if (dist >= limiteInferior && dist <= limiteSuperior) {
                    if (!primeiraLinha) {
                        sb.append("\n");
                    }
                    if (c1.nome.compareTo(c2.nome) <= 0) {
                        sb.append(c1.nome).append("->").append(c2.nome);
                    } else {
                        sb.append(c2.nome).append("->").append(c1.nome);
                    }
                    primeiraLinha = false;
                }
            }
        }

        return new Result(true, null, sb.toString());
    }

    private static Result insertCity(String alfa2, String nomeCidade, String regiao, double populacao) {
        Pais pais = paisPorAlfa2.get(alfa2.toLowerCase());
        if (pais == null) {
            return new Result(false, "Invalid country", null);
        }

        Cidade novaCidade = new Cidade(alfa2, nomeCidade, regiao, populacao, 0.0, 0.0);
        listaDeCidades.add(novaCidade);

        String alfa2Lower = alfa2.toLowerCase();
        if (!cidadesPorPais.containsKey(alfa2Lower)) {
            cidadesPorPais.put(alfa2Lower, new ArrayList<>());
        }
        cidadesPorPais.get(alfa2Lower).add(novaCidade);

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
            return new Result(false, "Comando invalido", null);
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
        System.out.println("  INSERT_CITY <alfa2> <city-name> <region> <population>");
        System.out.println("  REMOVE_COUNTRY <country-name>");
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