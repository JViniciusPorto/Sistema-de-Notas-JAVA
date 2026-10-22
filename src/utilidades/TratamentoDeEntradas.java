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

    public static String tratarTexto(String texto){
        return texto.trim().toUpperCase().replaceAll("\\s+", "");
    }
}
