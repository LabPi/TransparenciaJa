package com.iaware.cabuu.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iaware.cabuu.R;
import com.iaware.cabuu.adapters.MinhasParticipacoesAdapter;
import com.iaware.cabuu.entidades.MinhaParticipacao;
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

public class MinhasParticipacoesFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private List<MinhaParticipacao> mList;
    private MinhasParticipacoesAdapter adapter;
    LinearLayoutManager lln;
    private boolean isLoading;
    private int contador = 0;
    private boolean stop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_minhas_participacoes, container, false);

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
                        recuperarMinhasParticipacoes("" + contador);

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

        recuperarMinhasParticipacoes("0");
        //recuperarMinhasParticipacoesOff();
        return view;
    }


    public void recuperarMinhasParticipacoesOff(){
        List<MinhaParticipacao> mps = new ArrayList<MinhaParticipacao>();

        for(int i = 0; i < 5; i++){
            MinhaParticipacao mp = new MinhaParticipacao();
            mp.setStatus("Em análise");
            mp.setData("Hoje");
            mp.setTitulo("Titulo " + i);

            mps.add(mp);
        }

        if(adapter == null) {
            adapter = new MinhasParticipacoesAdapter(getActivity(),mps);
            mRecyclerView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    private void recuperarMinhasParticipacoes(String pagina){
        if(Connection.existeConexao(getActivity())) {
            new GetMinhasParticipacoesAsyncTask(getActivity()).execute(pagina);
        }
    }

    public class GetMinhasParticipacoesAsyncTask extends AsyncTask<String, Void, String> {

        Activity mContex;

        public GetMinhasParticipacoesAsyncTask(Activity contex){
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
            String link = Links.URL_LISTA_MINHAS_PARTICIPACOES+ Usuario.getCurrent().getToken()+"&pagina="+pagina;
            try {
                /*JSONObject jsonObject = new JSONObject();
                jsonObject.put("idPost", postID);

                Log.e("JSON Listar Comentario:", jsonObject.toString());*/
                URL url = new URL(link);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(30 * 1000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                /*OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(jsonObject.toString());
                out.close();*/

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
                mList = new ArrayList<MinhaParticipacao>();
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
                        MinhaParticipacao mp = JSONConverter.JSONObjectToMinhaParticipacao(jsonArray.getJSONObject(i));
                        mList.add(mp);
                    }

                    if(mList.size() == 0){
                        Toast.makeText(mContex, "Não há comentários", Toast.LENGTH_SHORT).show();
                    }

                    if(adapter == null) {
                        adapter = new MinhasParticipacoesAdapter(mContex,mList);
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
}
