package pt.ulusofona.aed.deisiworldmeter;

public class Cidade {
    String alfa2;
    String nome;
    String regiao;
    int populacao;
    double latitude;
    double longitude;

    public Cidade(String alfa2, String nome, String regiao, int populacao, double latitude, double longitude) {
        this.alfa2 = alfa2.toUpperCase().trim();
        this.nome = nome.trim();
        this.regiao = regiao.trim();
        this.populacao = populacao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return this.nome + " | " + this.alfa2 + " | " + this.regiao + " | " + this.populacao + " | (" + this.latitude + "," + this.longitude + ")";
    }
}