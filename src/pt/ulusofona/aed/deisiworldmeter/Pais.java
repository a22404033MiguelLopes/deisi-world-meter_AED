package pt.ulusofona.aed.deisiworldmeter;

import java.util.ArrayList;

public class Pais {
    int id;
    String alfa2;
    String alfa3;
    String nome;

    public Pais(int id, String alfa2, String alfa3, String nome) {
        this.id = id;
        this.alfa2 = alfa2.toUpperCase().trim();
        this.alfa3 = alfa3.toUpperCase().trim();
        this.nome = nome.trim();
    }

    public int getQuantidadeCidades() {
        ArrayList<Cidade> cidades = Main.getObjects(TipoEntidade.CIDADE);
        int contador = 0;

        if (cidades != null) {
            for (int i = 0; i < cidades.size(); i++) {
                Cidade c = cidades.get(i);
                // contar quando a cidade referencia o país pelo código alfa2
                if (c.alfa2 != null && c.alfa2.equalsIgnoreCase(this.alfa2)) {
                    contador = contador + 1;
                }
            }
        }

        return contador;
    }

    @Override
    public String toString() {
        if (this.id < 700) {
            return this.nome + " | " + this.id + " | " + this.alfa2 + " | " + this.alfa3;
        } else {
            return this.nome + " | " + this.id + " | " + this.alfa2 + " | " + this.alfa3 + " | " + this.getQuantidadeCidades();
        }
    }
}