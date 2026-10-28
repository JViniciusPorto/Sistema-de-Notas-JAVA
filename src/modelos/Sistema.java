// use chcp 65001 caso dê erro de formatação no terminal
package modelos;

import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import enums.ResultadoGeracao;
import enums.ResultadoSalvamento;
import enums.ResultadoTratamento;
import gerenciar_arquivos.GerenciarArquivos;
import utilidades.LimparTela;
import utilidades.TratamentoDeEntradas;

public class Sistema {
    private GerenciarArquivos gerenciador;
    private final CorretorProva corretor = new CorretorProva();
    private final GeradorBoletim gerador;
    private Scanner teclado;
    private final String respostaSaida = "SALVAR";
    
    private ResultadoTratamento resultado;

    public Sistema(){
        gerenciador = new GerenciarArquivos();
        gerador = new GeradorBoletim(gerenciador);
        teclado = new Scanner(System.in,StandardCharsets.UTF_8);
    }

    private void cadastrarDisciplina(){
        Disciplina disciplina;
        String nomeArquivo;
        String texto;
        String resposta;
        String nomeAluno;
        ResultadoSalvamento estadoSalvamento;

        while(true){
            LimparTela.limparTerminal();
            System.out.println("\n==============================================");
            System.out.println("                Criação do Arquivo              ");
            System.out.println("==============================================\n");
            System.out.println("Digite o nome do arquivo: ");
            nomeArquivo = TratamentoDeEntradas.tratarNomeArquivo(teclado.nextLine());

            if(nomeArquivo.isEmpty()){
                System.out.println("Digite um nome válido (letras ou números)!");
                LimparTela.esperar(1000);
                continue;
            }
            if(gerenciador.existeDisciplina(nomeArquivo)){
                System.out.println("Já existe um arquivo com esse nome!");
                System.out.println("Escreva outro nome!");
                LimparTela.esperar(1000);
                continue;
            }
            disciplina = new Disciplina(nomeArquivo);

            System.out.printf("O título do arquivo será %s \n", nomeArquivo);
            System.out.println("Confirma o título? (S/N)");
            texto = TratamentoDeEntradas.tratarTexto(teclado.nextLine());
            if(texto.equals("S")){
                break;
            }else if(!texto.equals("N")){
                System.out.println("Erro ao confirmar título!");
                LimparTela.esperar(1000);
            }
        }

        Aluno aluno;
        while(true){
            LimparTela.limparTerminal();
            System.out.println("\n============================================");
            System.out.println("    Registro de Gabaritos dos Estudantes    ");
            System.out.println("============================================\n");

            System.out.println("Digite o nome do aluno:");
            nomeAluno = teclado.nextLine();
            nomeAluno = TratamentoDeEntradas.tratarNome(nomeAluno);
            if(!nomeAluno.isEmpty()){
                aluno = new Aluno(nomeAluno);
                System.out.println("Nome recebido: "+nomeAluno);
                LimparTela.esperar(1000);
            }else{
                System.out.println("Digite um nome válido!");
                LimparTela.esperar(1000);
                continue;
            }

            if(disciplina.isAlunoContido(aluno)){
                System.out.println("Esse aluno já foi registrado!");
                LimparTela.esperar(1000);
                continue;
            }

            while(true){
                System.out.println("Digite o gabarito do aluno:");
                System.out.println("[Somente com V/F]");
                resposta = teclado.nextLine();
                resposta = TratamentoDeEntradas.tratarResposta(resposta);
                resultado = TratamentoDeEntradas.validarResposta(resposta);

                if(resultado == ResultadoTratamento.VALIDO){
                    break;
                }else if(resultado == ResultadoTratamento.CARACTERE_INVALIDO){
                    System.out.println("Digite somente com V/F");
                    LimparTela.esperar(1000);
                    continue;
                }else if( resultado == ResultadoTratamento.INCOMPLETO){
                    System.out.println("Digite 10 respostas para o gabarito!");
                    LimparTela.esperar(1000);
                    continue;
                }else{
                    System.out.println("Erro ao registrar o gabarito!");
                    System.out.println("Tente novamente!");
                    LimparTela.esperar(1000);
                    continue;
                }

            }
            disciplina.adicionarAluno(aluno, resposta);

            System.out.println("\n--------------------------------------------");
            System.out.println("             FINALIZAR CADASTRO");
            System.out.println("--------------------------------------------");
            System.out.println("Digite SALVAR para encerrar o cadastro.");
            System.out.println("Pressione ENTER para continuar.");
            System.out.print("\nOpção: ");
            texto = teclado.nextLine();
            texto = TratamentoDeEntradas.tratarTexto(texto);
            if(texto.equals(respostaSaida)){
                break;
            }
        }

        estadoSalvamento = gerenciador.salvarRegistroDisciplina(disciplina);

        if(estadoSalvamento == ResultadoSalvamento.SALVO){
            System.out.println("Registros dos gabaritos salvos!");
            LimparTela.esperar(1000);
            registrarGabaritoDisciplina(disciplina);
        }else if(estadoSalvamento == ResultadoSalvamento.ARQUIVO_INACESSIVEL){
            System.out.println("Erro de acessibilidade!");
            System.out.println("Tente novamente mais tarde ou entre\nem contato com os desenvolvedores");
        }else if(estadoSalvamento == ResultadoSalvamento.ERRO_ARQUIVO){
            System.out.println("Erro ao salvar registros!");
            System.out.println("Tente novamente mais tarde ou entre\nem contato com os desenvolvedores");
        }
    }

