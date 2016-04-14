package com.iaware.cabuu.views;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iaware.cabuu.R;
import com.iaware.cabuu.entidades.Usuario;
import com.iaware.cabuu.utils.Links;
import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CallbackManager callbackManager;//FACEBOOK
    FragmentManager fm;
    Usuario usuario;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        atualizarView();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_acoes_noticias) {
            setTitle("Notícias");
            fm.beginTransaction().replace(R.id.content_frame, new AcoesNoticiasActivity()).commit();
        } else if (id == R.id.nav_sugestoes) {
                setTitle("Participe");
                fm.beginTransaction().replace(R.id.content_frame, new SugestoesFragment()).commit();

        } else if (id == R.id.nav_m_sugestoes) {
            setTitle("Participações");
            fm.beginTransaction().replace(R.id.content_frame, new MinhasParticipacoesFragment()).commit();
        } else if (id == R.id.nav_login) {
                alertSair();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void atualizarView(){
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_principal);
            SimpleDraweeView imagemUserLogado = (SimpleDraweeView) headerView.findViewById(R.id.imagem_perfil);
            TextView nomeUserLogado = (TextView) headerView.findViewById(R.id.nome);
            TextView email = (TextView) headerView.findViewById(R.id.email);
            LinearLayout layoutperfil = (LinearLayout) headerView.findViewById(R.id.layout_perfil);
            System.out.println("TOKEN " + usuario.getCurrent().getToken());
            nomeUserLogado.setText(usuario.getCurrent().getNome());
            email.setText(usuario.getCurrent().getEmail());

            Bundle extras = getIntent().getExtras();
            if(extras !=null) {
                String idImagem = extras.getString("idImagem");
                String link = "https://graph.facebook.com/"+idImagem+"/picture";
                Uri uri = Uri.parse(link);
                imagemUserLogado.setImageURI(uri);

            }else {
                if(usuario.getCurrent().getIdImagem() != null && usuario.getCurrent().getIdImagem() > 0){
                    String link = Links.URL_IMAGE+usuario.getCurrent().getIdImagem();
                    Uri uri = Uri.parse(link);
                    imagemUserLogado.setImageURI(uri);
                    System.out.println("LINK: "+link);
                }else {
                    imagemUserLogado.setImageDrawable(getResources().getDrawable(R.drawable.imagem_user));
                }
            }

            //inicia nessa view
            fm = getFragmentManager();
            setTitle(getResources().getString(R.string.menu_acoes_noticias));
            navigationView.getMenu().getItem(0).setChecked(true);
            fm.beginTransaction().replace(R.id.content_frame, new AcoesNoticiasActivity()).commit();

            navigationView.getMenu().getItem(3).setIcon(R.drawable.ic_action_sair);
            navigationView.getMenu().getItem(3).setTitle(getResources().getString(R.string.menu_sair));

            layoutperfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarPerfil();
                }
            });


    }

    public void editarPerfil(){
        drawer.closeDrawers();
        fm = getFragmentManager();
        setTitle(getResources().getString(R.string.menu_acoes_noticias));
        fm.beginTransaction().replace(R.id.content_frame, new EditarPerfilFragment()).commit();
    }

    private void alertSair() {
        new AlertDialog.Builder(this)
                .setMessage("Deseja sair?")
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sair();
                    }
                })
                .setNegativeButton("NÃO", null)
                .create()
                .show();
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
        usuario.deleteCurrent();
        usuario = null;
        LoginManager.getInstance().logOut();//FACEBBOK
        //atualizarView();
        Intent intent = new Intent(PrincipalActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);//FACEBOOK
    }
    public CallbackManager getCallbackManager(){
        return callbackManager;
    }
    @Override
    protected void onResume() {
        super.onResume();

        //FACEBOOK
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(PrincipalActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //FACEBOOK
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(PrincipalActivity.this);
    }
}
