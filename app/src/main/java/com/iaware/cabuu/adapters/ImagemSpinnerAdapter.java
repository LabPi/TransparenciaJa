package com.iaware.cabuu.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iaware.cabuu.R;
import com.iaware.cabuu.entidades.Categoria;
import com.iaware.cabuu.utils.Links;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PeDeNRiQue on 28/03/2016.
 */
public class ImagemSpinnerAdapter extends ArrayAdapter<String> {

    LayoutInflater inflater;
    List<Categoria> categorias;

    public ImagemSpinnerAdapter(Context ctx, int txtViewResourceId, ArrayList objects) {
        super(ctx, txtViewResourceId, objects);
        categorias = (List<Categoria>) objects;

        inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View mySpinner = inflater.inflate(R.layout.custom_spinner, parent, false);

        TextView main_text = (TextView) mySpinner.findViewById(R.id.tv_nome);
        SimpleDraweeView imgFoto = (SimpleDraweeView) mySpinner.findViewById(R.id.img_foto);

        main_text.setText(categorias.get(position).getNome());
        String link = Links.URL_IMAGE+categorias.get(position).getIdImagem();
        Uri uri = Uri.parse(link);
        imgFoto.setImageURI(uri);

        return mySpinner;
    }
}