    private void gerarResultadoDisciplina(){
        String texto;
        ResultadoSalvamento estadoSalvamento;

        LimparTela.limparTerminal();
        System.out.println("\n==============================================");
        System.out.println("             Geração de Resultados            ");
        System.out.println("==============================================\n");

        List<String> disciplinas = gerenciador.listarDisciplinas();
        if(disciplinas.isEmpty()){
            System.out.println("Nenhuma disciplina cadastrada!");
            LimparTela.esperar(1500);
            return;
        }

        String nomeDisciplina;
        while(true){
            System.out.println("Disciplinas disponíveis:");
            for(int i = 0; i < disciplinas.size(); i++){
                System.out.printf("%d - %s\n", i + 1, disciplinas.get(i));
            }
            System.out.println("0 - Voltar");
            System.out.print("\nEscolha: ");

            texto = teclado.nextLine().trim();
            int opcao = TratamentoDeEntradas.tratarNumero(texto);
            
            if(opcao == 0){
                return;
            }
            if(opcao >= 1 && opcao <= disciplinas.size()){
                nomeDisciplina = disciplinas.get(opcao - 1);
                break;
            }else{
                System.out.println("Opção inválida!");
            }
            
            LimparTela.esperar(1000);
            LimparTela.limparTerminal();
        }

        Disciplina disciplina = gerenciador.lerArquivoDisciplina(nomeDisciplina);
        if(disciplina == null){
            System.out.println("Erro ao ler o arquivo da disciplina " + nomeDisciplina + "!");
            LimparTela.esperar(1500);
            return;
        }
        if(disciplina.getRegistros().isEmpty()){
            System.out.println("A disciplina " + nomeDisciplina + " ainda não tem alunos cadastrados!");
            LimparTela.esperar(1500);
            return;
        }

        String gabarito;
        while(true){
            System.out.println("\nInforme a localização do arquivo com o gabarito oficial");
            System.out.println("(ou apenas ENTER para voltar):");
            String caminhoTexto = teclado.nextLine().trim();
            if(caminhoTexto.isEmpty()){
                return;
            }
            // o Windows ("Copiar como caminho") coloca o caminho entre aspas
            caminhoTexto = caminhoTexto.replaceAll("^\"|\"$", "");

            Path caminho;
            try{
                caminho = Path.of(caminhoTexto);
            }catch(InvalidPathException e){
                System.out.println("Caminho inválido!");
                LimparTela.esperar(1000);
                continue;
            }

            List<String> linhas = gerenciador.lerLinhas(caminho);
            if(linhas == null){
                System.out.println("Não foi possível ler o arquivo! Confira o caminho.");
                LimparTela.esperar(1000);
                continue;
            }

            // primeira linha não vazia; \uFEFF é o BOM que alguns editores do Windows gravam no início do arquivo
            String candidato = "";
            for(String linha : linhas){
                String limpa = TratamentoDeEntradas.tratarResposta(linha.replace("\uFEFF", ""));
                if(!limpa.isEmpty()){
                    candidato = limpa;
                    break;
                }
            }

            resultado = TratamentoDeEntradas.validarResposta(candidato);
            if(resultado == ResultadoTratamento.VALIDO){
                gabarito = candidato;
                break;
            }else if(resultado == ResultadoTratamento.CARACTERE_INVALIDO){
                System.out.println("O gabarito deve ter somente V/F!");
            }else{
                System.out.println("O gabarito deve ter 10 respostas!");
            }
            LimparTela.esperar(1000);
        }

        List<Nota> notas = corretor.corrigirDisciplina(disciplina, gabarito);
        String textoAlfabetico = corretor.gerarTextoAlfabetico(notas);
        String textoPorNota = corretor.gerarTextoPorNota(notas);

        estadoSalvamento = gerenciador.salvarResultados(nomeDisciplina, textoAlfabetico, textoPorNota);
        if(estadoSalvamento != ResultadoSalvamento.SALVO){
            System.out.println("Erro ao salvar os resultados!");
            System.out.println("Tente novamente mais tarde ou entre\nem contato com os desenvolvedores");
            LimparTela.esperar(1500);
            return;
        }
        System.out.println("\nResultados gerados em " + gerenciador.getPastaResultados() + ":");
        System.out.println(" - " + nomeDisciplina + GerenciarArquivos.SUFIXO_RESULTADO_ALFABETICO);
        System.out.println(" - " + nomeDisciplina + GerenciarArquivos.SUFIXO_RESULTADO_POR_NOTA);

        System.out.println("\nDeseja visualizar os resultados na tela? (S/N)");
        texto = TratamentoDeEntradas.tratarTexto(teclado.nextLine());
        if(texto.equals("S")){
            System.out.println("\n--- " + nomeDisciplina + ": ordem alfabética ---");
            System.out.print(textoAlfabetico);
            System.out.println("\n--- " + nomeDisciplina + ": por nota (decrescente) ---");
            System.out.print(textoPorNota);
        }
        System.out.println("\nPressione ENTER para continuar.");
        teclado.nextLine();
    }

