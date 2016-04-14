package com.iaware.cabuu.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iaware.cabuu.entidades.Comentario;
import com.iaware.cabuu.utils.Links;
import com.iaware.cabuu.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by PeDeNRiQue on 10/01/2016.
 */
public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.MyViewHolder> {

    private List<Comentario> mList;
    private LayoutInflater mLayoutInflater;
    private Context context;

    public ComentarioAdapter(Context context, List<Comentario> l){
        this.context = context;
        mList = l;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_comentario,viewGroup,false);

        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.tvName.setText(mList.get(position).getNome());
        myViewHolder.tvData.setText(mList.get(position).getData());
        myViewHolder.tvDescription.setText(mList.get(position).getComentario());
        String link = Links.URL_IMAGE+mList.get(position).getIdImagem();
        Uri uri = Uri.parse(link);
        myViewHolder.imgPhoto.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        public TextView tvData;
        public TextView tvDescription;
        public SimpleDraweeView imgPhoto;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvData = (TextView) itemView.findViewById(R.id.tv_data);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            imgPhoto = (SimpleDraweeView) itemView.findViewById(R.id.img_photo);

        }
    }
}
