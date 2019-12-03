package tefor.chat.adaptador;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tefor.chat.R;

public class AdaptadorDetalleMensajes extends RecyclerView.Adapter<AdaptadorDetalleMensajes.ViewHolder> {

    private List<String> lista;

    public AdaptadorDetalleMensajes(List<String> lista) {
        this.lista = lista;
    }

    @Override
    public AdaptadorDetalleMensajes.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_detalle_mensajes, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdaptadorDetalleMensajes.ViewHolder holder, int posicion) {
        String mensaje = lista.get(posicion);

        holder.mensajeTextView.setText(mensaje);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mensajeTextView;

        ViewHolder(View itemView) {
            super(itemView);

            mensajeTextView = (TextView)
                    itemView.findViewById(R.id.layoutDetalleMensajes_mensajeTextView);
        }

    }

}
