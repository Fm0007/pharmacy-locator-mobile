package com.mehdi.pharmacie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PharmacieAdapter  extends RecyclerView.Adapter<PharmacieAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView nomTextView;
        public TextView zoneTextView;
        public TextView adresseTextView;
        public TextView telephoneTextView;
        public TextView etatTextView;



        public ViewHolder(View itemView) {
            super(itemView);

            nomTextView = (TextView) itemView.findViewById(R.id.nom);
            zoneTextView = (TextView) itemView.findViewById(R.id.zone);
            adresseTextView = (TextView) itemView.findViewById(R.id.adresse);
            telephoneTextView = (TextView) itemView.findViewById(R.id.telephone);
            etatTextView = (TextView) itemView.findViewById(R.id.etat);


            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    System.out.println("hhhhh");

                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });

        }

    }

    /*   *** *** Fin ViewHolder *** ***  */



    private Context mContext;
    private List<Pharmacie> mPharmacies;

    private OnItemClickListener mListener;

    public PharmacieAdapter(Context mContext, List<Pharmacie> mPharmacies) {
        this.mContext = mContext;
        this.mPharmacies = mPharmacies;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        // Get the data model based on position
        Pharmacie pharmacie = mPharmacies.get(position);

        // Set item views based on your views and data model
        TextView nom = viewHolder.nomTextView;
        nom.setText(pharmacie.getNom());

        TextView zone = viewHolder.zoneTextView;
        zone.setText(pharmacie.getZone());

        TextView adresse = viewHolder.adresseTextView;
        adresse.setText(pharmacie.getAdresse());

        TextView telephone = viewHolder.telephoneTextView;
        telephone.setText(pharmacie.getTelephone());

        TextView etat = viewHolder.etatTextView;
        etat.setText(pharmacie.getEtat());


    }

    @Override
    public int getItemCount() {
        return mPharmacies.size();
    }

    public void clear(){
       int size =  mPharmacies.size();

       if(size >0){
           for(int i=0; i< size;i++){
               mPharmacies.remove(0);
           }

           this.notifyItemRangeChanged(0,size);
       }
    }

}