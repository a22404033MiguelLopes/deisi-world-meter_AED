package pt.ulusofona.aed.deisiworldmeter;

public class RegistoPopulacao {
    int idPais;
    int ano;
    long populacaoMasculina;
    long populacaoFeminina;
    double densidade;

    public RegistoPopulacao(int idPais, int ano, long populacaoMasculina,
                            long populacaoFeminina, double densidade) {
        this.idPais = idPais;
        this.ano = ano;
        this.populacaoMasculina = populacaoMasculina;
        this.populacaoFeminina = populacaoFeminina;
        this.densidade = densidade;
    }

    public long getPopulacaoTotal() {
        return populacaoMasculina + populacaoFeminina;
    }
}