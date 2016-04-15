package com.iaware.cabuu.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iaware.cabuu.R;
import com.iaware.cabuu.adapters.NoticiaAdapter;
import com.iaware.cabuu.entidades.Noticia;
import com.iaware.cabuu.entidades.Usuario;
import com.iaware.cabuu.utils.Connection;
import com.iaware.cabuu.utils.ConversorInputStreamToString;
import com.iaware.cabuu.utils.CustomDialog;
import com.iaware.cabuu.utils.JSONConverter;
import com.iaware.cabuu.utils.Links;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class AcoesNoticiasActivity extends Fragment {
    private RecyclerView mRecyclerView;
    private List<Noticia> mList;
    private NoticiaAdapter noticiaAdapter;
    LinearLayoutManager lln;
    private boolean isLoading;
    private int contador = 0;
    private boolean stop;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acoes_noticias, container, false);

        getActivity().setTitle(getResources().getString(R.string.menu_acoes_noticias));
        //mList = new ArrayList<Noticia>();
        isLoading = true;
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
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
                        recuperarNoticias(""+contador);

                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                    //sair();
                }
            }
        });

        //GERENCIAR A APRESENTAÇÃO DOS ITENS
        lln = new LinearLayoutManager(getActivity());
        lln.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lln);

        recuperarNoticias("0");
        System.out.println("On create View");
        return view;
    }


    private void recuperarNoticias(String pagina){
        if(Connection.existeConexao(getActivity())) {
            new GetNoticiasAsyncTask(getActivity()).execute(pagina);
        }
    }

    public class GetNoticiasAsyncTask extends AsyncTask<String, Void, String> {

        Activity mContex;

        public GetNoticiasAsyncTask(Activity contex){
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
            InputStream inputStream = null;
            String result = "Sem resultado";
            String pagina = params[0];
            String link = Links.URL_LISTA_POSTS+"?pagina="+pagina+"&token="+ Usuario.getCurrent().getToken();
            try {
                if (params != null) {

                    URL url = new URL(link);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(30 * 100);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/json");
                    System.out.println("URL: " + link);
                    System.out.println("Response Code: " + conn.getResponseCode());
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = ConversorInputStreamToString.convertInputStreamToString(in);
                    System.out.println("-"+response);
                    result = response;
                }
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
                mList = new ArrayList<Noticia>();
            }
            try{
                if(result.equals("sessao_expirada")){
                    //CustomDialog.alert(mContex, "Sessão expirada. Conecte novamente.", "Voltar", "");
                    ((PrincipalActivity)getActivity()).tokenInvalido();
                    return;
                }else if(result.equals("error_server")){
                    CustomDialog.alert(mContex, "Servidor inacessível. Tente novamente mais tarde.", "Voltar", "");
                    return;
                } else if(result.equals("[]")){
                    stop = true;
                }else if(result.contains("Nenhuma participação encontrado") && contador == 0){

                }else{

                    JSONArray jsonArray = new JSONArray(result);

                    for(int i = 0; i < jsonArray.length();i++){
                        Noticia n = JSONConverter.JSONObjectToNoticia(jsonArray.getJSONObject(i));
                        if(n.getTipo().equals("imagem")) {
                            mList.add(n);
                        }
                    }

                    if(mList.size() == 0){
                        Toast.makeText(mContex, "Não há notícias", Toast.LENGTH_SHORT).show();
                    }

                    if(noticiaAdapter == null) {
                        noticiaAdapter = new NoticiaAdapter(mContex,mList);
                        mRecyclerView.setAdapter(noticiaAdapter);
                    }else{
                        noticiaAdapter.notifyDataSetChanged();
                    }

                    isLoading = false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
