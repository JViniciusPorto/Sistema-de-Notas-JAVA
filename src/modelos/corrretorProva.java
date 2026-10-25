package modelos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class corrretorProva {

    public boolean marcouTudoIgual(String respostas){
        char primeira = respostas.charAt(0);
        for(int i = 0; i < respostas.length(); i++ ){
            if(respostas.charAt(i) != primeira){
                return false;
            }
        }
        return true;
    }

    public int corrigirProva(String respostas, String gabarito){
        if(marcouTudoIgual(respostas)){
            return 0;
        }
        int acertos = 0;
        for(int i = 0; i < respostas.length() && i < gabarito.length(); i++){
            if(respostas.charAt(i) == gabarito.charAt(i)){
                acertos++;
            }
        }
        return acertos;
    }

    public List<Nota> corrigirDisciplina(Disciplina disciplina, String gabarito){
        List<Nota> notas = new ArrayList<>();
        for(Map.Entry< Aluno, String> entrada : disciplina.getRegistros().entrySet()){
            int valor = corrigirProva(entrada.getValue(), gabarito);
            notas.add(new Nota(entrada.getKey(), disciplina, valor));
        }
        return notas;
    }

      public List<Nota> ordenarAlfabetico(List<Nota> notas) {
        List<Nota> copia = new ArrayList<>(notas);
        copia.sort((a, b) -> a.getAluno().getNome().compareToIgnoreCase(b.getAluno().getNome()));
        return copia;
    }
    
    // Em caso de notas iguais, vai por ordem alfabetica
    public List<Nota> ordenarPorNotaDecrescente(List<Nota> notas) {
        List<Nota> copia = new ArrayList<>(notas);
        copia.sort((a, b) -> {
            int porNota = Integer.compare(b.getValor(), a.getValor());
            if (porNota != 0) {
                return porNota;
            }
            return a.getAluno().getNome().compareToIgnoreCase(b.getAluno().getNome());
        });
        return copia;
    }


    public double calcularMediaTurma(List<Nota> notas) {
        if (notas.isEmpty()) {
            return 0;
        }
        double soma = 0;
        for (Nota nota : notas) {
            soma += nota.getValor();
        }
        return Math.round(soma / notas.size() * 10) / 10.0;
    }

}
