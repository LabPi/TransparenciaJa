package com.iaware.cabuu.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iaware.cabuu.adapters.ComentarioAdapter;
import com.iaware.cabuu.entidades.Comentario;
import com.iaware.cabuu.entidades.Usuario;
import com.iaware.cabuu.utils.Connection;
import com.iaware.cabuu.utils.ConversorInputStreamToString;
import com.iaware.cabuu.utils.CustomDialog;
import com.iaware.cabuu.utils.GlobalDialog;
import com.iaware.cabuu.utils.JSONConverter;
import com.iaware.cabuu.utils.Links;
import com.iaware.cabuu.R;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ComentarioActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<Comentario> mList;
    private ComentarioAdapter adapter;
    LinearLayoutManager lln;
    private boolean isLoading;
    private int contador = 0;
    private boolean stop;
    Integer postID;

    Button imgSendComment;
    EditText edtTextToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        postID = b.getInt("postID");

        //mList = new ArrayList<Noticia>();
        isLoading = true;
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (mRecyclerView == null || mRecyclerView.getChildCount() == 0) ?
                                0 : mRecyclerView.getChildAt(0).getTop();

                int visibleItemCount = lln.getChildCount();
                int totalItemCount = lln.getItemCount();
                int pastVisiblesItems = lln.findFirstVisibleItemPosition();
                //Toast.makeText(getActivity(),"total: "+totalItemCount,Toast.LENGTH_SHORT).show();

                if (mRecyclerView.getAdapter() == null) {
                    return;
                }
                if (stop ) {
                    return;
                }

                int l = visibleItemCount + pastVisiblesItems;
                try {
                    if (l >= totalItemCount && !isLoading) {
                        // It is time to add new data. We call the listener
                        isLoading = true;
                        contador++;
                        recuperarComentarios("" + contador);

                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                    //sair();
                }
            }
        });

        //GERENCIAR A APRESENTAÇÃO DOS ITENS
        lln = new LinearLayoutManager(ComentarioActivity.this);
        lln.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lln);

        imgSendComment = (Button) findViewById(R.id.send_comment);
        edtTextToSend = (EditText) findViewById(R.id.edt_text_to_send);

        imgSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = edtTextToSend.getText().toString();
                if (text.length() > 0) {
                    if (Connection.existeConexao(ComentarioActivity.this)) {
                        new EnviarCommentarioAsyncTask().execute(text);
                    }
                } else {
                    GlobalDialog.alert(ComentarioActivity.this, "Qual é o seu comentário?", "Ok");
                }
            }
        });

        recuperarComentarios("0");
        System.out.println("On create View");
    }


    private void recuperarComentarios(String pagina){
        if(Connection.existeConexao(ComentarioActivity.this)) {
            new GetComentariosAsyncTask(ComentarioActivity.this).execute(pagina);
        }
    }

    public class GetComentariosAsyncTask extends AsyncTask<String, Void, String> {

        Activity mContex;

        public GetComentariosAsyncTask(Activity contex){
            this.mContex = contex;
        }

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressDialog.show(getActivity(), "", "Carregando notícias...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "Sem resultado";
            int pagina = Integer.parseInt(params[0]);
            String link = Links.URL_LISTA_COMENTARIOS+ Usuario.getCurrent().getToken()+"&pagina="+pagina;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("idPost", postID);

                Log.e("JSON Listar Comentario:", jsonObject.toString());
                URL url = new URL(link);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(30 * 1000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(jsonObject.toString());
                out.close();

                System.out.println("Response Code: " + conn.getResponseCode());
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String response = ConversorInputStreamToString.convertInputStreamToString(in);
                System.out.println(response);
                result = response;

            } catch (UnknownHostException e) {
                result = "error_server";
                e.printStackTrace();
            } catch (IOException e) {
                if(e.getMessage().contains("No authentication challenges found")){
                    result = "sessao_expirada";
                }else{
                    result = "error_server";
                }
                e.printStackTrace();

            }catch (Exception e) {
                result = "error_server";
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mList == null){
                mList = new ArrayList<Comentario>();
            }
            try{
                if(result.equals("sessao_expirada")){
                    //CustomDialog.alert(mContex, "Sessão expirada. Conecte novamente.", "Voltar", "");
                    tokenInvalido();
                    return;
                }else if(result.equals("error_server")){
                    CustomDialog.alert(mContex, "Servidor inacessível. Tente novamente mais tarde.", "Voltar", "");
                    return;
                } else if(result.equals("[]")){
                    stop = true;
                }else{

                    JSONArray jsonArray = new JSONArray(result);

                    for(int i = 0; i < jsonArray.length();i++){
                        Comentario c = JSONConverter.JSONObjectToComentario(jsonArray.getJSONObject(i));
                        mList.add(c);
                    }

                    if(mList.size() == 0){
                        Toast.makeText(mContex, "Não há comentários", Toast.LENGTH_SHORT).show();
                    }

                    if(adapter == null) {
                        adapter = new ComentarioAdapter(mContex,mList);
                        mRecyclerView.setAdapter(adapter);
                    }else{
                        adapter.notifyDataSetChanged();
                    }

                    isLoading = false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void tokenInvalido() {
        new AlertDialog.Builder(this)
                .setMessage("Sessão expirada. Por favor conecte novamente.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sair();
                    }
                })
                .create()
                .show();
    }

    public void sair(){
        //System.out.println("deletado");
        Usuario.deleteCurrent();
        LoginManager.getInstance().logOut();//FACEBBOK
        //atualizarView();
        Intent intent = new Intent(ComentarioActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private class EnviarCommentarioAsyncTask extends AsyncTask<String,Void,String>{
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ComentarioActivity.this, "",
                    "Aguarde. Enviando comentário...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String sugestionToSend = params[0];
            try {
                if(sugestionToSend.length() > 0 ){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("descricao", params[0]);
                    jsonObject.put("posts_id", postID);

                    Log.e("JSON Enviar Comentario:", jsonObject.toString());
                    URL url = new URL(Links.URL_ENVIA_COMENTARIO+ Usuario.getCurrent().getToken());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(30 * 1000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type","application/json");

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(jsonObject.toString());
                    out.close();

                    System.out.println("Response Code: " + conn.getResponseCode());
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = ConversorInputStreamToString.convertInputStreamToString(in);
                    System.out.println(response);
                    result = response;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalStateException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            if(s.contains("sucesso")){
                String  comment = edtTextToSend.getText().toString();
                edtTextToSend.setText("");
                Comentario comm = new Comentario();
                comm.setData("Agora");
                comm.setNome(Usuario.getCurrent().getNome());
                comm.setIdImagem(Usuario.getCurrent().getIdImagem());
                comm.setComentario(comment);
                mList.add(0, comm);

                if(adapter == null) {
                    adapter = new ComentarioAdapter(ComentarioActivity.this,mList);
                    mRecyclerView.setAdapter(adapter);
                }else{
                    adapter.notifyDataSetChanged();
                }
            }else{
                GlobalDialog.alert(ComentarioActivity.this,"Aviso","Erro ao enviar comentário");
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
