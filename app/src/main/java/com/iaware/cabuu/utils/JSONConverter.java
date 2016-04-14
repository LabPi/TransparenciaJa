package com.iaware.cabuu.utils;


import com.iaware.cabuu.entidades.Comentario;
import com.iaware.cabuu.entidades.MinhaParticipacao;
import com.iaware.cabuu.entidades.Noticia;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PeDeNRiQue on 25/11/2015.
 */
public class JSONConverter {

    /*public static Usuario JSONObjectToUsuario(JSONObject json){
        Usuario usuario = new Usuario();
        try {
            usuario.setNomeUser(json.getString("nome"));
            usuario.setTelefoneUser(json.getString("telefone"));
            usuario.setEmailUser(json.getString("email"));
            usuario.setCpfUser(json.getString("cpf"));
            usuario.setCidadeUser(json.getString("cidade"));
            usuario.setImagemUser(json.getInt("idImage"));
            usuario.setTokenUser(json.getString("authToken"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return usuario;
    }*/

    public static Noticia JSONObjectToNoticia(JSONObject json){
        Noticia noticia = new Noticia();
        try {
            noticia.setIdRemoto(json.getInt("id"));
            noticia.setConteudo(json.getString("conteudo"));

            if(json.getString("titulo") != null) {
                noticia.setTitulo(json.getString("titulo"));
            }
            noticia.setCategoria(json.getString("programa"));
            noticia.setIdImagemPost(json.getInt("idImagePost"));
            if(!json.isNull("idImgPrograma")) {
                noticia.setIdImagemCategoria(json.getInt("idImgPrograma"));
            }
            noticia.setTotalCurtidas(json.getInt("totalCurtidas"));
            noticia.setTotalComentarios(json.getInt("totalComentarios"));
            noticia.setTipoCurtida(json.getInt("tipoCurtida"));
            noticia.setData(json.getString("data"));
            noticia.setTipo(json.getString("tipoFilePost"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return noticia;
    }

    public static Comentario JSONObjectToComentario(JSONObject json){
        Comentario comentario = new Comentario();

        try {
            comentario.setNome(json.getString("nomeUsuario"));
            comentario.setComentario(json.getString("descricao"));
            comentario.setIdImagem(json.getInt("idImage"));
            comentario.setData(json.getString("data"));
        }catch (JSONException e){
            e.printStackTrace();
        }

        return comentario;
    }

    public static MinhaParticipacao JSONObjectToMinhaParticipacao(JSONObject json) {
        MinhaParticipacao mp = new MinhaParticipacao();

        try{
            mp.setIdRemoto(json.getInt("id"));
            mp.setTitulo(json.getString("titulo"));
            mp.setData(json.getString("data"));
            mp.setStatus(json.getString("status"));
        }catch (JSONException e){
            e.printStackTrace();
        }

        return mp;
    }
}
