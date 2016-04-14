package com.iaware.cabuu.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iaware.cabuu.entidades.Usuario;
import com.iaware.cabuu.utils.Connection;
import com.iaware.cabuu.utils.ConversorInputStreamToString;
import com.iaware.cabuu.utils.GlobalDialog;
import com.iaware.cabuu.utils.ImagemUtils;
import com.iaware.cabuu.utils.Links;
import com.iaware.cabuu.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;


public class LoginActivity extends AppCompatActivity {

    //FACEBOOK
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    String idFacebook;
    String nomeFacebook;
    String email;
    //FIM FACEBOOK

    ProgressDialog gDialog = null;

    TextView tvRegister;
    Button btnLogin;
    EditText edtUser;
    EditText edtPassword;

    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.content_login);

        tvRegister = (TextView) findViewById(R.id.tv_register);
        btnLogin = (Button) findViewById(R.id.entrarUser);
        edtUser = (EditText) findViewById(R.id.emailUser);
        edtPassword = (EditText) findViewById(R.id.password);

        //FACEBOOK
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email"));
        callbackManager = CallbackManager.Factory.create();

        if(isLoggedIn() || Usuario.hasLogedUser()){
            Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                gDialog = ProgressDialog.show(LoginActivity.this, "",
                        "Aguarde. Logando...", true);
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.v("LoginActivity", object.toString());

                                try {
                                    idFacebook = object.getString("id");
                                    nomeFacebook = object.getString("name");
                                    email = object.getString("email");
                                    /*editEmail.setText(object.getString("email"));
                                    getProfilePicture(idFacebookUser);*/
                                    System.out.println("JSON: " + object.toString());
                                    salvarImagemFacebook(idFacebook);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Connection.existeConexao(LoginActivity.this);
            }
        });
        //FIM FACEBOOK

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CriarContaActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtUser.getText().toString().equals("")){
                    GlobalDialog.alert(LoginActivity.this, "Campo de e-mail vazio", "Ok");
                }else if(edtPassword.getText().toString().equals("")){
                    GlobalDialog.alert(LoginActivity.this,"Campo da senha vazio","Ok");
                }else if(Connection.existeConexao(LoginActivity.this)){
                    login(edtUser.getText().toString(),edtPassword.getText().toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void loginFacebook(String email,String nome,Bitmap bitmap){
        Log.i("email e senha", email + " " + nome);
        //addUsuario();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("email",email);
            jsonObject.put("nome",nome);
            jsonObject.put("discriminador","USFB");
            jsonObject.put("image", ImagemUtils.bitmapImageToString(bitmap));
            jsonObject.put("tipo_image","jpg");
        }catch (JSONException e){
            e.printStackTrace();
        }
        if(Connection.existeConexao(LoginActivity.this)) {
            new LoginAsyncTask(LoginActivity.this).execute(jsonObject.toString());
        }
    }


    public void login(String email, String senha){
        Log.i("email e senha", email + " " + senha);
        //addUsuario();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("email",email);
            jsonObject.put("senha",senha);
            jsonObject.put("discriminador","US");
        }catch (JSONException e){
            e.printStackTrace();
        }

        if(Connection.existeConexao(LoginActivity.this)) {
            new LoginAsyncTask(LoginActivity.this).execute(jsonObject.toString());
        }

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    public class LoginAsyncTask extends AsyncTask<String, Void, String> {

        Activity mContex;

        public LoginAsyncTask(Activity contex){
            this.mContex = contex;
        }

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(gDialog == null) {
                dialog = ProgressDialog.show(mContex, "",
                        "Aguarde. Logando...", true);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "Sem resultado";
            String json = (String) params[0];
            try {
                if (params != null) {

                    URL url = new URL(Links.URL_LOGIN);
                    System.out.println("JSON LOGIN :"+json);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(30 * 1000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type","application/json");

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(json);
                    out.close();

                    System.out.println("Response Code: " + conn.getResponseCode());
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = ConversorInputStreamToString.convertInputStreamToString(in);
                    System.out.println(response);
                    result = response;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "error_server";
            } catch (Exception e) {
                e.printStackTrace();
                return "error_server";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //Todo tratar retorno e salvar sync true se for sucesso

            if(dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if(gDialog != null && gDialog.isShowing()){
                gDialog.dismiss();
            }
            //JSONObject jsonObject1 = new JSONObject(result);

            JSONObject json;
            try{

                if (result.contains("authToken")) {
                    json = new JSONObject(result);
                    usuario = new Usuario();
                    usuario.setNome(json.getString("nome"));
                    usuario.setIdRemoto(json.getInt("id_usuario"));
                    usuario.setDiscriminador(json.getString("discriminador"));
                    if(email != null) {
                        usuario.setEmail(email);
                    }else{
                        usuario.setEmail(edtUser.getText().toString());
                    }
                    usuario.setIdImagem(json.getInt("idImage"));
                    usuario.setToken(json.getString("authToken"));

                    usuario.save();

                    Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else if(result.contains("error_server")){
                    GlobalDialog.alert(LoginActivity.this,"Servidor inacessível. Tente mais tarde","Ok");
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }


        }
    }


    //IMAGEM DO FACEBOOK
    private String userFacebookId;
    public void salvarImagemFacebook(String id) {
        userFacebookId = id;

        new AsyncTask<Void, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(Void... params)
            {
                // safety check
                if (userFacebookId == null)
                    return null;

                String url = String.format(
                        "https://graph.facebook.com/%s/picture",
                        userFacebookId);

                // you'll need to wrap the two method calls
                // which follow in try-catch-finally blocks
                // and remember to close your input stream

                InputStream inputStream = null;
                try {
                    inputStream = new URL(url).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap)
            {
                // safety check
                if (bitmap != null){
                    //imagem.setImageBitmap(bitmap);
                    //ImagemUtils.saveImageToSD(bitmap, userFacebookId);
                    loginFacebook(email,nomeFacebook,bitmap);
                }
                // do what you need to do with the bitmap :)
            }
        }.execute();

        //return "OK";
    }

}
