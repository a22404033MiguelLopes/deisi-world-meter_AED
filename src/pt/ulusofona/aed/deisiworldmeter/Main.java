package pt.ulusofona.aed.deisiworldmeter;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static ArrayList<Pais> listaDePaises = new ArrayList<>();
    static ArrayList<Cidade> listaDeCidades = new ArrayList<>();

    static EstatisticasFicheiro estatisticasPaises;
    static EstatisticasFicheiro estatisticasCidades;
    static EstatisticasFicheiro estatisticasPopulacao;

    public static boolean parseFiles(File pasta) {
        listaDePaises.clear();
        listaDeCidades.clear();

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

        return true;
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

                    listaDePaises.add(new Pais(id, codigoAlfa2, codigoAlfa3, nome));
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

                    listaDeCidades.add(new Cidade(codigoAlfa2, nome, codigoRegiao, populacao, latitude, longitude));
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

                    estatisticasPopulacao.linhasValidas++;

                    for (int i = 0; i < listaDePaises.size(); i++) {
                        if (listaDePaises.get(i).id == id) {
                            listaDePaises.get(i).numeroDeRegistosPopulacao++;
                            break;
                        }
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
        for (int i = 0; i < listaDePaises.size(); i++) {
            if (listaDePaises.get(i).codigoAlfa2.equalsIgnoreCase(codigoAlfa2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean paisExisteComId(int id) {
        for (int i = 0; i < listaDePaises.size(); i++) {
            if (listaDePaises.get(i).id == id) {
                return true;
            }
        }
        return false;
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
        for (int i = 0; i < listaDePaises.size(); i++) {
            if (listaDePaises.get(i).codigoAlfa3.equalsIgnoreCase(codigoAlfa3)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        boolean resultado = parseFiles(new File("."));

        if (!resultado) {
            System.out.println("Erro: ficheiros não encontrados!");
            return;
        }

        ArrayList<Object> invalidos = getObjects(TipoEntidade.INPUT_INVALIDO);
        for (int i = 0; i < invalidos.size(); i++) {
            System.out.println(invalidos.get(i));
        }
    }
}