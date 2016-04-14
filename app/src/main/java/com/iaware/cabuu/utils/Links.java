package com.iaware.cabuu.utils;

/**
 * Created by PeDeNRiQue on 14/11/2015.
 */
public class Links {



    public static String URL_PRINCIPAL = "https://cabuu.herokuapp.com/";
    public static String URL_LOGIN = URL_PRINCIPAL+"loga_ws?permission_cabuu=cabuu";
    public static String URL_IMAGE = URL_PRINCIPAL+"image_video/";
    public static String URL_LISTA_POSTS = URL_PRINCIPAL+"posts_ws";
    public static String URL_LISTA_COMENTARIOS = URL_PRINCIPAL+"comentario/lista_ws?token=";
    public static String URL_LISTA_PROGRAMAS = URL_PRINCIPAL+"programa/lista_ws?token=";
    public static String URL_LISTA_MINHAS_PARTICIPACOES = URL_PRINCIPAL+"posts/usuario_ws?token=";
    public static String URL_CRIAR_CONTA = URL_PRINCIPAL+"usuario/novo_ws?permission_cabuu=cabuu";
    public static String URL_ENVIAR_SUGESTAO = URL_PRINCIPAL+"postIn/novo?token=";
    public static String URL_ENVIA_COMENTARIO = URL_PRINCIPAL+"comentario/envia_ws?token=";
    public static String URL_CURTIR_POST = URL_PRINCIPAL+"postIn/curtir_ws?token=";
    public static String URL_EDITAR_PERFIL = URL_PRINCIPAL+"usuario/edita_ws?token=";

    /*public static String linkPrincipal = "https://deolhonadengue.herokuapp.com";
    public static String logaWS = "/loga_ws";
    public static String novoUsuarioWS = "/usuario/novo_ws?permission_focodengue=focodenguetrue";
    public static String manifestacaoWS = "/manifestacao/envia_ws";
    public static String postWS = "/posts_ws";
    public static String enviarSugestaoWS = "/sugestao/envia_ws";
    public static String imageWS = "/image_video/";
    public static String focoWS = "/foco_dengue/";*/
}
