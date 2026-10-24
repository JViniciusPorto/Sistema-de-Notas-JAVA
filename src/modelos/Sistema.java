// use chcp 65001 caso dê erro de formatação no terminal
package modelos;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import enums.ResultadoCriacao;
import enums.ResultadoExclusao;
import enums.ResultadoSalvamento;
import enums.ResultadoTratamento;
import gerenciar_arquivos.GerenciarArquivos;
import utilidades.LimparTela;
import utilidades.TratamentoDeEntradas;

public class Sistema {
    private GerenciarArquivos gerenciador;
    private Scanner teclado;
    private final String respostaSaida = "SALVAR";
    private String nomeArquivo;
    private String texto;
    private String resposta;
    private String nomeAluno;
    private ResultadoTratamento resultado;
    private ResultadoCriacao estadoCriacao;
    private ResultadoSalvamento estadoSalvamento;
    private ResultadoExclusao estadoExclusao;

    public Sistema(){
        gerenciador = new GerenciarArquivos();
        teclado = new Scanner(System.in,StandardCharsets.UTF_8);
    }

    public void cadastrarDisciplina(){
        Disciplina disciplina;

        while(true){
            LimparTela.limparTerminal();
            System.out.println("\n==============================================");
            System.out.println("                Criação do Arquivo              ");
            System.out.println("==============================================\n");
            System.out.println("Digite o nome do arquivo: ");
            nomeArquivo = teclado.nextLine();
            if(!nomeArquivo.isEmpty()){
                nomeArquivo = TratamentoDeEntradas.tratarNomeArquivo(nomeArquivo);
                disciplina = new Disciplina(nomeArquivo);
            }else{
                System.out.println("Digite um nome válido!");
                LimparTela.esperar(1000);
                continue;
            }
            System.out.printf("O título do arquivo será %s \n",nomeArquivo);
            System.out.println("Confirma o título? (S/N)");
            texto = teclado.nextLine();
            texto = TratamentoDeEntradas.tratarTexto(texto);
            if(texto.equals("S")){
                estadoCriacao = gerenciador.criarArquivoDisciplina(disciplina);
                if(estadoCriacao == ResultadoCriacao.CRIADO){
                    System.out.println("Arquivo criado com sucesso!");
                    LimparTela.esperar(1000);
                    break;
                }else if(estadoCriacao == ResultadoCriacao.JA_EXISTE){
                    System.out.println("Já existe um arquivo com esse nome!");
                    System.out.println("Escreva outro nome!");
                    LimparTela.esperar(1000);
                    continue;
                }else if(estadoCriacao == ResultadoCriacao.ERRO){
                    System.out.println("Erro ao tentar criar o arquivo!");
                    System.out.println("Por favor, tente novamente!");
                    LimparTela.esperar(1000);
                    continue;
                }
            }else if(texto.equals("N")){
                continue;
            }else{
                System.out.println("Erro ao confirmar título!");
                LimparTela.esperar(1000);
                continue;
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
        }else if(estadoSalvamento == ResultadoSalvamento.ARQUIVO_INACESSIVEL){
            System.out.println("Erro de acessibilidade!");
            System.out.println("Tente novamente mais tarde ou entre\nem contato com os desenvolvedores");
            estadoExclusao = gerenciador.excluirDisciplina(disciplina.getNome());
            if(estadoExclusao == ResultadoExclusao.EXCLUIDO){
                System.out.println("Para fins de segurança, o arquivo\nfoi excluído!");
            }else if(estadoExclusao == ResultadoExclusao.NAO_EXISTE){
                System.out.printf("Erro ao excluir o arquivo %s.txt!\n", disciplina.getNome());
                System.out.println("Esse arquivo não existe!");
            }else if(estadoExclusao == ResultadoExclusao.ERRO){
                System.out.printf("Erro ao excluir o arquivo %s.txt!\n",disciplina.getNome());
                System.out.println("Entre em contato com os desenvolvedores!");
            }
        }else if(estadoSalvamento == ResultadoSalvamento.ERRO_ARQUIVO){
            System.out.println("Erro ao salvar registros!");
            System.out.println("Tente novamente mais tarde ou entre\nem contato com os desenvolvedores");
            estadoExclusao = gerenciador.excluirDisciplina(disciplina.getNome());
            if(estadoExclusao == ResultadoExclusao.EXCLUIDO){
                System.out.println("Para fins de segurança, o arquivo\nfoi excluído!");
            }else if(estadoExclusao == ResultadoExclusao.NAO_EXISTE){
                System.out.printf("Erro ao excluir o arquivo %s.txt!\n", disciplina.getNome());
                System.out.println("Esse arquivo não existe!");
            }else if(estadoExclusao == ResultadoExclusao.ERRO){
                System.out.printf("Erro ao excluir o arquivo %s.txt!\n",disciplina.getNome());
                System.out.println("Entre em contato com os desenvolvedores!");
            }
        }

    }
}
