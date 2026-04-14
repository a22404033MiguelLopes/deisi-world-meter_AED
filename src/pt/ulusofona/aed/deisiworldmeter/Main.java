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
    static ArrayList<String> sumarioErros = new ArrayList<>();

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            parseFiles(new File(args[0]));
        } else {
            parseFiles(new File("."));
        }
        for (String s : sumarioErros) {
            System.out.println(s);
        }
        ArrayList<Pais> ps = listaPaises;
        for (int i = 0; i < ps.size() && i < 10; i++) {
            System.out.println(ps.get(i).toString());
        }
    }

    public static boolean parseFiles(File folder) {
        listaPaises.clear();
        listaCidades.clear();
        listaDados.clear();
        sumarioErros.clear();
        boolean leuPaises = lerPaises(folder);
        boolean leuCidades = lerCidades(folder);
        boolean leuDados = lerDados(folder);

        return leuPaises && leuCidades && leuDados;
    }

    private static String[] splitCsv(String line, int expectedFields) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                // toggle quote state
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());

        while (expectedFields > 0 && fields.size() < expectedFields) {
            fields.add("");
        }

        return fields.toArray(new String[0]);
    }

    private static boolean isAlpha(String s) {
        if (s == null) return false;
        s = s.trim();
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isLetter(s.charAt(i))) return false;
        }
        return true;
    }

    private static boolean lerPaises(File folder) {
        File f = new File(folder, "paises.csv");
        int ok = 0;
        int erro = 0;
        int totalLinhasDados = 0;

        try (Scanner s = new Scanner(f)) {
            String firstLine = null;
            while (s.hasNextLine()) {
                firstLine = s.nextLine();
                if (!firstLine.trim().isEmpty()) break;
                firstLine = null;
            }

            if (firstLine != null) {
                String[] partesFirst = splitCsv(firstLine, 4);
                boolean firstIsHeader = true;
                if (partesFirst.length >= 1) {
                    String idCandidate = partesFirst[0].trim();
                    try {
                        Integer.parseInt(idCandidate);
                        firstIsHeader = false;
                    } catch (NumberFormatException ex) {
                        firstIsHeader = true;
                    }
                }

                if (!firstIsHeader) {
                    String linha = firstLine;
                    totalLinhasDados++;
                    String[] partes = splitCsv(linha, 4);
                    if (partes.length == 4) {
                        try {
                            String idStr = partes[0].trim();
                            String a2 = partes[1].trim();
                            String a3 = partes[2].trim();
                            String nome = partes[3].trim();

                            if (!idStr.isEmpty() && !a2.isEmpty() && !a3.isEmpty() && !nome.isEmpty()
                                    && a2.length() == 2 && a3.length() == 3
                                    && isAlpha(a2) && isAlpha(a3)) {
                                int id = Integer.parseInt(idStr);
                                if (id > 0) {
                                    boolean duplicado = false;
                                    for (Pais p : listaPaises) {
                                        if (p.id == id || p.alfa2.equalsIgnoreCase(a2) || p.alfa3.equalsIgnoreCase(a3)) {
                                            duplicado = true;
                                            break;
                                        }
                                    }
                                    if (!duplicado) {
                                        listaPaises.add(new Pais(id, a2, a3, nome));
                                        ok++;
                                    } else {
                                        erro++;
                                    }
                                } else {
                                    erro++;
                                }
                            } else {
                                erro++;
                            }
                        } catch (NumberFormatException e) {
                            erro++;
                        }
                    } else {
                        erro++;
                    }
                }
            }

            while (s.hasNextLine()) {
                String linha = s.nextLine();
                if (linha.trim().isEmpty()) continue;
                totalLinhasDados++;
                String[] partes = splitCsv(linha, 4);
                if (partes.length == 4) {
                    try {
                        String idStr = partes[0].trim();
                        String a2 = partes[1].trim();
                        String a3 = partes[2].trim();
                        String nome = partes[3].trim();

                        if (!idStr.isEmpty() && !a2.isEmpty() && !a3.isEmpty() && !nome.isEmpty()
                                && a2.length() == 2 && a3.length() == 3
                                && isAlpha(a2) && isAlpha(a3)) {
                            int id = Integer.parseInt(idStr);
                            if (id > 0) {
                                boolean duplicado = false;
                                for (Pais p : listaPaises) {
                                    if (p.id == id || p.alfa2.equalsIgnoreCase(a2) || p.alfa3.equalsIgnoreCase(a3)) {
                                        duplicado = true;
                                        break;
                                    }
                                }
                                if (!duplicado) {
                                    listaPaises.add(new Pais(id, a2, a3, nome));
                                    ok++;
                                } else {
                                    erro++;
                                }
                            } else {
                                erro++;
                            }
                        } else {
                            erro++;
                        }
                    } catch (NumberFormatException e) {
                        erro++;
                    }
                } else {
                    erro++;
                }
            }
            sumarioErros.add(0, "paises.csv | " + ok + " | " + erro + " | " + totalLinhasDados);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private static boolean lerCidades(File folder) {
        File f = new File(folder, "cidades.csv");
        int ok = 0;
        int erro = 0;
        int totalLinhasDados = 0;

        try (Scanner s = new Scanner(f)) {
            String firstLine = null;
            while (s.hasNextLine()) {
                firstLine = s.nextLine();
                if (!firstLine.trim().isEmpty()) break;
                firstLine = null;
            }

            if (firstLine != null) {
                String[] partesFirst = splitCsv(firstLine, 6);
                boolean firstIsHeader = false;
                if (partesFirst.length >= 1) {
                    String first = partesFirst[0].trim();
                    boolean isAlpha2 = first.length() == 2 && isAlpha(first);
                    boolean isInt = false;
                    try { Integer.parseInt(first); isInt = true; } catch (Exception ex) { }
                    if (!isAlpha2 && !isInt) firstIsHeader = true;
                }

                if (!firstIsHeader) {
                    String linha = firstLine;
                    totalLinhasDados++;
                    String[] partes = splitCsv(linha, 6);
                    if (partes.length == 6) {
                        try {
                            String a2orId = partes[0].trim();
                            String nome = partes[1].trim();
                            String popStr = partes[3].trim();

                            String a2 = null;
                            if (!a2orId.isEmpty() && a2orId.length() == 2 && isAlpha(a2orId)) {
                                a2 = a2orId;
                            } else {
                                try {
                                    int cid = Integer.parseInt(a2orId);
                                    for (Pais p : listaPaises) {
                                        if (p.id == cid) { a2 = p.alfa2; break; }
                                    }
                                } catch (NumberFormatException ex) { a2 = null; }
                            }

                            if (a2 != null && !nome.isEmpty() && !popStr.isEmpty() && a2.length() == 2 && isAlpha(a2)) {
                                double popD = Double.parseDouble(popStr);
                                int pop = (int) popD;
                                double lat = Double.parseDouble(partes[4].trim());
                                double lon = Double.parseDouble(partes[5].trim());
                                listaCidades.add(new Cidade(a2, nome, partes[2].trim(), pop, lat, lon));
                                ok++;
                            } else {
                                erro++;
                            }
                        } catch (NumberFormatException e) { erro++; }
                    } else { erro++; }
                }
            }

            while (s.hasNextLine()) {
                String linha = s.nextLine();
                if (linha.trim().isEmpty()) continue;
                totalLinhasDados++;
                String[] partes = splitCsv(linha, 6);
                if (partes.length == 6) {
                    try {
                        String a2orId = partes[0].trim();
                        String nome = partes[1].trim();
                        String popStr = partes[3].trim();

                        String a2 = null;
                        if (!a2orId.isEmpty() && a2orId.length() == 2 && isAlpha(a2orId)) {
                            a2 = a2orId;
                        } else {
                            try {
                                int cid = Integer.parseInt(a2orId);
                                for (Pais p : listaPaises) {
                                    if (p.id == cid) { a2 = p.alfa2; break; }
                                }
                            } catch (NumberFormatException ex) { a2 = null; }
                        }

                        if (a2 != null && !popStr.isEmpty() && a2.length() == 2 && isAlpha(a2)) {
                            double popD = Double.parseDouble(popStr);
                            int pop = (int) popD;
                            double lat = Double.parseDouble(partes[4].trim());
                            double lon = Double.parseDouble(partes[5].trim());
                            listaCidades.add(new Cidade(a2, nome, partes[2].trim(), pop, lat, lon));
                            ok++;
                        } else { erro++; }
                    } catch (NumberFormatException e) { erro++; }
                } else { erro++; }
            }
            sumarioErros.add("cidades.csv | " + ok + " | " + erro + " | " + totalLinhasDados);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private static boolean lerDados(File folder) {
        File f = new File(folder, "populacao.csv");
        int ok = 0;
        int erro = 0;
        int totalLinhasDados = 0;

        try (Scanner s = new Scanner(f)) {
            String firstLine = null;
            while (s.hasNextLine()) {
                firstLine = s.nextLine();
                if (!firstLine.trim().isEmpty()) break;
                firstLine = null;
            }

            if (firstLine != null) {
                String[] partesFirst = splitCsv(firstLine, 5);
                boolean firstIsHeader = true;
                if (partesFirst.length >= 1) {
                    String idCandidate = partesFirst[0].trim();
                    try { Integer.parseInt(idCandidate); firstIsHeader = false; } catch (NumberFormatException ex) { firstIsHeader = true; }
                }

                if (!firstIsHeader) {
                    totalLinhasDados++;
                    String[] partes = splitCsv(firstLine, 5);
                    if (partes.length == 5) {
                        try {
                            listaDados.add(new DadosDemograficos(
                                    Integer.parseInt(partes[0].trim()),
                                    Integer.parseInt(partes[1].trim()),
                                    Integer.parseInt(partes[2].trim()),
                                    Integer.parseInt(partes[3].trim()),
                                    Double.parseDouble(partes[4].trim())
                            ));
                            ok++;
                        } catch (NumberFormatException e) { erro++; }
                    } else { erro++; }
                }
            }

            while (s.hasNextLine()) {
                String linha = s.nextLine();
                if (linha.trim().isEmpty()) continue;
                totalLinhasDados++;
                String[] partes = splitCsv(linha, 5);
                if (partes.length == 5) {
                    try {
                        listaDados.add(new DadosDemograficos(
                                Integer.parseInt(partes[0].trim()),
                                Integer.parseInt(partes[1].trim()),
                                Integer.parseInt(partes[2].trim()),
                                Integer.parseInt(partes[3].trim()),
                                Double.parseDouble(partes[4].trim())
                        ));
                        ok++;
                    } catch (NumberFormatException e) { erro++; }
                } else { erro++; }
            }

            sumarioErros.add("populacao.csv | " + ok + " | " + erro + " | " + totalLinhasDados);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static ArrayList getObjects(TipoEntidade tipo) {
        if (tipo == TipoEntidade.PAIS) {
            return listaPaises;
        }
        if (tipo == TipoEntidade.CIDADE) {
            return listaCidades;
        }
        if (tipo == TipoEntidade.DADOS_DEMOGRAFICOS) {
            return listaDados;
        }
        if (tipo == TipoEntidade.INPUT_INVALIDO) {
            return sumarioErros;
        }
        return null;
    }
}

