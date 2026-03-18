package pt.ulusofona.aed.deisiworldmeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

enum TipoEntidade {
    PAIS, CIDADE, DADOS_DEMOGRAFICOS, INPUT_INVALIDO
}

public class Main {

    static ArrayList<Pais> listaPaises = new ArrayList<>();
    static ArrayList<Cidade> listaCidades = new ArrayList<>();
    static ArrayList<DadosDemograficos> listaDados = new ArrayList<>();
    static ArrayList<String> linhasComErro = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("A ler ficheiros...");
        boolean sucesso = parseFiles(new File("."));

        if (!sucesso) {
            System.out.println("Erro: Não foi possível encontrar os ficheiros .csv na raiz!");
            return;
        }

        ArrayList<Pais> paises = getObjects(TipoEntidade.PAIS);
        System.out.println("Países lidos: " + paises.size());
        if (!paises.isEmpty()) {
            System.out.println("Primeiro país: " + paises.get(0)); // Testa o teu toString()
        }

        ArrayList<Cidade> cidades = getObjects(TipoEntidade.CIDADE);
        System.out.println("Cidades lidas: " + cidades.size());

        ArrayList<String> erros = getObjects(TipoEntidade.INPUT_INVALIDO);
        System.out.println("Linhas com erro: " + erros.size());
        for (String erro : erros) {
            System.out.println(" -> Erro na linha: " + erro);
        }
    }

    public static boolean parseFiles(File folder) {
        listaPaises.clear();
        listaCidades.clear();
        listaDados.clear();
        linhasComErro.clear();

        return lerPaises(folder) && lerCidades(folder) && lerPopulacao(folder);
    }

    private static boolean lerPaises(File folder) {
        File ficheiro = new File(folder, "paises.csv");
        try (Scanner leitor = new Scanner(ficheiro)) {
            if (leitor.hasNextLine()) leitor.nextLine(); // Ignora o cabeçalho

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(",");

                if (partes.length == 4) {
                    try {
                        int id = Integer.parseInt(partes[0].trim());
                        String alfa2 = partes[1].trim();
                        String alfa3 = partes[2].trim();
                        String nome = partes[3].trim();
                        listaPaises.add(new Pais(id, alfa2, alfa3, nome));
                    } catch (NumberFormatException e) {
                        linhasComErro.add(linha);
                    }
                } else {
                    linhasComErro.add(linha);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private static boolean lerCidades(File folder) {
        File ficheiro = new File(folder, "cidades.csv");
        try (Scanner leitor = new Scanner(ficheiro)) {
            if (leitor.hasNextLine()) leitor.nextLine();

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(",", -1);

                boolean temCamposVazios = false;
                for (String parte : partes) {
                    if (parte.trim().isEmpty()) {
                        temCamposVazios = true;
                        break;
                    }
                }

                if (partes.length == 6 && !temCamposVazios) {
                    try {
                        String alfa2 = partes[0].trim();
                        String nome = partes[1].trim();
                        String regiao = partes[2].trim();
                        int pop = (int) Double.parseDouble(partes[3].trim());
                        double lat = Double.parseDouble(partes[4].trim());
                        double lon = Double.parseDouble(partes[5].trim());

                        listaCidades.add(new Cidade(alfa2, nome, regiao, pop, lat, lon));
                    } catch (NumberFormatException e) {
                        linhasComErro.add(linha);
                    }
                } else {
                    linhasComErro.add(linha);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private static boolean lerPopulacao(File folder) {
        File ficheiro = new File(folder, "populacao.csv");
        try (Scanner leitor = new Scanner(ficheiro)) {
            if (leitor.hasNextLine()) leitor.nextLine();

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(",");

                if (partes.length == 5) {
                    try {
                        int id = Integer.parseInt(partes[0].trim());
                        int ano = Integer.parseInt(partes[1].trim());
                        int popM = Integer.parseInt(partes[2].trim());
                        int popF = Integer.parseInt(partes[3].trim());
                        double densidade = Double.parseDouble(partes[4].trim());

                        listaDados.add(new DadosDemograficos(id, ano, popM, popF, densidade));
                    } catch (NumberFormatException e) {
                        linhasComErro.add(linha);
                    }
                } else {
                    linhasComErro.add(linha);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static ArrayList getObjects(TipoEntidade tipo) {
        if (tipo == TipoEntidade.PAIS) {
            return listaPaises;
        } else if (tipo == TipoEntidade.CIDADE) {
            return listaCidades;
        } else if (tipo == TipoEntidade.DADOS_DEMOGRAFICOS) {
            return listaDados;
        } else if (tipo == TipoEntidade.INPUT_INVALIDO) {
            return linhasComErro;
        }
        return null;
    }
}