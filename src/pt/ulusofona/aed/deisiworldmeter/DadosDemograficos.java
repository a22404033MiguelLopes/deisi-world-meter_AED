package pt.ulusofona.aed.deisiworldmeter;

public class DadosDemograficos {
    int id;
    int ano;
    int popMasculina;
    int popFeminina;
    double densidade;

    public DadosDemograficos(int id, int ano, int popMasculina, int popFeminina, double densidade) {
        this.id = id;
        this.ano = ano;
        this.popMasculina = popMasculina;
        this.popFeminina = popFeminina;
        this.densidade = densidade;
    }
}