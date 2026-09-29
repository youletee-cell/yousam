package com.example.joyeria_yuosam.vista;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joyeria_yuosam.R;
import com.example.joyeria_yuosam.databinding.ItemProductoBinding;
import com.example.joyeria_yuosam.modelo.ProductoJoya;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador RecyclerView para el catálogo de joyas.
 * Maneja iconos por categoría, insignias de estado de stock e interacciones de usuario.
 * 
 * Autor: TDA Joyería YouSam
 */
public class JoyaAdapter extends RecyclerView.Adapter<JoyaAdapter.JoyaViewHolder> {

    public interface OnJoyaActionListener {
        void onVender(ProductoJoya joya);
        void onEditar(ProductoJoya joya);
        void onEliminar(ProductoJoya joya);
    }

    private final List<ProductoJoya> listaJoyas;
    private final OnJoyaActionListener listener;

    public JoyaAdapter(List<ProductoJoya> listaJoyas, OnJoyaActionListener listener) {
        this.listaJoyas = new ArrayList<>(listaJoyas != null ? listaJoyas : new ArrayList<>());
        this.listener = listener;
    }

    public void actualizarLista(List<ProductoJoya> nuevaLista) {
        this.listaJoyas.clear();
        if (nuevaLista != null) {
            this.listaJoyas.addAll(nuevaLista);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JoyaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductoBinding binding = ItemProductoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new JoyaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JoyaViewHolder holder, int position) {
        holder.bind(listaJoyas.get(position));
    }

    @Override
    public int getItemCount() {
        return listaJoyas.size();
    }

    class JoyaViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductoBinding binding;

        public JoyaViewHolder(@NonNull ItemProductoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ProductoJoya joya) {
            Context context = itemView.getContext();

            // Datos principales
            binding.tvJoyaId.setText(joya.getId());
            binding.tvJoyaNombre.setText(joya.getNombre());
            binding.tvJoyaCategoria.setText(joya.getCategoria());
            binding.tvJoyaMaterial.setText(String.format("Material: %s", joya.getMaterial()));

            // Icono dinámico según categoría
            binding.tvIconoCategoria.setText(obtenerIconoCategoria(joya.getCategoria()));

            // Stock Badge (Verde si > 2, Rojo/Rosa si <= 2)
            int stock = joya.getStock();
            binding.tvStockBadge.setText(String.format(Locale.getDefault(), "Stock: %d / %d", stock, ProductoJoya.STOCK_MAXIMO_PERMITIDO));
            
            if (stock <= 2) {
                binding.tvStockBadge.setBackgroundResource(R.color.stock_low_bg);
                binding.tvStockBadge.setTextColor(ContextCompat.getColor(context, R.color.stock_low));
            } else {
                binding.tvStockBadge.setBackgroundResource(R.color.stock_normal_bg);
                binding.tvStockBadge.setTextColor(ContextCompat.getColor(context, R.color.stock_normal));
            }

            // Precios
            double precio = joya.getPrecio();
            double subtotalAcumulado = joya.calcularCostoAcumuladoRecursivo(stock);

            binding.tvJoyaPrecio.setText(String.format(Locale.getDefault(), "$%,.2f", precio));
            binding.tvJoyaSubtotal.setText(String.format(Locale.getDefault(), "Total inv: $%,.2f", subtotalAcumulado));

            // Acciones
            binding.btnVenderJoya.setOnClickListener(v -> {
                if (listener != null) listener.onVender(joya);
            });

            binding.btnEditarJoya.setOnClickListener(v -> {
                if (listener != null) listener.onEditar(joya);
            });

            binding.btnEliminarJoya.setOnClickListener(v -> {
                if (listener != null) listener.onEliminar(joya);
            });
        }

        private String obtenerIconoCategoria(String cat) {
            if (cat == null) return "💎";
            switch (cat.toLowerCase().trim()) {
                case "anillo": return "💍";
                case "collar": return "📿";
                case "pulsera": return "💎";
                case "aretes": return "✨";
                case "reloj": return "⌚";
                case "dije": return "✝️";
                case "tobillera": return "🩰";
                case "broche": return "🌸";
                case "gemelos": return "🧷";
                case "juego de joyas": return "👑";
                default: return "💎";
            }
        }
    }
}