    private void registrarGabaritoDisciplina(Disciplina disciplina){
        String gabarito;
        String resposta;
        ResultadoSalvamento estadoSalvamento;
        CorretorProva corretor = new CorretorProva();
        while(true){
            LimparTela.limparTerminal();
            System.out.println("\n==============================================");
            System.out.println("             Gabarito Oficial da Prova         ");
            System.out.println("==============================================\n");
            System.out.println("Digite o gabarito oficial da disciplina " + disciplina.getNome() + ":");
            System.out.println("[Somente com V/F]");
            resposta = TratamentoDeEntradas.tratarResposta(teclado.nextLine());
            
            if(corretor.marcouTudoIgual(resposta)){
                System.out.println("O gabarito não pode ter todas as alternativas iguais!");
                LimparTela.esperar(1000);
                continue;
            }
            
            resultado = TratamentoDeEntradas.validarResposta(resposta);
            if(resultado == ResultadoTratamento.VALIDO){
                gabarito = resposta;
                break;
            }else if(resultado == ResultadoTratamento.CARACTERE_INVALIDO){
                System.out.println("Digite somente com V/F");
            }else{
                System.out.println("Digite 10 respostas para o gabarito!");
            }
            LimparTela.esperar(1000);
        }
        String titulo = disciplina.getNome()+"_Gabarito";
        estadoSalvamento = gerenciador.criarArquivoGabarito(titulo, gabarito);

        if(estadoSalvamento != ResultadoSalvamento.SALVO){
            System.out.println("Erro ao salvar o gabarito!");
            System.out.println("Tente novamente mais tarde ou entre em contato\ncom os desenvolvedores");
            LimparTela.esperar(1000);
            return;
        }
        System.out.printf("Arquivo %s criado com sucesso!\n",titulo);
        System.out.println("Caminho do arquivo:");
        Path caminho = gerenciador.transformarCaminhoGabarito(titulo);
        System.out.printf("\n%s\n",caminho.toString());
        LimparTela.esperar(2000);
    }

