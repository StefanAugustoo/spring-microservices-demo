package br.pucrs.microdemo.representantes.util;

public final class CpfUtil {

    private CpfUtil() {}

    public static String normalizar(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("\\D", "");
    }
}
