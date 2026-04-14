package pt.ulusofona.aed.deisiworldmeter;

public class Pais {
    int id;
    String codigoAlfa2;
    String codigoAlfa3;
    String nome;
    int numeroDeCidades;

    public Pais(int id, String codigoAlfa2, String codigoAlfa3, String nome) {
        this.id = id;
        this.codigoAlfa2 = codigoAlfa2;
        this.codigoAlfa3 = codigoAlfa3;
        this.nome = nome;
        this.numeroDeCidades = 0;
    }

    @Override
    public String toString() {
        return nome + " | " + id + " | " + codigoAlfa2.toUpperCase() + " | " + codigoAlfa3.toUpperCase() + " | " + numeroDeCidades;
    }
}