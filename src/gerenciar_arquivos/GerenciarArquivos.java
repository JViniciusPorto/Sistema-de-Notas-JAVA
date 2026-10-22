package gerenciar_arquivos;

import java.nio.file.Path;
import java.util.Map;

import enums.ResultadoCriacao;
import enums.ResultadoExclusao;
import enums.ResultadoSalvamento;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import modelos.*;

public class GerenciarArquivos {
    private final Path pastaDisciplina;
    private Path arquivo;
    private FileWriter escritorDeArquivos;
    private BufferedWriter escritorDeLinhas;
    private FileReader leitorDeArquivos;
    private BufferedReader leitorDeLinhas;

    public GerenciarArquivos(){
        this.pastaDisciplina = Path.of("src","persistencia","disciplinas");
        try{
            Files.createDirectories(pastaDisciplina);
        }catch(IOException e){
            throw new RuntimeException("Não foi possível criar a pasta de disciplinas.", e);
        }
    }

    public ResultadoCriacao criarArquivoDisciplina(Disciplina disciplina){
        this.arquivo = pastaDisciplina.resolve(disciplina.getNome()+".txt");
        if(!existeDisciplina(disciplina.getNome())){
            try{
                Files.createFile(arquivo);
                return ResultadoCriacao.CRIADO;
            }catch(IOException e){
                return ResultadoCriacao.ERRO;
            }
        }else{
            return ResultadoCriacao.JA_EXISTE;
        }
    }

    public ResultadoSalvamento salvarRegistroDisciplina(Disciplina disciplina){
        arquivo = pastaDisciplina.resolve(disciplina.getNome()+".txt");
        File arquivoFile = arquivo.toFile();
        Map<Aluno,String> copia = disciplina.getRegistros();

        try{
            escritorDeArquivos = new FileWriter(arquivoFile, true);
            escritorDeLinhas = new BufferedWriter(escritorDeArquivos);
            for(Map.Entry<Aluno,String> entrada : copia.entrySet()){
                Aluno alunoParaRegistro = entrada.getKey();
                String respostaAluno = entrada.getValue();
                String registrarLinha = respostaAluno + "\t" + alunoParaRegistro.getNome();
                escritorDeLinhas.write(registrarLinha);
                escritorDeLinhas.newLine();
            }
            escritorDeLinhas.close();
            return ResultadoSalvamento.SALVO;
        }catch(FileNotFoundException e){
            return ResultadoSalvamento.ARQUIVO_INACESSIVEL;
        }catch(IOException e){
            return ResultadoSalvamento.ERRO_ARQUIVO;
        }
    }

    public Disciplina lerArquivoDisciplina(String nomeDisciplina){
        this.arquivo = pastaDisciplina.resolve(nomeDisciplina+".txt");
        Disciplina disciplina = new Disciplina(nomeDisciplina);
        File arquivoFile = arquivo.toFile();
        try{
            leitorDeArquivos = new FileReader(arquivoFile);
            leitorDeLinhas = new BufferedReader(leitorDeArquivos);
            String linha;
            while((linha = leitorDeLinhas.readLine()) != null){
                String[] partes = linha.split("\t");
                String resposta = partes[0];
                String nome = partes[1];
                Aluno aluno = new Aluno(nome);
                disciplina.adicionarAluno(aluno, resposta);
            }
            leitorDeLinhas.close();
            return disciplina;
        }catch(IOException e){
            return null;
        }
    }

    public boolean existeDisciplina(String nomeDisciplina) {
        Path arquivo = pastaDisciplina.resolve(nomeDisciplina + ".txt");
        return arquivo.toFile().exists();
    }

    public ResultadoExclusao excluirDisciplina(String nomeDisciplina) {
        Path arquivo = pastaDisciplina.resolve(nomeDisciplina + ".txt");

        try {
            Files.delete(arquivo);
            return ResultadoExclusao.EXCLUIDO;
        }catch(NoSuchFileException e){
            return ResultadoExclusao.NAO_EXISTE;
        }catch(IOException e){
            return ResultadoExclusao.ERRO;
        }
    }

}
