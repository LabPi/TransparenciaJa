package com.iaware.cabuu.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iaware.cabuu.entidades.MinhaParticipacao;
import com.iaware.cabuu.R;

import java.util.List;

/**
 * Created by PeDeNRiQue on 10/01/2016.
 */
public class MinhasParticipacoesAdapter extends RecyclerView.Adapter<MinhasParticipacoesAdapter.MyViewHolder> {

    private List<MinhaParticipacao> mList;
    private LayoutInflater mLayoutInflater;
    private Context context;

    public MinhasParticipacoesAdapter(Context context, List<MinhaParticipacao> l){
        this.context = context;
        mList = l;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_minhas_participacoes,viewGroup,false);

        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.tvTitulo.setText(mList.get(position).getTitulo());
        myViewHolder.tvData.setText(mList.get(position).getData());
        myViewHolder.tvStatus.setText(mList.get(position).getStatus().substring(0,1).toUpperCase().
                    concat(mList.get(position).getStatus().substring(1)));
        GradientDrawable bgShape = (GradientDrawable)myViewHolder.imgStatus.getBackground();
        bgShape.setColor(Color.BLACK);

        if(mList.get(position).getStatus().equals("rejeitado")){
            //myViewHolder.imgStatus.setBackgroundColor(context.getResources().getColor(R.color.primary_yellow));
            bgShape.setColor(Color.RED);
        }else if(mList.get(position).getStatus().equals("aprovado")){
            //myViewHolder.imgStatus.setBackgroundColor(context.getResources().getColor(R.color.primary_red));
            bgShape.setColor(Color.GREEN);
        } else {
            //myViewHolder.imgStatus.setBackgroundColor(context.getResources().getColor(R.color.primary_green));
            bgShape.setColor(Color.YELLOW);
        }
        myViewHolder.imgStatus.setBackground(bgShape);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitulo;
        public TextView tvData;
        public TextView tvStatus;
        public View imgStatus;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitulo = (TextView) itemView.findViewById(R.id.tv_titulo);
            tvData = (TextView) itemView.findViewById(R.id.tv_data);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_status);
            imgStatus = (View) itemView.findViewById(R.id.img_status);

        }
    }
}
