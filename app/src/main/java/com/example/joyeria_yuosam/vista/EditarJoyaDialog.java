package com.example.joyeria_yuosam.vista;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.joyeria_yuosam.R;
import com.example.joyeria_yuosam.databinding.DialogEditarJoyaBinding;
import com.example.joyeria_yuosam.modelo.ProductoJoya;

import java.util.Locale;

/**
 * Diálogo modal para editar una joya existente.
 * 
 * Autor: TDA Joyería YouSam
 */
public class EditarJoyaDialog {

    public interface OnJoyaActualizadaListener {
        void onJoyaEditada(ProductoJoya joyaEditada);
    }

    private static final String[] CATEGORIAS = new String[]{
            "Anillo", "Collar", "Pulsera", "Aretes", "Dije", "Reloj", "Tobillera", "Broche", "Gemelos", "Juego de Joyas"
    };

    private static final String[] MATERIALES = new String[]{
            "Oro 24K", "Oro Rosa", "Plata .925", "Platino", "Diamante", "Esmeralda", "Perla", "Baño de Oro", "Oro Blanco"
    };

    public static void mostrar(Context context, ProductoJoya joyaExistente, OnJoyaActualizadaListener listener) {
        if (joyaExistente == null) return;

        DialogEditarJoyaBinding binding = DialogEditarJoyaBinding.inflate(LayoutInflater.from(context));

        // Adaptadores para Dropdowns
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, CATEGORIAS);
        binding.actvEditCategoria.setAdapter(adapterCat);

        ArrayAdapter<String> adapterMat = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, MATERIALES);
        binding.actvEditMaterial.setAdapter(adapterMat);

        // Precargar Datos
        binding.etEditId.setText(joyaExistente.getId());
        binding.etEditNombre.setText(joyaExistente.getNombre());
        binding.actvEditCategoria.setText(joyaExistente.getCategoria(), false);
        binding.actvEditMaterial.setText(joyaExistente.getMaterial(), false);
        binding.etEditPrecio.setText(String.format(Locale.US, "%.2f", joyaExistente.getPrecio()));
        binding.etEditStock.setText(String.valueOf(joyaExistente.getStock()));

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(binding.getRoot())
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        binding.btnCancelEdit.setOnClickListener(v -> dialog.dismiss());

        binding.btnSaveEdit.setOnClickListener(v -> {
            String nombre = binding.etEditNombre.getText() != null ? binding.etEditNombre.getText().toString().trim() : "";
            String categoria = binding.actvEditCategoria.getText() != null ? binding.actvEditCategoria.getText().toString().trim() : "";
            String material = binding.actvEditMaterial.getText() != null ? binding.actvEditMaterial.getText().toString().trim() : "";
            String precioStr = binding.etEditPrecio.getText() != null ? binding.etEditPrecio.getText().toString().trim() : "";
            String stockStr = binding.etEditStock.getText() != null ? binding.etEditStock.getText().toString().trim() : "";

            if (nombre.isEmpty()) {
                binding.etEditNombre.setError(context.getString(R.string.error_invalid_input));
                return;
            }

            double precio;
            try {
                precio = Double.parseDouble(precioStr);
                if (precio <= 0) {
                    binding.etEditPrecio.setError(context.getString(R.string.error_invalid_price));
                    return;
                }
            } catch (NumberFormatException e) {
                binding.etEditPrecio.setError(context.getString(R.string.error_invalid_price));
                return;
            }

            int stock;
            try {
                stock = Integer.parseInt(stockStr);
                if (stock < 0 || stock > ProductoJoya.STOCK_MAXIMO_PERMITIDO) {
                    binding.etEditStock.setError(context.getString(R.string.error_invalid_stock));
                    return;
                }
            } catch (NumberFormatException e) {
                binding.etEditStock.setError(context.getString(R.string.error_invalid_stock));
                return;
            }

            try {
                ProductoJoya joyaEditada = new ProductoJoya(joyaExistente.getId(), nombre, categoria, material, precio, stock);
                if (listener != null) {
                    listener.onJoyaEditada(joyaEditada);
                }
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }
}
