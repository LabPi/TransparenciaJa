package com.iaware.cabuu.views;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.iaware.cabuu.utils.ConversorInputStreamToString;
import com.iaware.cabuu.utils.GlobalDialog;
import com.iaware.cabuu.utils.ImagemUtils;
import com.iaware.cabuu.R;
import com.iaware.cabuu.entidades.Usuario;
import com.iaware.cabuu.utils.Connection;
import com.iaware.cabuu.utils.Links;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditarPerfilFragment extends Fragment {
    private static final int SELECT_PICTURE = 100;
    private static final int TAKE_PHOTO = 101;
    private static final int RESULT_OK = -1;

    EditText edtNome;
    EditText edtEmail;
    EditText edtTelefone;

    SimpleDraweeView fotoFace;
    String imagemPerfilString = "";
    String pathImage;
    Uri fileUri;

    Bitmap bitmap;
    File dir;
    String timeStamp;
    boolean hasPhoto;

    Button buttonAtualizar;
    Usuario perfilUsuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editar_perfil, container, false);
        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Editar perfil");

        edtNome = (EditText) view.findViewById(R.id.edt_nome);
        edtEmail = (EditText) view.findViewById(R.id.edt_email);
        edtTelefone = (EditText) view.findViewById(R.id.edt_telefone);
        fotoFace = (SimpleDraweeView) view.findViewById(R.id.fotoFace);
        buttonAtualizar = (Button) view.findViewById(R.id.buttonAtualizar);


        perfilUsuario = Usuario.getCurrent();

        edtNome.setText(perfilUsuario.getNome());
        edtEmail.setText(perfilUsuario.getEmail());
        String link = Links.URL_IMAGE+perfilUsuario.getIdImagem();
        Uri uri = Uri.parse(link);
        fotoFace.setImageURI(uri);
        fotoFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseImageProfileNull();
            }
        });

        buttonAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connection.existeConexao(getActivity())) {
                    String nome = edtNome.getText().toString();
                    String email = edtEmail.getText().toString();
                    String telefone = edtTelefone.getText().toString();
                    UpdateProfileAsyncTask send = new UpdateProfileAsyncTask(getActivity());
                    send.execute(nome,email,telefone);
                }
            }
        });

        return view;
    }

    public class UpdateProfileAsyncTask extends AsyncTask<String, Object, String> {
        Context cxt;
        String email;


        public UpdateProfileAsyncTask(Context context) {
            cxt = context;

        }

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "",
                    "Aguarde. Atualizando sua conta...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            URL url;
            String json;

            try {
                url = new URL(Links.URL_EDITAR_PERFIL+ Usuario.getCurrent().getToken());

                JSONObject user_json = new JSONObject();
                String telefone = params[2];
                user_json.put("nome", params[0]);
                user_json.put("email", params[1]);
                user_json.put("tipo_imagem", "png");
                json = user_json.toString();
                Log.i("JSON ATUALIZAR", json);

                if(telefone == null ||
                        (telefone != null && telefone.equals(""))){
                    json = json.replace("}",",\"telefone\":\"\"}");
                }else{
                    user_json.put("telefone",telefone);
                    json = user_json.toString();
                }
                if(imagemPerfilString.equals("")){
                    json = json.replace("}",",\"imagem\":\"\"}");
                    Log.i("JSON ATUALIZAR", json);
                }else{
                    user_json.put("imagem", imagemPerfilString);
                    json = user_json.toString();
                    Log.i("JSON ATUALIZAR", json);
                }
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
                Log.e("RESULT",result);
            } catch (IOException e) {
                if(e.getMessage().contains("No authentication challenges found")){
                    result = "sessao_expirada";
                }else{
                    result = "error_server";
                }
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return result;
        }

        protected void onPostExecute(String result) {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }

            JSONObject json;
            try {
                if(result.equals("sessao_expirada")){
                    //CustomDialog.alert(mContex, "Sessão expirada. Conecte novamente.", "Voltar", "");
                    ((PrincipalActivity)getActivity()).tokenInvalido();
                    return;
                }
                    json = new JSONObject(result);
                if(!json.has("nome") || !json.has("image_id")){

                    GlobalDialog.alert(cxt, "Não foi possível atualizar o perfil. Tente novamente mais tarde.", "Ok");
                    return;
                }

                //Toast.makeText(CadastrarUsuarioActivity.this, "Result:: "+result,Toast.LENGTH_LONG).show();
                if (result == null) {
                    //Toast.makeText(CadastrarUsuarioActivity.this, "Não foi possível obter uma Conexão Com o Servidor",Toast.LENGTH_LONG).show();
                    GlobalDialog.alert(cxt,"Não foi possível obter uma conexão com o servidor.","Ok");
                } else if (json.has("nome") && json.has("image_id")) {
                    GlobalDialog.alert(cxt,"Perfil atualizado com sucesso.","Ok");
                    String nome = json.getString("nome");
                    String email = json.getString("email");
                    String telefone = json.getString("telefone");
                    Integer nomeImage = json.getInt("image_id");

                    perfilUsuario.setNome(nome);
                    perfilUsuario.setEmail(email);
                    if(telefone != null) {
                        perfilUsuario.setTelefone(telefone);
                    }else{
                        perfilUsuario.setTelefone("");
                    }
                    perfilUsuario.setIdImagem(nomeImage);

                    perfilUsuario.save();

                    Log.i("PERFIL 2", nome + " " + nomeImage);

                    ((PrincipalActivity)getActivity()).atualizarView();
                    //new SaveBitmapUtil(EditarPerfilActivity.this).salvarBitmap(nomeImage,bitmap);
                }else if(json.has("erro")){
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void rotacionarImagem(Bitmap bitmapOrg){
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg,300,300,true);

        bitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
        imagemPerfilString = ImagemUtils.bitmapToString(bitmap);
        fotoFace.setImageBitmap(bitmap);
    }

    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);

        if (request == TAKE_PHOTO) {
            if (result == RESULT_OK) {
                fileUri = Uri.fromFile(new File(pathImage));
                setPic(fileUri);
                hasPhoto = true;

                Bitmap bm = BitmapFactory.decodeFile(pathImage);
                bitmap = bm;

                //Log.i("IMAGE PATH",pathImage);

                bm = ImagemUtils.getResizedBitmap(bm,200,200);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                imagemPerfilString = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
        } else if (request == SELECT_PICTURE && data != null) {

            //fotoFace = (CircleImageView) findViewById(R.id.fotoFace);

            Uri selectedImageUri = data.getData();

            pathImage = getPath(selectedImageUri);
            Bitmap bm = BitmapFactory.decodeFile(pathImage);
            bitmap = bm;
            fotoFace.setImageBitmap(bm);

//            imagemPerfilString = ImagemUtil.BitMapToString(bm);

            //Log.i("IMAGE PATH",pathImage);

            bm = ImagemUtils.getResizedBitmap(bm, 200, 200);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            imagemPerfilString = Base64.encodeToString(byteArray, Base64.DEFAULT);

            //fotoFace.setImageBitmap(bm);
            hasPhoto = true;

        } else {
            //fb.authorizeCallback(request, result, data);
        }

    }


    private void setPic(Uri uri) {
        getActivity().getContentResolver().notifyChange(uri, null);
        ContentResolver cr = getActivity().getContentResolver();
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



        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);

        fotoFace.setImageBitmap(bitmap2);
    }

    public String getPath(Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;

    }



    private void choseImageProfileNull() {

        AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
        final String[] lista = {"Tirar foto", "Escolher da galeria"};
        mensagem.setTitle("Foto do Perfil");


        mensagem.setItems(lista, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 1:
                        try {/*****
                         Intent intent = new Intent();
                         intent.setType("image/*");
                         intent.setAction(Intent.ACTION_GET_CONTENT);
                         startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
                         ****/
                            Intent i = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            //i.setType("video/*, images/*");
                            i.setType("image/*");
                            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0:
                        try {
                            dir = new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES), "IDB");

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

                            startActivityForResult(i, TAKE_PHOTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case 2:
                        dialog.dismiss();
                        break;
                    case 7:
                        break;
                    default:
                        dialog.dismiss();
                }
            }
        });

        mensagem.show();

    }

    private void choseImageProfileNotNull() {

        AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
        final String[] lista = {"Sem foto", "Tirar foto", "Escolher da galeria"};
        mensagem.setTitle("Foto do Perfil");


        mensagem.setItems(lista, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        pathImage = null;
                        fileUri = null;
                        fotoFace.setImageResource(R.drawable.imagem_user);
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
                                    Environment.DIRECTORY_PICTURES), "idb");

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
                        break;
                }
            }
        });

        mensagem.show();

    }

}
