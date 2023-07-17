package com.project.bucketmanager.Models.enums;

public enum EContentDisposition {
    INLINE("inline"),
    ATTACHMENT("attachment");
    private final String value;
    EContentDisposition(String contentDisposition){
        this.value = contentDisposition;
    }

    /**
     * Retorna a instância do enum {@code EContentDisposition} correspondente ao valor fornecido.
     *
     * @param value O valor para o qual se deseja obter o enum {@code EContentDisposition}.
     * @return A instância do enum {@code EContentDisposition} correspondente ao valor fornecido.
     *         Se o valor for nulo, retorna {@code EContentDisposition.ATTACHMENT} como padrão.
     * @throws IllegalArgumentException se o valor fornecido não corresponder a nenhum dos enums válidos.
     */
    public static EContentDisposition getByValue(String value){
        if (value == null || value.isEmpty()) {
            return EContentDisposition.ATTACHMENT;
        }
        for(EContentDisposition disposition:EContentDisposition.values()){
            if(disposition.value.equals(value)){
                return disposition;
            }
        }
        throw new IllegalArgumentException("Invalid content disposition type: "+value);
    }

    public String getValue() {
        return value;
    }
}
