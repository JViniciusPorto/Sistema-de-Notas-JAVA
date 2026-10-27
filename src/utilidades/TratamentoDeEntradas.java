package utilidades;

import enums.ResultadoTratamento;
import java.text.Normalizer;

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
        String semAcento = Normalizer.normalize(nomeArquivo.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");          
        // remove os acentos (á→a, ã→a, ç→c...)
        return semAcento.toUpperCase().replaceAll("[^A-Z0-9]", "").replaceAll("\\s+"," ").trim();
    }

    public static String tratarTexto(String texto){
        return texto.trim().toUpperCase();
    }
    public static int tratarNumero(String texto){
        int numero;
        try{
            numero = Integer.parseInt(texto);
        }catch(NumberFormatException e){
            numero = -1;
        }
        return numero;
    }
}
