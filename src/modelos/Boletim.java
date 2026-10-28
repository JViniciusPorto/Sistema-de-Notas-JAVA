package modelos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Boletim {
    private Aluno aluno;
    private List<Nota> notas;
    private double media;

    public Boletim(Aluno aluno, List<Nota> notas) {
        this.aluno = aluno;
        this.notas = new ArrayList<>(notas);
        calcularMedia();
    }

    public void calcularMedia() {
        if (notas.isEmpty()) {
            this.media = 0;
            return;
        }
        double soma = 0;
        for (Nota nota : notas) {
            soma += nota.getValor();
        }
        this.media = Math.round(soma / notas.size() * 10) / 10.0; // só para arredondar a média em 1 casa decimal
    }

    public String verificarAprovacao() {
        if (this.media >= 7.0) {
            return "APROVADO";
        } else {
            return "REPROVADO";
        }
    }

    public Aluno getAluno() {
        return this.aluno;
    }

    public List<Nota> getNotas() {
        return Collections.unmodifiableList(notas);
    }

    public double getMedia() {
        return this.media;
    }

    public String gerarTexto() {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();

        sb.append("====================================").append(nl);
        sb.append("BOLETIM GERAL DO ALUNO").append(nl);
        sb.append("====================================").append(nl).append(nl);
        sb.append("Aluno: ").append(aluno.getNome()).append(nl).append(nl);

        for (Nota nota : notas) {
            sb.append(String.format("Disciplina: %-12s Nota: %d",
                    nota.getDisciplina().getNome(), nota.getValor())).append(nl);
        }

        sb.append(nl);
        // Locale.US garante "8.0" (e não "8,0") mesmo com o Windows em português
        sb.append(String.format(Locale.US, "Média geral: %.1f", media)).append(nl);
        sb.append("Situação final: ").append(verificarAprovacao()).append(nl);
        sb.append("====================================").append(nl);
        return sb.toString();
    }
}