package com.iaware.cabuu.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.iaware.cabuu.adapters.ImagemSpinnerAdapter;
import com.iaware.cabuu.utils.ConversorInputStreamToString;
import com.iaware.cabuu.utils.GlobalDialog;
import com.iaware.cabuu.utils.ImagemUtils;
import com.iaware.cabuu.R;
import com.iaware.cabuu.entidades.Categoria;
import com.iaware.cabuu.entidades.Midia;
import com.iaware.cabuu.entidades.Usuario;
import com.iaware.cabuu.utils.Connection;
import com.iaware.cabuu.utils.CustomDialog;
import com.iaware.cabuu.utils.Links;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SugestoesFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    Usuario usuario;

    EditText endereco;
    EditText titulo;
    ImageButton on_click_localizacao;
    CheckBox anonimo;
    EditText descricao;
    Button enviar;
    Spinner spinner;

    final int STATIC_INTEGER_VALUE = 100;
    Double latitude = null;
    Double longitude = null;

    ImageView imgMidia1;
    ImageView imgMidia2;
    ImageView imgMidia3;
    ImageView imageView;

    Midia midia1 = null;
    Midia midia2 = null;
    Midia midia3 = null;

    int controle;
    RelativeLayout rlMidia1;
    RelativeLayout rlMidia2;
    RelativeLayout rlMidia3;

    public static int TAKE_PICTURE = 1;
    public static int REQUEST_CAMERA = 2;
    public static int SELECT_FILE = 3;
    public static int SELECT_VIDEO = 4;
    public static int GRAVAR_VIDEO = 5;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected static final String TAG = "SugestoesActivity";

    String categoria;
    List<Categoria> categorias;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sugestoes, container, false);

        endereco = (EditText) rootView.findViewById(R.id.endereco);
        titulo = (EditText) rootView.findViewById(R.id.titulo);
        on_click_localizacao = (ImageButton) rootView.findViewById(R.id.on_click_localizacao);
        anonimo = (CheckBox) rootView.findViewById(R.id.anonimo);
        descricao = (EditText) rootView.findViewById(R.id.descricao);
        enviar = (Button) rootView.findViewById(R.id.enviar);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        anonimo.setVisibility(View.INVISIBLE);
        endereco.setEnabled(false);

        imgMidia1 = (ImageView) rootView.findViewById(R.id.midia1);
        imgMidia2 = (ImageView) rootView.findViewById(R.id.midia2);
        imgMidia3 = (ImageView) rootView.findViewById(R.id.midia3);

        rlMidia1 = (RelativeLayout) rootView.findViewById(R.id.rl_midia1);
        rlMidia2 = (RelativeLayout) rootView.findViewById(R.id.rl_midia2);
        rlMidia3 = (RelativeLayout) rootView.findViewById(R.id.rl_midia3);

        midia1 = new Midia("","",1);
        midia2 = new Midia("","",2);
        midia3 = new Midia("","",3);

        buildGoogleApiClient();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        on_click_localizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivityForResult(intent, STATIC_INTEGER_VALUE);
            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 if (imgMidia1.getDrawable() == null && imgMidia2.getDrawable() == null && imgMidia3.getDrawable() == null) {
                    CustomDialog.alert(getActivity(), "Você deve enviar pelo menos uma mídia", "Voltar", "");
                }else if(latitude == null && longitude == null){
                     CustomDialog.alert(getActivity(), "Você deve passar a localização do foco", "Voltar", "");
                 }else if(descricao.getText().toString().isEmpty()) {
                     CustomDialog.alert(getActivity(), "Descreva sua sugestão", "Voltar", "");
                 }else if(titulo.getText().toString().isEmpty()){
                     GlobalDialog.alert(getActivity(), "Qual o título da sugestão?", "Voltar");
                 }else{
                    InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    enviarSugestao();
                }
            }
        });

        rlMidia1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = imgMidia1;
                controle = 1;
                selecionarMidia();
            }
        });
        rlMidia2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = imgMidia2;
                controle = 2;
                selecionarMidia();
            }
        });
        rlMidia3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = imgMidia3;
                controle = 3;
                selecionarMidia();
            }
        });

        rlMidia1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removerMidia(imgMidia1, 1);
                return true;
            }
        });
        rlMidia2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removerMidia(imgMidia2,2);

                return true;
            }
        });
        rlMidia3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removerMidia(imgMidia3,3);

                return true;
            }
        });

        if(Connection.existeConexao(getActivity())){
            String l = Links.URL_LISTA_PROGRAMAS;
            new GetCategoriasAsyncTask(getActivity()).execute(l+ Usuario.getCurrent().getToken());
        }

        return rootView;
    }

    public void removerMidia(final ImageView imgView,final int controle){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity());
        builder.setTitle("Aviso");
        builder.setMessage("Deseja remover a mídia?");
        builder.setCancelable(false);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (controle == 1) {
                            midia1 = new Midia("", "", 1);
                        } else if (controle == 2) {
                            midia2 = new Midia("", "", 2);
                            ;
                        } else if (controle == 3) {
                            midia3 = new Midia("", "", 3);
                            ;
                        }
                        imgView.setImageBitmap(null);
                    }
                });
        builder.setNegativeButton("Não", null);
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    public void selecionarMidia(){
        final CharSequence[] items = { "Tirar foto", "Escolher imagem","Escolher vídeo" ,"Gravar vídeo","Cancelar" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecionar foto / vídeo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Tirar foto")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, TAKE_PICTURE);
                } else if (items[item].equals("Escolher imagem")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Escolher vídeo")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("video/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"),
                            SELECT_VIDEO);
                } else if (items[item].equals("Gravar vídeo")) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                    startActivityForResult(intent, GRAVAR_VIDEO);
                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STATIC_INTEGER_VALUE && resultCode == getActivity().RESULT_OK && data != null) {
            Bundle b = data.getExtras();
            latitude = b.getDouble("latitude");
            longitude = b.getDouble("longitude");
            System.out.println("Latitude: "+ latitude);
            System.out.println("Longitude: " + longitude);
            getAddressFromLocation(latitude,longitude, getActivity(), new Handler());
        }else if(data == null){
            System.out.println("cancel");
        }else if(requestCode == 1 && resultCode == 0 && data != null){
            System.out.println("cancel");
        }else{
            try {
                int controleAux = ImagemUtils.setImage(getActivity(), imageView, requestCode, resultCode, data, controle).getControle();
                if(controleAux == 1){
                    midia1 = ImagemUtils.setImage(getActivity(), imageView, requestCode, resultCode, data, controle);
                }else if(controleAux == 2){
                    midia2 = ImagemUtils.setImage(getActivity(), imageView, requestCode, resultCode, data, controle);
                }else if(controleAux == 3){
                    midia3 = ImagemUtils.setImage(getActivity(), imageView, requestCode, resultCode, data, controle);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void enviarSugestao() {
        JSONObject jsonObject = criarJSONSugestao();
        new EnviarSugestaoAsyncTask(getActivity()).execute(jsonObject.toString());

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            getAddressFromLocation(latitude, longitude, getActivity(), new Handler());
            mGoogleApiClient.disconnect();
        } else {
            Toast.makeText(getActivity(), "No Location", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public class EnviarSugestaoAsyncTask extends AsyncTask<String, Void, String> {

        Activity mContex;

        public EnviarSugestaoAsyncTask(Activity contex){
            this.mContex = contex;
        }

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(mContex, "","Aguarde. Enviando sugestão...", true);
        }


        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "Sem resultado";
            String json = (String) params[0];
            try {
                if (params != null) {
                    URL url = new URL(Links.URL_ENVIAR_SUGESTAO+ usuario.getCurrent().getToken());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(30 * 1000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(json);
                    out.close();

                    System.out.println("Response Code: " + conn.getResponseCode());
                    System.out.println("Response Message: " + conn.getResponseMessage());
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = ConversorInputStreamToString.convertInputStreamToString(in);
                    System.out.println(response);
                    result = response;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //Todo tratar retorno e salvar sync true se for sucesso
            if(dialog != null || dialog.isShowing()) {
                dialog.dismiss();
            }
            if(result.contains("sucesso")){
                Toast.makeText(mContex, "Sugestão enviada com sucesso", Toast.LENGTH_SHORT).show();

                NavigationView navigationView = (NavigationView) mContex.findViewById(R.id.nav_view);
                FragmentManager fm = getFragmentManager();
                //getActivity().setTitle("Ações e Notícias");
                navigationView.getMenu().getItem(0).setChecked(true);
                fm.beginTransaction().replace(R.id.content_frame, new AcoesNoticiasActivity()).commit();

            } else {
                Toast.makeText(mContex,"Erro ao enviar sugestão",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getAddressFromLocation(final double latitude, final double longitude,
                                       final Context context, final Handler handler) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getAddressLine(0)).append(", ");
                sb.append(address.getLocality()).append(", ");
                sb.append(address.getCountryCode());

                result = sb.toString();
                endereco.setText(result);
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder", e);
        }
    }

    public JSONObject criarJSONSugestao(){
        JSONObject jsonObject = new JSONObject();

        long programa = categorias.get((int)spinner.getSelectedItemId()).getIdReferencia();

        try {
            jsonObject.put("conteudo", descricao.getText().toString());
            jsonObject.put("endereco", endereco.getText().toString());
            jsonObject.put("titulo", titulo.getText().toString());
            jsonObject.put("idPrograma", programa);

            if(latitude != null) {
                jsonObject.put("latitude", latitude);
            }
            if(longitude != null) {
                jsonObject.put("longitude", longitude);
            }
            jsonObject.put("arq_um", midia1.getConteudo());
            jsonObject.put("tipo_um", midia1.getTipo());

            jsonObject.put("arq_dois", midia2.getConteudo());
            jsonObject.put("tipo_dois", midia2.getTipo());

            jsonObject.put("arq_tres", midia3.getConteudo());
            jsonObject.put("tipo_tres", midia3.getTipo());

        }catch (JSONException e){
            e.printStackTrace();
        }
        System.out.println("TIPOS: "+midia1.getTipo()+" "+midia2.getTipo()+" "+midia3.getTipo());
        return jsonObject;
    }

    public class GetCategoriasAsyncTask extends AsyncTask<String, Void, String> {

        Activity mContex;

        public GetCategoriasAsyncTask(Activity contex){
            this.mContex = contex;
        }

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(mContex, "", "Carregando categorias...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "Sem resultado";
            String link = params[0];
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
            } catch (FileNotFoundException e) {
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
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            try{
                if(result.equals("sessao_expirada")){
                    //CustomDialog.alert(mContex, "Sessão expirada. Conecte novamente.", "Voltar", "");
                    ((PrincipalActivity)getActivity()).tokenInvalido();
                    return;
                }else if(result.equals("error_server")) {
                    dialog.dismiss();
                    CustomDialog.alert(mContex, "Servidor inacessível. Tente novamente mais tarde.", "Voltar", "");
                    return;
                }else{
                    dialog.dismiss();
                    JSONArray jsonArray = new JSONArray(result);
                    categorias = new ArrayList<Categoria>();

                    for(int i = 0; i < jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Categoria c = new Categoria();
                        c.setIdReferencia(jsonObject.getInt("id"));
                        c.setIdImagem(jsonObject.getInt("idImagem"));
                        c.setNome(jsonObject.getString("nome"));
                        categorias.add(c);
                    }


                    ImagemSpinnerAdapter adapter = new ImagemSpinnerAdapter(mContex, R.layout.custom_spinner,
                            (ArrayList) categorias);
                    spinner.setAdapter(adapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
