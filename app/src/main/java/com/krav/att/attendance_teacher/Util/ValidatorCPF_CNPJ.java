package com.krav.att.attendance_teacher.Util;

public class ValidatorCPF_CNPJ {

    private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private static ValidatorCPF_CNPJ instance = null;
    private ValidatorCPF_CNPJ() {}

    public static ValidatorCPF_CNPJ getInstance() {
        if (instance==null) {
            instance = new ValidatorCPF_CNPJ();
        }
        return instance;
    }

    /**
     * Return a randomly valid CPF
     * @return CPF with no mask
     */
    public String createCPF() {
        String iniciais = "";
        Integer numero;
        for (int i = 0; i < 9; i++) {
            numero = new Integer((int) (Math.random() * 10));
            iniciais += numero.toString();
        }
        Integer digito1 = calcularDigito(iniciais.substring(0,9), pesoCPF);
        Integer digito2 = calcularDigito(iniciais.substring(0,9) + digito1, pesoCPF);

        return iniciais + digito1 + digito2;
    }

    /**
     * Return a randomly valid CNPJ
     * @return CNPJ with no mask
     */
    public String createCNPJ() {
        String iniciais = "";
        Integer numero;
        for (int i = 0; i < 12; i++) {
            numero = new Integer((int) (Math.random() * 10));
            iniciais += numero.toString();
        }
        Integer digito1 = calcularDigito(iniciais.substring(0,12), pesoCNPJ);
        Integer digito2 = calcularDigito(iniciais.substring(0,12) + digito1, pesoCNPJ);

        return iniciais + digito1 + digito2;
    }

    public String maskCPF(String cpf) {
        if (isCPF(cpf)) {
            return cpf.substring(0,3)+"."+cpf.substring(3,6)+"."+cpf.substring(6,9)+"-"+cpf.substring(9,11);
        } else {
            return cpf;
        }
    }

    public String maskCNPJ(String cnpj) {
        if (isCNPJ(cnpj)) {
            return  cnpj.substring(0,2)+"."+cnpj.substring(2,5)+"."+
                    cnpj.substring(5,8)+"/"+cnpj.substring(8,12)+"-"+
                    cnpj.substring(12);
        } else {
            return cnpj;
        }
    }

    public String unmask(String masked) {
        if (masked!=null) {
            return masked.replaceAll("[.]", "").replaceAll("[-]","").replaceAll("[/]", "");
        }
        return null;
    }

    private int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
            digito = Integer.parseInt(str.substring(indice,indice+1));
            soma += digito*peso[peso.length-str.length()+indice];
        }
        soma = 11 - soma % 11;
        return soma > 9 ? 0 : soma;
    }

    public boolean isCPF(String cpf) {
        if (cpf==null) return false;
        cpf = unmask(cpf);
        if (cpf.length()!=11) return false;

        Integer digito1 = calcularDigito(cpf.substring(0,9), pesoCPF);
        Integer digito2 = calcularDigito(cpf.substring(0,9) + digito1, pesoCPF);
        return cpf.equals(cpf.substring(0,9) + digito1.toString() + digito2.toString());
    }

    public boolean isCNPJ(String cnpj) {
        if (cnpj==null) return false;
        cnpj = unmask(cnpj);
        if (cnpj.length()!=14) return false;

        Integer digito1 = calcularDigito(cnpj.substring(0,12), pesoCNPJ);
        Integer digito2 = calcularDigito(cnpj.substring(0,12) + digito1, pesoCNPJ);
        return cnpj.equals(cnpj.substring(0,12) + digito1.toString() + digito2.toString());
    }

}