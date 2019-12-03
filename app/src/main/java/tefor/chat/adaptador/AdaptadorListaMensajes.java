package tefor.chat.adaptador;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tefor.chat.R;
import tefor.chat.dto.Usuario;

public class AdaptadorListaMensajes extends RecyclerView.Adapter<AdaptadorListaMensajes.ViewHolder> {

    private OnItemTouchListener onItemTouchListener;
    private List<Usuario> lista;

    public AdaptadorListaMensajes() {
        this.lista = ModeloMensajes.getOrCreate().getUsuarios();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lista_mensajes,
                parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int posicion) {
        ModeloMensajes modelo = ModeloMensajes.getOrCreate();

        final Usuario usuario = lista.get(posicion);
        String mensaje = modelo.getMensajes().get(posicion);

        holder.nombreTextView.setText(usuario.getNick());
        holder.ultimoMensajeTextView.setText(mensaje);

        holder.nombreTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onItemTouchListener.onItemClick(usuario, posicion);
            }

        });
        holder.ultimoMensajeTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onItemTouchListener.onItemClick(usuario, posicion);
            }

        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // <editor-fold defaultstate="collapsed" desc="setters">

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }

    // </editor-fold>

    public interface OnItemTouchListener {

        void onItemClick(Usuario usuario, int posicion);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombreTextView, ultimoMensajeTextView;

        ViewHolder(View itemView) {
            super(itemView);

            nombreTextView = (TextView) itemView.findViewById(R.id.rowLayout_nombreTextView);
            ultimoMensajeTextView =
                    (TextView) itemView.findViewById(R.id.rowLayout_ultimoMensajeTextView);
        }

    }

    public static class ModeloMensajes {

        private static ModeloMensajes instancia;
        private List<Usuario> usuarios;
        private List<String> mensajes;

        public static ModeloMensajes getOrCreate() {
            if (instancia == null) {
                instancia = new ModeloMensajes();
            }

            return instancia;
        }

        private ModeloMensajes() {
        }

        // <editor-fold defaultstate="collapsed" desc="getters y setters">

        public List<Usuario> getUsuarios() {
            return usuarios;
        }

        public ModeloMensajes setUsuarios(List<Usuario> usuarios) {
            this.usuarios = usuarios;
            return this;
        }

        public List<String> getMensajes() {
            return mensajes;
        }

        public ModeloMensajes setMensajes(List<String> mensajes) {
            this.mensajes = mensajes;
            return this;
        }

        // </editor-fold>

    }

}
