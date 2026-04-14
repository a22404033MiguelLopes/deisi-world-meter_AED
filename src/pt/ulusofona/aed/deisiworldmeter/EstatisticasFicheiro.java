package pt.ulusofona.aed.deisiworldmeter;

public class EstatisticasFicheiro {
    String nomeFicheiro;
    int linhasValidas;
    int linhasInvalidas;
    int primeiraLinhaInvalida;

    public EstatisticasFicheiro(String nomeFicheiro) {
        this.nomeFicheiro = nomeFicheiro;
        this.linhasValidas = 0;
        this.linhasInvalidas = 0;
        this.primeiraLinhaInvalida = -1;
    }

    public void registarLinhaInvalida(int numeroLinha) {
        this.linhasInvalidas++;
        if (this.primeiraLinhaInvalida == -1) {
            this.primeiraLinhaInvalida = numeroLinha;
        }
    }

    @Override
    public String toString() {
        return nomeFicheiro + " | " + linhasValidas + " | " + linhasInvalidas + " | " + primeiraLinhaInvalida;
    }
}