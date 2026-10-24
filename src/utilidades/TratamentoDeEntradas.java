package utilidades;

import enums.ResultadoTratamento;

public class TratamentoDeEntradas {
    public static ResultadoTratamento validarResposta(String respostas) {
        if(respostas.length() != 10){
            return ResultadoTratamento.INCOMPLETO;
        }

        for (int i = 0; i < respostas.length(); i++) {
            char resposta = respostas.charAt(i);

            if (resposta != 'V' && resposta != 'F') {
                return ResultadoTratamento.CARACTERE_INVALIDO;
            }
            
        }

        return ResultadoTratamento.VALIDO;
    }

    public static String tratarResposta(String resposta) {
        return resposta.trim().toUpperCase().replaceAll("\\s+", "");
    }

    public static String tratarNome(String nome) {
        return nome.trim().replaceAll("\\s+", " ");
    }

    public static String tratarNomeArquivo(String nomeArquivo) {
        return nomeArquivo.trim().toUpperCase().replaceAll("[^a-zA-Z]","");
    }

    public static String tratarTexto(String texto){
        return texto.trim().toUpperCase();
    }
}
