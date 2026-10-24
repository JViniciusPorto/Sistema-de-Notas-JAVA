package modelos;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class Disciplina {
  private String nome;
  private Map<Aluno,String> registro;

  public Disciplina(String nome){
    this.nome = nome;
    this.registro = new LinkedHashMap<>();
  }

  public void adicionarAluno(Aluno aluno, String resposta){
    if(!isAlunoContido(aluno)){
        registro.put(aluno, resposta);
    }else{
        System.out.println("Esse aluno já foi registrado!");
    }
  }

  public boolean isAlunoContido(Aluno aluno){
    if(registro.isEmpty()){
        return false;
    }
    for(Map.Entry<Aluno,String> entrada : registro.entrySet()){
        Aluno temporario = entrada.getKey();
        if(temporario.getNome().equals(aluno.getNome())){
            return true;
        }
    }
    return false;
  }

  public Map<Aluno,String> getRegistros(){
    return Collections.unmodifiableMap(registro);
  }

  public List<Aluno> getNomesAlunos(){
    List<Aluno> nomesAlunos = new ArrayList<>();
    
    for(Map.Entry<Aluno,String> entrada : registro.entrySet()){
        nomesAlunos.add(entrada.getKey());
    }
    return Collections.unmodifiableList(nomesAlunos);
  }

  public String getNome(){
    return this.nome;
  }
}
