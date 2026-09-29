package com.example.joyeria_yuosam.vista;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.joyeria_yuosam.R;
import com.example.joyeria_yuosam.databinding.DialogAgregarJoyaBinding;
import com.example.joyeria_yuosam.modelo.ProductoJoya;

/**
 * Diálogo modal para registrar una nueva joya con selectores e insumos validados.
 * 
 * Autor: TDA Joyería YouSam
 */
public class AgregarJoyaDialog {

    public interface OnJoyaGuardadaListener {
        void onJoyaCreada(ProductoJoya joya);
    }

    private static final String[] CATEGORIAS = new String[]{
            "Anillo", "Collar", "Pulsera", "Aretes", "Dije", "Reloj", "Tobillera", "Broche", "Gemelos", "Juego de Joyas"
    };

    private static final String[] MATERIALES = new String[]{
            "Oro 24K", "Oro Rosa", "Plata .925", "Platino", "Diamante", "Esmeralda", "Perla", "Baño de Oro", "Oro Blanco"
    };

    public static void mostrar(Context context, OnJoyaGuardadaListener listener) {
        DialogAgregarJoyaBinding binding = DialogAgregarJoyaBinding.inflate(LayoutInflater.from(context));

        // Adaptadores para Dropdowns
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, CATEGORIAS);
        binding.actvAddCategoria.setAdapter(adapterCat);
        binding.actvAddCategoria.setText(CATEGORIAS[0], false);

        ArrayAdapter<String> adapterMat = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, MATERIALES);
        binding.actvAddMaterial.setAdapter(adapterMat);
        binding.actvAddMaterial.setText(MATERIALES[0], false);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(binding.getRoot())
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        binding.btnCancelAdd.setOnClickListener(v -> dialog.dismiss());

        binding.btnSaveAdd.setOnClickListener(v -> {
            String id = binding.etAddId.getText() != null ? binding.etAddId.getText().toString().trim() : "";
            String nombre = binding.etAddNombre.getText() != null ? binding.etAddNombre.getText().toString().trim() : "";
            String categoria = binding.actvAddCategoria.getText() != null ? binding.actvAddCategoria.getText().toString().trim() : "";
            String material = binding.actvAddMaterial.getText() != null ? binding.actvAddMaterial.getText().toString().trim() : "";
            String precioStr = binding.etAddPrecio.getText() != null ? binding.etAddPrecio.getText().toString().trim() : "";
            String stockStr = binding.etAddStock.getText() != null ? binding.etAddStock.getText().toString().trim() : "";

            if (id.isEmpty()) {
                binding.etAddId.setError(context.getString(R.string.error_invalid_input));
                return;
            }
            if (nombre.isEmpty()) {
                binding.etAddNombre.setError(context.getString(R.string.error_invalid_input));
                return;
            }

            double precio;
            try {
                precio = Double.parseDouble(precioStr);
                if (precio <= 0) {
                    binding.etAddPrecio.setError(context.getString(R.string.error_invalid_price));
                    return;
                }
            } catch (NumberFormatException e) {
                binding.etAddPrecio.setError(context.getString(R.string.error_invalid_price));
                return;
            }

            int stock;
            try {
                stock = Integer.parseInt(stockStr);
                if (stock < 0 || stock > ProductoJoya.STOCK_MAXIMO_PERMITIDO) {
                    binding.etAddStock.setError(context.getString(R.string.error_invalid_stock));
                    return;
                }
            } catch (NumberFormatException e) {
                binding.etAddStock.setError(context.getString(R.string.error_invalid_stock));
                return;
            }

            try {
                ProductoJoya nuevaJoya = new ProductoJoya(id, nombre, categoria, material, precio, stock);
                if (listener != null) {
                    listener.onJoyaCreada(nuevaJoya);
                }
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }
}
