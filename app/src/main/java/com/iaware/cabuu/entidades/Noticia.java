package com.iaware.cabuu.entidades;

import java.io.Serializable;

/**
 * Created by PeDeNRiQue on 09/12/2015.
 */
public class Noticia implements Serializable{
    private Integer idRemoto;
    private Integer idImagemPost;
    private Integer idImagemCategoria;
    private Integer totalCurtidas;
    private Integer totalComentarios;
    private int tipoCurtida;
    private String titulo;
    private String conteudo;
    private String categoria;
    private String data;
    private String tipo;

    public Integer getIdRemoto() {
        return idRemoto;
    }

    public void setIdRemoto(Integer idRemoto) {
        this.idRemoto = idRemoto;
    }

    public Integer getIdImagemPost() {
        return idImagemPost;
    }

    public void setIdImagemPost(Integer idImagemPost) {
        this.idImagemPost = idImagemPost;
    }

    public Integer getIdImagemCategoria() {
        return idImagemCategoria;
    }

    public void setIdImagemCategoria(Integer idImagemCategoria) {
        this.idImagemCategoria = idImagemCategoria;
    }

    public Integer getTotalCurtidas() {
        return totalCurtidas;
    }

    public void setTotalCurtidas(Integer totalCurtidas) {
        this.totalCurtidas = totalCurtidas;
    }

    public Integer getTotalComentarios() {
        return totalComentarios;
    }

    public void setTotalComentarios(Integer totalComentarios) {
        this.totalComentarios = totalComentarios;
    }

    public int getTipoCurtida() {
        return tipoCurtida;
    }

    public void setTipoCurtida(int tipoCurtida) {
        this.tipoCurtida = tipoCurtida;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
