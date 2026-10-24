package servicos;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import enums.ResultadoGeracao;
import enums.ResultadoSalvamento;
import gerenciar_arquivos.GerenciarArquivos;
import modelos.Aluno;
import modelos.Boletim;
import modelos.Disciplina;
import modelos.Nota;

public class GeradorBoletim {
    private final GerenciarArquivos gerenciador;
    private final List<Boletim> boletinsGerados = new ArrayList<>();
    private String detalheErro = "";

    public GeradorBoletim(GerenciarArquivos gerenciador) {
        this.gerenciador = gerenciador;
    }

    // Lê os relatórios alfabéticos (NOME_DA_DISCIPLINA_alfabetico.txt) do diretório, junta as notas
    // de cada aluno, monta um Boletim por aluno e salva cada um em .txt.
    // Não imprime nada: devolve um ResultadoGeracao e o Sistema decide as mensagens.
    public ResultadoGeracao gerar(Path diretorioResultados) {
        boletinsGerados.clear();
        detalheErro = "";

        if (!gerenciador.existeDiretorio(diretorioResultados)) {
            return ResultadoGeracao.DIRETORIO_INEXISTENTE;
        }

        List<Path> resultados = gerenciador.listarResultadosAlfabeticos(diretorioResultados);
        if (resultados.isEmpty()) {
            return ResultadoGeracao.SEM_RESULTADOS;
        }

        // TreeMap: os boletins saem em ordem alfabética de aluno
        Map<String, Aluno> alunos = new TreeMap<>();
        Map<String, List<Nota>> notasPorAluno = new TreeMap<>();

        for (Path resultado : resultados) {
            List<String> linhas = gerenciador.lerLinhas(resultado);
            if (linhas == null) {
                detalheErro = resultado.getFileName().toString();
                return ResultadoGeracao.ERRO_LEITURA;
            }

            Disciplina disciplina = new Disciplina(extrairNomeDisciplina(resultado));

            for (int i = 0; i < linhas.size(); i++) {
                String linha = linhas.get(i);
                if (linha.isBlank()) {
                    continue;
                }

                // formato esperado: NOME<TAB>NOTA
                int posicaoTab = linha.lastIndexOf('\t');
                String nome = (posicaoTab < 0) ? "" : linha.substring(0, posicaoTab).trim();
                int valor = (posicaoTab < 0) ? -1 : converterNota(linha.substring(posicaoTab + 1));

                if (nome.isEmpty() || valor < 0) {
                    detalheErro = resultado.getFileName() + ", linha " + (i + 1) + ": \"" + linha + "\"";
                    return ResultadoGeracao.FORMATO_INVALIDO;
                }

                // um único objeto Aluno por nome, compartilhado entre as disciplinas
                Aluno aluno = alunos.computeIfAbsent(nome, Aluno::new);
                notasPorAluno
                        .computeIfAbsent(nome, n -> new ArrayList<>())
                        .add(new Nota(aluno, disciplina, valor));
            }
        }

        if (notasPorAluno.isEmpty()) {
            return ResultadoGeracao.SEM_RESULTADOS;
        }

        for (Map.Entry<String, List<Nota>> entrada : notasPorAluno.entrySet()) {
            String nomeAluno = entrada.getKey();
            Boletim boletim = new Boletim(alunos.get(nomeAluno), entrada.getValue());

            if (gerenciador.salvarBoletim(nomeAluno, boletim.gerarTexto()) != ResultadoSalvamento.SALVO) {
                detalheErro = nomeAluno;
                return ResultadoGeracao.ERRO_ESCRITA;
            }
            boletinsGerados.add(boletim);
        }

        return ResultadoGeracao.GERADO;
    }

    public List<Boletim> getBoletinsGerados() {
        return Collections.unmodifiableList(boletinsGerados);
    }

    // Quando o resultado não é GERADO: arquivo/linha/aluno que causou o problema
    public String getDetalheErro() {
        return this.detalheErro;
    }

    // "POO_alfabetico.txt" -> "POO". Remove só o sufixo final, então disciplinas com "_" no nome funcionam.
    private String extrairNomeDisciplina(Path resultado) {
        String nomeArquivo = resultado.getFileName().toString();
        int tamanhoSufixo = GerenciarArquivos.SUFIXO_RESULTADO_ALFABETICO.length();
        return nomeArquivo.substring(0, nomeArquivo.length() - tamanhoSufixo);
    }

    // Nota válida = inteiro de 0 a 10 (quantidade de acertos). Devolve -1 se não for.
    private int converterNota(String texto) {
        try {
            int valor = Integer.parseInt(texto.trim());
            return (valor >= 0 && valor <= 10) ? valor : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}