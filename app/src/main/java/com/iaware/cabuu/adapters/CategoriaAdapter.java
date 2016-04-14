package com.iaware.cabuu.adapters;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iaware.cabuu.entidades.Categoria;
import com.iaware.cabuu.R;
import com.iaware.cabuu.utils.Links;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PeDeNRiQue on 10/01/2016.
 */
public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.MyViewHolder> {

    private List<Categoria> mList;
    private LayoutInflater mLayoutInflater;
    private Context context;
    private List<MyViewHolder> myViewHolders;
    private String categoriaSelecionada = "";

    public CategoriaAdapter(Context context, List<Categoria> l){
        this.context = context;
        mList = l;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myViewHolders = new ArrayList<MyViewHolder>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_categorias, viewGroup, false);

        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    public String getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.tvNome.setText(mList.get(position).getNome());
        String link = Links.URL_IMAGE+mList.get(position).getIdImagem();
        Uri uri = Uri.parse(link);
        myViewHolder.imgFoto.setImageURI(uri);

        myViewHolders.add(myViewHolder);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public SimpleDraweeView imgFoto;
        public TextView tvNome;
        public RelativeLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgFoto = (SimpleDraweeView) itemView.findViewById(R.id.img_foto);
            tvNome = (TextView) itemView.findViewById(R.id.tv_nome);
            layout = (RelativeLayout) itemView.findViewById(R.id.background);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for(MyViewHolder m : myViewHolders){
                        //m.tvNome.setText( "Tste");
                        m.layout.setBackgroundColor(Color.WHITE);
                        categoriaSelecionada = mList.get(getAdapterPosition()).getIdReferencia().toString();
                    }
                    view.setBackgroundColor(Color.LTGRAY);
                }
            });

        }

    }
}
