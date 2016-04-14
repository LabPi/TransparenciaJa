package com.iaware.cabuu.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iaware.cabuu.entidades.Noticia;
import com.iaware.cabuu.entidades.Usuario;
import com.iaware.cabuu.utils.Connection;
import com.iaware.cabuu.utils.ConversorInputStreamToString;
import com.iaware.cabuu.utils.Links;
import com.iaware.cabuu.views.NoticiaExpandida;
import com.iaware.cabuu.R;
import com.iaware.cabuu.views.ComentarioActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.MyViewHolder> {

    private List<Noticia> mList;
    private LayoutInflater mLayoutInflater;
    private Context context;

    public NoticiaAdapter(Context context,List<Noticia> l){
        this.context = context;
        mList = l;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addListItem(Noticia n, int position){
        mList.add(n);
        notifyItemInserted(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_noticia,viewGroup,false);

        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, int position) {
        String link = Links.URL_IMAGE+mList.get(position).getIdImagemPost();
        String linkImagemCategoria = Links.URL_IMAGE+mList.get(position).getIdImagemCategoria();
        Uri uri = Uri.parse(link);
        Uri uriCategoria = Uri.parse(linkImagemCategoria);
        myViewHolder.ivNoticia.setImageURI(uri);
        myViewHolder.ivCategoria.setImageURI(uriCategoria);
        /*if(mList.get(position).getConteudo().length() > 90){
            myViewHolder.tvDescricao.setText(mList.get(position).getConteudo().substring(0,87)+"...");
        }else {
            myViewHolder.tvDescricao.setText(mList.get(position).getConteudo());
        }*/
        myViewHolder.tv_categoria.setText(mList.get(position).getCategoria());
        myViewHolder.tvData.setText(mList.get(position).getData());
        myViewHolder.ivTitulo.setText(mList.get(position).getTitulo());

        final Noticia noticia = mList.get(position);

        if(noticia.getTipoCurtida() == 1){
            myViewHolder.btn_curitr.setBackgroundResource(R.mipmap.icone_curtir_azul);
        }else{
            myViewHolder.btn_curitr.setBackgroundResource(R.mipmap.icone_curtir_branco);
        }

        myViewHolder.ivNoticia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, NoticiaExpandida.class);
                Bundle b = new Bundle();
                b.putSerializable("noticia", noticia);
                i.putExtras(b);

                context.startActivity(i);
                /*Intent i = new Intent(context, ShowVideoActivity.class);
                context.startActivity(i);*/
            }
        });

        myViewHolder.btn_comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComentarioActivity.class);
                intent.putExtra("postID", noticia.getIdRemoto());
                context.startActivity(intent);
            }
        });


        myViewHolder.btn_curitr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nCurtida = noticia.getTipoCurtida();

                if (!Connection.existeConexao((AppCompatActivity) context)) {
                    return;
                }
                if (noticia.getTipoCurtida() == 1) {

                    nCurtida = nCurtida - 1;
                    noticia.setTotalCurtidas(nCurtida);
                    noticia.setTipoCurtida(0);
                    myViewHolder.btn_curitr.setBackgroundResource(R.mipmap.icone_curtir_branco);

                } else {
                    nCurtida = nCurtida + 1;
                    noticia.setTotalCurtidas(nCurtida);
                    noticia.setTipoCurtida(1);
                    myViewHolder.btn_curitr.setBackgroundResource(R.mipmap.icone_curtir_azul);
                }

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("idPost", noticia.getIdRemoto());
                    jsonObject.put("tipo", noticia.getTipoCurtida());

                    new LikePostAsyncTask(myViewHolder.btn_curitr, noticia.getTipoCurtida()).execute(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(noticia.getTotalCurtidas() == 0){
                    myViewHolder.tvNumeroCurtidas.setText("");
                }else{
                    myViewHolder.tvNumeroCurtidas.setText("" + noticia.getTotalCurtidas());
                }
            }
        });

        myViewHolder.tvTotalComentarios.setText("" + noticia.getTotalComentarios());
        myViewHolder.tvNumeroCurtidas.setText("" + noticia.getTotalCurtidas());

        if(noticia.getTotalComentarios() == 0){
            myViewHolder.tvTotalComentarios.setText("");
        }else{
            myViewHolder.tvTotalComentarios.setText("" + noticia.getTotalComentarios());
        }

        if(noticia.getTotalCurtidas() == 0){
            myViewHolder.tvNumeroCurtidas.setText("");
        }else{
            myViewHolder.tvNumeroCurtidas.setText("" + noticia.getTotalCurtidas());
        }

        /*
        myViewHolder.rl_acaoCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareOnFacebookUtils shareOnFacebookUtils = new ShareOnFacebookUtils(context);
                shareOnFacebookUtils.share(noticia.getTitulo(), noticia.getConteudo());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public SimpleDraweeView ivNoticia;
        public SimpleDraweeView ivCategoria;
        public TextView ivTitulo;

        public TextView tvData;
        public TextView tv_categoria;

        public Button btn_comentar;
        public Button btn_curitr;

        public TextView tvTotalComentarios;
        public TextView tvNumeroCurtidas;

        /*public TextView tvCurtir;

        public RelativeLayout rl_acaoCompartilhar;
*/
        public MyViewHolder(View itemView) {
            super(itemView);

            ivNoticia = (SimpleDraweeView) itemView.findViewById(R.id.iv_noticia);
            ivCategoria = (SimpleDraweeView) itemView.findViewById(R.id.iv_categoria);
            //tvDescricao = (TextView) itemView.findViewById(R.id.tv_descricao);
            tv_categoria = (TextView) itemView.findViewById(R.id.tv_categoria);
            tvData = (TextView) itemView.findViewById(R.id.tv_data);

            btn_comentar = (Button) itemView.findViewById(R.id.img_comentario);
            btn_curitr = (Button) itemView.findViewById(R.id.img_curtir);

            ivTitulo = (TextView) itemView.findViewById(R.id.ivTitulo);
            tvTotalComentarios = (TextView) itemView.findViewById(R.id.numero_comentarios);
            tvNumeroCurtidas = (TextView) itemView.findViewById(R.id.numero_curtidas);
            /*
            tvCurtir = (TextView) itemView.findViewById(R.id.txt_curtir);

            rl_acaoCompartilhar = (RelativeLayout) itemView.findViewById(R.id.acao_compartilhar);*/
        }
    }

    public class LikePostAsyncTask extends AsyncTask<String,Void,String> {

        TextView mText;
        int mTipo;

        public LikePostAsyncTask(TextView text, int tipo) {
            mText = text;
            mTipo = tipo;
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";

            String json = params[0];
            try {

                Log.e("JSON Enviar like:", json);
                URL url = new URL(Links.URL_CURTIR_POST + Usuario.getCurrent().getToken());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(30 * 1000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(json);
                out.close();

                System.out.println("Response Code: " + conn.getResponseCode());
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = ConversorInputStreamToString.convertInputStreamToString(in);
                System.out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
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
}
