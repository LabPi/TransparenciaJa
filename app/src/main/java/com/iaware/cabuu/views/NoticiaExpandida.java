package com.iaware.cabuu.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.iaware.cabuu.entidades.Noticia;
import com.iaware.cabuu.utils.ConversorInputStreamToString;
import com.iaware.cabuu.R;
import com.iaware.cabuu.entidades.Usuario;
import com.iaware.cabuu.utils.Connection;
import com.iaware.cabuu.utils.Links;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class NoticiaExpandida extends AppCompatActivity {

    SimpleDraweeView imagem;
    SimpleDraweeView ivCategoria;
    VideoView vvNoticia;
    TextView titulo;
    TextView descricao;
    TextView tvData;
    TextView tvCategoria;

    Noticia noticia = null;

    public Button btn_comentar;
    public Button btn_curitr;

    public TextView tvTotalComentarios;
    public TextView tvNumeroCurtidas;

    private ProgressDialog progressDialog;
    private MediaController mediaControls;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia_expandida);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        //opcao = (String) intent.getStringExtra("opcao");

        imagem = (SimpleDraweeView) findViewById(R.id.iv_noticia);
        ivCategoria = (SimpleDraweeView) findViewById(R.id.iv_categoria);
        titulo = (TextView) findViewById(R.id.titulo);
        tvCategoria = (TextView) findViewById(R.id.tv_categoria);
        descricao = (TextView) findViewById(R.id.descricao_post);
        tvData = (TextView) findViewById(R.id.tv_data);
        vvNoticia = (VideoView) findViewById(R.id.vv_noticia);


        btn_comentar = (Button) findViewById(R.id.img_comentario);
        btn_curitr = (Button) findViewById(R.id.img_curtir);

        tvTotalComentarios = (TextView) findViewById(R.id.numero_comentarios);
        tvNumeroCurtidas = (TextView) findViewById(R.id.numero_curtidas);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            noticia = (Noticia) b.getSerializable("noticia");
        }

        /* INICIO VIDEO */

        /*if (noticia.getIdImagemPost() == 43){

            vvNoticia.setVideoPath(
                    "http://www.ebookfrenzy.com/android_book/movie.mp4");

            vvNoticia.start();
        }*/
        /*if(!noticia.getTipo().equals("imagem")) {

            imagem.setVisibility(View.GONE);
            if (mediaControls == null) {
                mediaControls = new MediaController(NoticiaExpandida.this);
            }


            try {
                vvNoticia.setMediaController(mediaControls);
                vvNoticia.setVideoURI(Uri.parse(Links.URL_IMAGE + noticia.getIdImagemPost()));
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            vvNoticia.requestFocus();
            vvNoticia.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    vvNoticia.seekTo(position);
                    if (position == 0) {
                        vvNoticia.start();
                    } else {
                        vvNoticia.pause();

                    }
                }

            });
        }else{
            vvNoticia.setVisibility(View.GONE);
        }*/
        /* FIM VIDEO */

        if(noticia.getTipoCurtida() == 1){
            btn_curitr.setBackgroundResource(R.mipmap.icone_curtir_azul);
        }else{
            btn_curitr.setBackgroundResource(R.mipmap.icone_curtir_branco);
        }

        if(noticia.getTipo().contains("imagem")){
            vvNoticia.setVisibility(View.GONE);
            String link = Links.URL_IMAGE+noticia.getIdImagemPost();
            System.out.println(link);
            Uri uri = Uri.parse(link);
            imagem.setImageURI(uri);
        }else{
            imagem.setVisibility(View.GONE);
            Uri uri = Uri.parse(Links.URL_IMAGE+noticia.getIdImagemPost());
            vvNoticia.start();
            MediaController mc = new MediaController(this);
            mc.setAnchorView(vvNoticia);
            mc.setMediaPlayer(vvNoticia);
            vvNoticia.setMediaController(mc);
            vvNoticia.setVideoURI(uri);
            vvNoticia.start();
        }

        String linkImagemCategoria = Links.URL_IMAGE+noticia.getIdImagemCategoria();
        Uri uriImageCategoria = Uri.parse(linkImagemCategoria);


        ivCategoria.setImageURI(uriImageCategoria);
        descricao.setText(noticia.getConteudo());
        titulo.setText(noticia.getTitulo());
        tvCategoria.setText(noticia.getCategoria());

        btn_comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoticiaExpandida.this, ComentarioActivity.class);
                intent.putExtra("postID", noticia.getIdRemoto());
                startActivity(intent);
            }
        });


        btn_curitr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nCurtida = noticia.getTipoCurtida();

                if (!Connection.existeConexao(NoticiaExpandida.this)) {
                    return;
                }
                if (noticia.getTipoCurtida() == 1) {

                    nCurtida = nCurtida - 1;
                    noticia.setTotalCurtidas(nCurtida);
                    noticia.setTipoCurtida(0);
                    btn_curitr.setBackgroundResource(R.mipmap.icone_curtir_branco);

                } else {
                    nCurtida = nCurtida + 1;
                    noticia.setTotalCurtidas(nCurtida);
                    noticia.setTipoCurtida(1);
                    btn_curitr.setBackgroundResource(R.mipmap.icone_curtir_azul);
                }

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("idPost", noticia.getIdRemoto());
                    jsonObject.put("tipo", noticia.getTipoCurtida());

                    new LikePostAsyncTask(btn_curitr, noticia.getTipoCurtida()).execute(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tvNumeroCurtidas.setText("( " + noticia.getTotalCurtidas() + " )");
                Log.i("NUMERO CURTIDA", "" + noticia.getTotalCurtidas());
            }
        });

        tvTotalComentarios.setText("( "+noticia.getTotalComentarios()+" )");
        tvNumeroCurtidas.setText("( " + noticia.getTotalCurtidas() + " )");

    }


    public class LikePostAsyncTask extends AsyncTask<String,Void,String> {

        TextView mText;
        int mTipo;

        public LikePostAsyncTask(TextView text,int tipo ){
            mText = text;
            mTipo = tipo;
        }
        @Override
        protected String doInBackground(String... params) {
            String response = "";

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("idPost", params[0]);
                jsonObject.put("tipo", params[1]);

                Log.e("JSON Enviar like:", jsonObject.toString());
                URL url = new URL(Links.URL_CURTIR_POST+ Usuario.getCurrent().getToken());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(30 * 1000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(jsonObject.toString());
                out.close();

                System.out.println("Response Code: " + conn.getResponseCode());
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = ConversorInputStreamToString.convertInputStreamToString(in);
                System.out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalStateException e){
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("1")) {
                mTipo = 1;
                /*mText.setText("Descurtir");
                mText.setTypeface(null, Typeface.BOLD);*/
            } else if (s.equals("0")) {
                mTipo = 0;
                /*mText.setText("Curtir");
                mText.setTypeface(null, Typeface.NORMAL);*/
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
