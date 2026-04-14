package pt.ulusofona.aed.deisiworldmeter;

public class Cidade {
    String codigoAlfa2DoPais;
    String nome;
    String codigoRegiao;
    double populacao;
    double latitude;
    double longitude;

    public Cidade(String codigoAlfa2DoPais, String nome, String codigoRegiao,
                  double populacao, double latitude, double longitude) {
        this.codigoAlfa2DoPais = codigoAlfa2DoPais;
        this.nome = nome;
        this.codigoRegiao = codigoRegiao;
        this.populacao = populacao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return nome + " | " + codigoAlfa2DoPais.toUpperCase() + " | " + codigoRegiao + " | " +
                (int) populacao + " | (" + latitude + "," + longitude + ")";
    }
}