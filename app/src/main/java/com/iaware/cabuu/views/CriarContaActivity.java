package com.iaware.cabuu.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iaware.cabuu.utils.ConversorInputStreamToString;
import com.iaware.cabuu.utils.CustomDialog;
import com.iaware.cabuu.utils.ImagemUtils;
import com.iaware.cabuu.R;
import com.iaware.cabuu.entidades.Usuario;
import com.iaware.cabuu.utils.Connection;
import com.iaware.cabuu.utils.Links;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CriarContaActivity extends AppCompatActivity {

    EditText edtNome;
    EditText edtTelefone;
    EditText edtEmail;
    EditText edtSenha;
    Button cadastrarUser;
    SimpleDraweeView imagemPerfil;

    private static final int SELECT_PICTURE = 100;
    private static final int TAKE_PHOTO = 101;
    final int SELECIONAR_IMAGEM = 100;
    final int TIRAR_FOTO = 101;
    String bytesImagem;


    File dir;
    String timeStamp;
    Uri fileUri;
    String pathImage;
    Bitmap bitmap;
    String imagemPerfilString;
    boolean hasPhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagemPerfil = (SimpleDraweeView) findViewById(R.id.imagemUser);
        edtNome = (EditText) findViewById(R.id.nomeUser);
        edtEmail = (EditText) findViewById(R.id.emailUser);
        edtTelefone = (EditText) findViewById(R.id.telefoneUser);
        edtSenha = (EditText) findViewById(R.id.senhaUser);

        cadastrarUser = (Button) findViewById(R.id.cadastrarUser);

        //imagemPerfil.setImageDrawable(getResources().getDrawable(R.drawable.imagem_user));

        cadastrarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampos()) {
                    criarConta();
                }
            }
        });

        edtSenha.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    cadastrarUser.performClick();
                    return true;
                }
                return false;
            }
        });
        imagemPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pathImage == null) {
                    choseImageProfileNull();
                } else {
                    choseImageProfileNotNull();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == SELECT_PICTURE) {
                hasPhoto = true;
                imagemPerfilString = getAbsolutePath(data.getData());
                bitmap = decodeFile(imagemPerfilString);
                imagemPerfil.setImageBitmap(bitmap);
            } else if (requestCode == TAKE_PHOTO) {
                hasPhoto = true;
                imagemPerfilString = getImagePath();
                bitmap = decodeFile(imagemPerfilString);
                imagemPerfil.setImageBitmap(bitmap);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
/*
        if (requestCode == TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                fileUri = Uri.fromFile(new File(pathImage));
                setPic(fileUri);
                hasPhoto = true;

                //Bitmap bm = BitmapFactory.decodeFile(pathImage);
                //bitmap = bm;
                bitmap = BitmapFactory.decodeFile(pathImage);
                //Log.i("IMAGE PATH",pathImage);

                //bm = ImagemUtil.getResizedBitmap(bm,64,64);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //bytesImagem = ImagemUtils.bitmapImageToString(bitmap);
                byte[] byteArray = stream.toByteArray();
                imagemPerfilString = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
        } else if (requestCode == SELECT_PICTURE && data != null) {

            imagemPerfil = (ImageView) findViewById(R.id.imagemUser);


            Uri selectedImageUri = data.getData();


            pathImage = getPath(selectedImageUri);
            //Bitmap bm = BitmapFactory.decodeFile(pathImage);
            //bitmap = bm;
            bitmap = BitmapFactory.decodeFile(pathImage);
            imagemPerfil.setImageBitmap(bitmap);

//            imagemPerfilString = ImagemUtil.BitMapToString(bm);

            //Log.i("IMAGE PATH",pathImage);

            //bm = ImagemUtil.getResizedBitmap(bm,64,64);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            imagemPerfilString = Base64.encodeToString(byteArray, Base64.DEFAULT);
            //bytesImagem = ImagemUtils.bitmapImageToString(bitmap);
            //fotoFace.setImageBitmap(bm);
            hasPhoto = true;

        }*/
    }

    public boolean validarCampos(){
        String nome = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        String telefone = edtTelefone.getText().toString();
        String senha = edtSenha.getText().toString();

        if(!hasPhoto){
            CustomDialog.alert(this, "Onde está sua foto?", "Voltar", "");
            return false;
        }else if(nome.equals("")){
            CustomDialog.alert(this,"Qual é seu nome?","Voltar","");
            return false;
        }else if(telefone.equals("")){
            CustomDialog.alert(this,"Qual é seu telefone?","Voltar","");
            return false;
        }else if(email.equals("")){
            CustomDialog.alert(this,"Qual é seu e-mail?","Voltar","");
            return false;
        }else if(!isEmailValid(email)){
            CustomDialog.alert(this,"E-mail inválido?","Voltar","");
            return false;
        }else if(senha.equals("")){
            CustomDialog.alert(this,"Qual é sua senha?","Voltar","");
            return false;
        }else if(senha.length() < 4){
            CustomDialog.alert(this,"Sua senha deve possuir mais de 4 caracteres","Voltar","");
            return false;
        }

        return true;
    }

    public static boolean isEmailValid(String email) {
        if ((email == null) || (email.trim().length() == 0))
            return false;

        String emailPattern = "\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
        Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void criarConta(){

        String json = criarJSON();

        if(Connection.existeConexao(CriarContaActivity.this)) {
            new CriarContaAsyncTask(CriarContaActivity.this).execute(json);
        }
    }

    public String criarJSON(){
        String nome = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        String telefone = edtTelefone.getText().toString();
        String senha = edtSenha.getText().toString();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("nome", nome);
            jsonObject.put("telefone",telefone);
            jsonObject.put("email",email);
            jsonObject.put("senha",senha);
            jsonObject.put("discriminador","US");


            if(bitmap != null ){//bytesImagem
                jsonObject.put("tipo_image", "png");
                /*bytesImagem = ImagemUtils.bitmapImageToString(bitmap);
                jsonObject.put("image", bytesImagem);*/
                //System.out.println("BYTES: "+bytesImagem.toString());
                bytesImagem = ImagemUtils.bitmapImageToString(bitmap);
                jsonObject.put("image", bytesImagem);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public class CriarContaAsyncTask extends AsyncTask<String, Void, String> {

        Activity mContex;

        public CriarContaAsyncTask(Activity contex){
            this.mContex = contex;
        }

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(CriarContaActivity.this, "",
                    "Aguarde. Criando sua conta...", true);
        }


        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "Sem resultado";
            String json = (String) params[0];
            String link = Links.URL_CRIAR_CONTA;
            try {
                if (params != null) {
                    Log.i("LINK CADASTRO",link);
                    Log.i("JSON_CADASTRO",json);
                    URL url = new URL(link);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(30 * 1000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type","application/json");

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

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "error_server";
            } catch (ConnectException e){
                e.printStackTrace();
                result = "servidor_fora";
            }catch (SocketTimeoutException e){
                e.printStackTrace();
                result = "servidor_fora";
            }catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //Todo tratar retorno e salvar sync true se for sucesso
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            if(result.contains("error_server")) {
                //Toast.makeText(CriarContaActivity.this,"Erro ao criar sua conta",Toast.LENGTH_SHORT).show();
                CustomDialog.alert(mContex, "Ocorreu um erro ao criar sua conta", "VOLTAR","");
            }else if(result.contains("sucesso")){//Cadastro realizado com sucesso
                //login(edtEmail.getText().toString(),edtSenha.getText().toString());
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    //converterJson(jsonObject);
                   login(edtEmail.getText().toString(), edtSenha.getText().toString());
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }else if(result.contains("email.duplicado")) {
                CustomDialog.alert(mContex,"E-mail já cadastrado","Voltar", "");
            }else if(result.contains("servidor_fora")){
                CustomDialog.alert(mContex,"Servidor fora do ar. Tente novamente mais tarde","Voltar", "");
            }else{
                //Toast.makeText(CriarContaActivity.this,"Erro ao criar sua conta",Toast.LENGTH_SHORT).show();
                CustomDialog.alert(mContex,"Erro ao criar sua conta","Voltar", "");
            }
        }
    }

    public void converterJson(String string){

        Usuario usuario = new Usuario();
        JSONObject consulta;
        try{
                consulta = new JSONObject(string);

            if(string.contains("authToken")) {
                usuario.setNome(consulta.getString("nome"));
                usuario.setIdRemoto(consulta.getInt("id_usuario"));
                usuario.setDiscriminador(consulta.getString("discriminador"));
                usuario.setEmail(edtEmail.getText().toString());
                usuario.setIdImagem(consulta.getInt("idImage"));
                usuario.setToken(consulta.getString("authToken"));

                usuario.save();

                finish();
                Intent intent = new Intent(this, PrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(String email, String senha){
        Log.i("email e senha", email + senha);
        //addUsuario();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("email",email);
            jsonObject.put("senha",senha);
            jsonObject.put("discriminador","US");

            new LoginAsyncTask(CriarContaActivity.this).execute(jsonObject.toString());
        }catch (JSONException e){
            e.printStackTrace();
        }

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
            dialog = ProgressDialog.show(CriarContaActivity.this, "",
                    "Aguarde. Logando...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "Sem resultado";
            String json = (String) params[0];
            try {
                if (params != null) {

                    URL url = new URL(Links.URL_LOGIN);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(30 * 1000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type","application/json");

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
            //JSONObject jsonObject = new JSONObject(result);

            converterJson(result);
        }
    }

    private void choseImageProfileNull() {

        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        final String[] lista = {"Tirar foto", "Escolher da galeria"};
        mensagem.setTitle("Foto do Perfil");


        mensagem.setItems(lista, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 1:
                        try {
                            /*Intent i = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            //i.setType("video*//*, images*//*");
                            i.setType("image*//*");
                            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);*/
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0:
                        try {
                            /*dir = new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES), "CLUBE");

                            if (!dir.exists()) {
                                dir.mkdirs();
                                //Toast.makeText(getBaseContext(), "Pasta criada", Toast.LENGTH_LONG).show();
                            } else {
                                Log.e("Diretorio", "Ja criado");
                            }

                            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                            Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                            pathImage = dir.getPath() + "/" + timeStamp + ".jpg";

                            fileUri = Uri.fromFile(new File(pathImage));

                            i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                            startActivityForResult(i, TAKE_PHOTO);*/

                            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                            startActivityForResult(intent, TAKE_PHOTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case 2:
                        dialog.dismiss();
                        break;
                    case 7:
                        finish();
                        break;
                    default:
                        dialog.dismiss();
                }
            }
        });

        mensagem.show();

    }

    private void choseImageProfileNotNull() {

        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        final String[] lista = {"Sem foto", "Tirar foto", "Escolher da galeria"};
        mensagem.setTitle("Foto do Perfil");


        mensagem.setItems(lista, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        pathImage = null;
                        fileUri = null;
                        imagemPerfil.setImageResource(R.drawable.imagem_user);
                        hasPhoto = false;
                        break;
                    case 2:
                        try {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            dir = new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES), "CLUBE");

                            if (!dir.exists()) {
                                dir.mkdirs();
                            } else {
                                Log.e("Diretorio", "Ja criado");
                            }

                            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                            Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                            pathImage = dir.getPath() + "/" + timeStamp + ".jpg";

                            fileUri = Uri.fromFile(new File(pathImage));

                            i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                            startActivityForResult(i, TAKE_PHOTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case 7:
                        finish();
                        break;
                }
            }
        });

        mensagem.show();

    }

    private void setPic(Uri uri) {
        getContentResolver().notifyChange(uri, null);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap = null;
        int w = 0;
        int h = 0;
        Matrix mtx = new Matrix();


        try {
            bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
            w = bitmap.getWidth();
            h = bitmap.getHeight();

            mtx = new Matrix();

            ExifInterface exif = new ExifInterface(pathImage);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case 3: // ORIENTATION_ROTATE_180
                    mtx.postRotate(180);
                    break;
                case 6: // ORIENTATION_ROTATE_90
                    mtx.postRotate(90);
                    break;
                case 8: // ORIENTATION_ROTATE_270
                    mtx.postRotate(270);
                    break;
                default: // ORIENTATION_ROTATE_0
                    mtx.postRotate(0);
                    break;
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);

        imagemPerfil.setImageBitmap(bitmap2);
    }

    public String getPath(Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;

    }


    public Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        this.pathImage = file.getAbsolutePath();
        return imgUri;
    }


    public String getImagePath() {
        return pathImage;
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}
