package gerenciar_arquivos;

import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import enums.ResultadoExclusao;
import enums.ResultadoSalvamento;
import enums.ResultadoTratamento;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
// === BOLETIM === imports novos
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import modelos.*;
import utilidades.TratamentoDeEntradas;

public class GerenciarArquivos {
    public static final String SUFIXO_RESULTADO_ALFABETICO = "_alfabetico.txt";
    public static final String SUFIXO_RESULTADO_POR_NOTA = "_por_nota.txt";
    private final Path pastaResultados = Path.of("src", "persistencia", "resultados");
    private final Path pastaBoletins = Path.of("src", "persistencia", "boletins");
    private final Path pastaDisciplina = Path.of("src","persistencia","disciplinas");
    private final Path pastaGabaritos = Path.of("src","persistencia","gabaritos");

    public GerenciarArquivos(){
        try{
            Files.createDirectories(pastaDisciplina);
            Files.createDirectories(pastaGabaritos);
        }catch(IOException e){
            throw new RuntimeException("Não foi possível criar os diretórios", e);
        }
    }

    public ResultadoSalvamento salvarRegistroDisciplina(Disciplina disciplina){
        Path arquivo = pastaDisciplina.resolve(disciplina.getNome()+".txt");
        boolean criou = false;
        Map<Aluno,String> copia = disciplina.getRegistros();
        try(BufferedWriter escritor = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8,StandardOpenOption.CREATE_NEW)){
            criou = true;
            for(Map.Entry<Aluno,String> entrada : copia.entrySet()){
                Aluno alunoParaRegistro = entrada.getKey();
                String respostaAluno = entrada.getValue();
                String registrarLinha = respostaAluno + "\t" + alunoParaRegistro.getNome();
                escritor.write(registrarLinha);
                escritor.newLine();
            }
            
            return ResultadoSalvamento.SALVO;
        }catch(FileAlreadyExistsException e){
            return ResultadoSalvamento.ERRO_ARQUIVO;
        }catch(AccessDeniedException e){
            apagarSeCriou(criou, arquivo);
            return ResultadoSalvamento.ARQUIVO_INACESSIVEL;
        }catch(IOException e){
            apagarSeCriou(criou, arquivo);
            return  ResultadoSalvamento.ERRO_ARQUIVO;
        }
    }

    private void apagarSeCriou(boolean criou, Path destino){
        if(criou){
            try{
                Files.deleteIfExists(destino);
            }catch(IOException ignorado){
            }
        }
    }

    public Disciplina lerArquivoDisciplina(String nomeDisciplina){
        Path arquivo = pastaDisciplina.resolve(nomeDisciplina+".txt");
        Disciplina disciplina = new Disciplina(nomeDisciplina);

        try(BufferedReader leitor = Files.newBufferedReader(arquivo,StandardCharsets.UTF_8);){
            
            String linha;
            while((linha = leitor.readLine()) != null){

                if(linha.isBlank()){
                    continue;
                }

                String[] partes = linha.split("\t");
                if(partes.length<2){
                    continue;
                }
                
                String resposta = TratamentoDeEntradas.tratarResposta(partes[0]);
                if(TratamentoDeEntradas.validarResposta(resposta) != ResultadoTratamento.VALIDO){
                    continue;
                }
                String nome = partes[1];
                Aluno aluno = new Aluno(nome);
                disciplina.adicionarAluno(aluno, resposta);
            }

            return disciplina;
        }catch(IOException e){
            return null;
        }
    }

    public boolean existeDisciplina(String nomeDisciplina) {
        Path arquivo = pastaDisciplina.resolve(nomeDisciplina + ".txt");
        return arquivo.toFile().exists();
    }

    // Nomes das disciplinas cadastradas (arquivos .txt da pasta de disciplinas, sem a extensão), em ordem alfabética.
    public List<String> listarDisciplinas() {
        try (Stream<Path> arquivos = Files.list(pastaDisciplina)) {
            return arquivos
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .filter(nome -> nome.toLowerCase().endsWith(".txt") && nome.length() > 4)
                    .map(nome -> nome.substring(0, nome.length() - 4))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    // Grava os dois resultados da disciplina na pasta de resultados. Se já existirem, são sobrescritos.
    public ResultadoSalvamento salvarResultados(String nomeDisciplina, String textoAlfabetico, String textoPorNota) {
        try {
            Files.createDirectories(pastaResultados);
            Files.writeString(pastaResultados.resolve(nomeDisciplina + SUFIXO_RESULTADO_ALFABETICO),
                    textoAlfabetico, StandardCharsets.UTF_8);
            Files.writeString(pastaResultados.resolve(nomeDisciplina + SUFIXO_RESULTADO_POR_NOTA),
                    textoPorNota, StandardCharsets.UTF_8);
            return ResultadoSalvamento.SALVO;
        } catch (AccessDeniedException | NoSuchFileException e) {
            return ResultadoSalvamento.ARQUIVO_INACESSIVEL;
        } catch (IOException | InvalidPathException e) {
            return ResultadoSalvamento.ERRO_ARQUIVO;
        }
    }

    public Path getPastaResultados() {
        return pastaResultados;
    }

    public boolean existeDiretorio(Path diretorio) {
        return Files.isDirectory(diretorio);
    }

    // Lista só os resultados alfabéticos (NOME_alfabetico.txt) do diretório, em ordem alfabética.
    // Outros arquivos da pasta (ex.: resultados por nota) são ignorados.
    public List<Path> listarResultadosAlfabeticos(Path diretorio) {
        try (Stream<Path> arquivos = Files.list(diretorio)) {
            return arquivos
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String nome = p.getFileName().toString().toLowerCase();
                        return nome.endsWith(SUFIXO_RESULTADO_ALFABETICO)
                                && nome.length() > SUFIXO_RESULTADO_ALFABETICO.length();
                    })
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    public List<String> lerLinhas(Path arquivo) {
        try {
            return Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    // O nome do arquivo é o nome do aluno. Se já existir, é sobrescrito.
    // A pasta de boletins é criada aqui, só quando é preciso.
    public ResultadoSalvamento salvarBoletim(String nomeAluno, String conteudo) {
        String nomeArquivo = nomeAluno.replaceAll("[\\\\/:*?\"<>|]", "_") + ".txt";
        try {
            Files.createDirectories(pastaBoletins);
            Files.writeString(pastaBoletins.resolve(nomeArquivo), conteudo, StandardCharsets.UTF_8);
            return ResultadoSalvamento.SALVO;
        } catch (AccessDeniedException | NoSuchFileException e) {
            return ResultadoSalvamento.ARQUIVO_INACESSIVEL;
        } catch (IOException | InvalidPathException e) {
            return ResultadoSalvamento.ERRO_ARQUIVO;
        }
    }

    public ResultadoSalvamento criarArquivoGabarito(String nomeArquivo,String conteudo){
        try{
            Files.writeString(pastaGabaritos.resolve(nomeArquivo+".txt"), conteudo, StandardCharsets.UTF_8);
            return ResultadoSalvamento.SALVO;
        }catch(AccessDeniedException | NoSuchFileException e){
            return ResultadoSalvamento.ARQUIVO_INACESSIVEL;
        }catch(IOException e){
            return ResultadoSalvamento.ERRO_ARQUIVO;
        }
    }

    // Apaga os .txt da pasta de boletins. Se a pasta ainda não existe, não há nada a apagar.
    public ResultadoExclusao limparBoletins() {
        if (!Files.isDirectory(pastaBoletins)) {
            return ResultadoExclusao.NAO_E_DIRETORIO;
        }
        try (Stream<Path> arquivos = Files.list(pastaBoletins)) {
            // primeiro junta numa lista, depois apaga (não se apaga enquanto a pasta está sendo percorrida)
            List<Path> antigos = arquivos
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".txt"))
                    .collect(Collectors.toList());
            for (Path antigo : antigos) {
                Files.delete(antigo);
            }
            return ResultadoExclusao.EXCLUIDO;
        } catch (AccessDeniedException e) {
            return ResultadoExclusao.ARQUIVO_INACESSIVEL;
        } catch (IOException e) {
            return ResultadoExclusao.ERRO_ARQUIVO;
        }
    }

    public Path transformarCaminhoGabarito(String nome){
        return pastaGabaritos.resolve(nome+".txt");
    }

    public Path getPastaBoletins() {
        return pastaBoletins;
    }
}