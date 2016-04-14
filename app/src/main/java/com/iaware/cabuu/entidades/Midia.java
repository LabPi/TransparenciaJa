package com.iaware.cabuu.entidades;

/**
 * Created by henrique on 13/02/16.
 */
public class Midia {
    String conteudo;
    String tipo;
    int controle;

    public Midia() {

    }



    public Midia(String conteudo, String tipo, int controle) {
        this.conteudo = conteudo;
        this.tipo = tipo;
        this.controle = controle;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getControle() {
        return controle;
    }

    public void setControle(int controle) {
        this.controle = controle;
    }
}
