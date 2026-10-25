package modelos;

public class Nota {
    private final Aluno aluno;
    private final Disciplina disciplina;
    private final int valor;

    public Nota(Aluno aluno, Disciplina materia, int valor){
        this.aluno = aluno;
        this.disciplina = materia;
        this.valor = valor;
    }

    public Aluno getAluno(){
        return this.aluno;
    }

    public Disciplina getDisciplina(){
        return this.disciplina;
    }

    public int getValor(){
        return this.valor;
    }

}