    private void gerarBoletins(){
        String texto;
        LimparTela.limparTerminal();
        System.out.println("\n==============================================");
        System.out.println("              Geração de Boletins             ");
        System.out.println("==============================================\n");

        ResultadoGeracao estadoGeracao = gerador.gerar(gerenciador.getPastaResultados());

        if(estadoGeracao == ResultadoGeracao.DIRETORIO_INEXISTENTE){
            System.out.println("A pasta de resultados não existe!");
            System.out.println("Gere o resultado de ao menos uma disciplina primeiro.");
        }else if(estadoGeracao == ResultadoGeracao.SEM_RESULTADOS){
            System.out.println("Nenhum resultado de disciplina encontrado!");
            System.out.println("Gere o resultado de ao menos uma disciplina primeiro.");
        }else if(estadoGeracao == ResultadoGeracao.ERRO_LEITURA){
            System.out.println("Erro ao ler o arquivo " + gerador.getDetalheErro() + "!");
        }else if(estadoGeracao == ResultadoGeracao.FORMATO_INVALIDO){
            System.out.println("Formato inválido em " + gerador.getDetalheErro());
            System.out.println("Cada linha deve ser: NOME<TAB>NOTA");
        }else if(estadoGeracao == ResultadoGeracao.ERRO_LIMPEZA){
            System.out.println("Não foi possível apagar os boletins antigos!");
            System.out.println("Feche os arquivos de " + gerenciador.getPastaBoletins() + " que estejam abertos e tente novamente.");
        }else if(estadoGeracao == ResultadoGeracao.ERRO_ESCRITA){
            System.out.println(gerador.getBoletinsGerados().size()+" boletim(ns) gerado(s).");
            System.out.println("Não foi possível salvar o boletim de :"+gerador.getDetalheErro());
        }else if(estadoGeracao == ResultadoGeracao.GERADO){
            List<Boletim> boletins = gerador.getBoletinsGerados();
            System.out.println(boletins.size() + " boletim(ns) gerado(s) em " + gerenciador.getPastaBoletins());

            System.out.println("\nDeseja visualizar os boletins na tela? (S/N)");
            texto = TratamentoDeEntradas.tratarTexto(teclado.nextLine());
            if(texto.equals("S")){
                for(Boletim boletim : boletins){
                    System.out.println();
                    System.out.print(boletim.gerarTexto());
                }
            }
        }

        System.out.println("\nPressione ENTER para continuar.");
        teclado.nextLine();
    }
    public void menu(){
        while(true){
            LimparTela.limparTerminal();
            System.out.println("\n=============================================");
            System.out.println("            Gerenciamento de Arquivos          ");
            System.out.println("=============================================\n");
            System.out.println("1 - Cadastrar disciplina");
            System.out.println("2 - Gerar resultado de uma disciplina");
            System.out.println("3 - Gerar boletins");
            System.out.println("0 - Sair");
            System.out.println("\nDigite uma opção:");
            int opcao = TratamentoDeEntradas.tratarNumero(teclado.nextLine().trim());
            switch (opcao) {
                case 1:
                    cadastrarDisciplina();
                    break;
                case 2:
                    gerarResultadoDisciplina();
                    break;
                case 3:
                    gerarBoletins();
                    break;
                case 0:
                    System.out.println("\nEncerrando o sistema");
                    return;
                default:
                    System.out.println("Opção Inválida!");
                    LimparTela.esperar(1000);
                    break;
            }
        }
    }
}