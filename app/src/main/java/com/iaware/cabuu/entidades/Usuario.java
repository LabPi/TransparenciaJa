package com.iaware.cabuu.entidades;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * Created by PeDeNRiQue on 09/12/2015.
 */
@Table(name = "Usuario")
public class Usuario extends Model {

    @Column(name = "id_remoto")
    private Integer idRemoto;
    @Column(name = "nome")
    private String nome;
    @Column(name = "email")
    private String email;
    @Column(name = "senha")
    private String senha;
    @Column(name = "telefone")
    private String telefone;
    @Column(name = "id_imagem")
    private Integer idImagem;
    @Column(name = "token")
    private String token;
    @Column(name = "discriminador")
    private String discriminador;

    public Usuario() {
        super();
    }

    public static Usuario getCurrent() {
        return new Select().from(Usuario.class).executeSingle();
    }

    public static boolean hasLogedUser() {
        boolean a = new Select()
                .from(Usuario.class)
                .executeSingle() != null;
        return a;

    }

    public static void deleteCurrent() {
        Usuario u = (Usuario) new Delete().from(Usuario.class).execute();
        u = null;
    }


    public Integer getIdRemoto() {
        return idRemoto;
    }

    public void setIdRemoto(Integer idRemoto) {
        this.idRemoto = idRemoto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Integer getIdImagem() {
        return idImagem;
    }

    public void setIdImagem(Integer idImagem) {
        this.idImagem = idImagem;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDiscriminador() {
        return discriminador;
    }

    public void setDiscriminador(String discriminador) {
        this.discriminador = discriminador;
    }
}
