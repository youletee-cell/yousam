package com.example.joyeria_yuosam.vista;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.joyeria_yuosam.R;
import com.example.joyeria_yuosam.databinding.ActivityMainBinding;
import com.example.joyeria_yuosam.modelo.JoyeriaDatabaseHelper;
import com.example.joyeria_yuosam.modelo.ProductoJoya;
import com.example.joyeria_yuosam.presentador.JoyeriaContrato;
import com.example.joyeria_yuosam.presentador.JoyeriaPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pantalla Principal que implementa la Vista en la arquitectura MVP (JoyeriaContrato.VistaPrincipal).
 * 
 * Autor: TDA Joyería YouSam
 */
public class MainActivity extends AppCompatActivity implements JoyeriaContrato.VistaPrincipal {

    private ActivityMainBinding binding;
    private JoyeriaContrato.PresentadorPrincipal presenter;
    private JoyaAdapter joyaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();

        // Inicializar Presentador con Base de Datos SQLite
        JoyeriaDatabaseHelper dbHelper = new JoyeriaDatabaseHelper(this);
        presenter = new JoyeriaPresenter(this, dbHelper);

        // Cargar Datos Iniciales
        presenter.cargarJoyeria();
    }

    private void setupUI() {
        // RecyclerView Setup
        joyaAdapter = new JoyaAdapter(new ArrayList<>(), new JoyaAdapter.OnJoyaActionListener() {
            @Override
            public void onVender(ProductoJoya joya) {
                mostrarDialogoVenta(joya);
            }

            @Override
            public void onEditar(ProductoJoya joya) {
                EditarJoyaDialog.mostrar(MainActivity.this, joya, joyaEditada -> {
                    presenter.actualizarJoya(joyaEditada);
                });
            }

            @Override
            public void onEliminar(ProductoJoya joya) {
                mostrarDialogoConfirmarEliminar(joya);
            }
        });

        binding.rvJoyas.setLayoutManager(new LinearLayoutManager(this));
        binding.rvJoyas.setAdapter(joyaAdapter);

        // Búsqueda Reactiva
        binding.etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.buscarJoyas(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // FAB Agregar Joya
        binding.fabAgregarJoya.setOnClickListener(v -> {
            AgregarJoyaDialog.mostrar(this, joya -> {
                presenter.registrarJoya(joya);
            });
        });
    }

    private void mostrarDialogoVenta(ProductoJoya joya) {
        if (joya.getStock() <= 0) {
            mostrarError("No hay stock disponible para vender esta joya.");
            return;
        }

        final EditText etCantidad = new EditText(this);
        etCantidad.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etCantidad.setText("1");
        etCantidad.setTextColor(getResources().getColor(R.color.dialog_text_black, getTheme()));
        etCantidad.setPadding(40, 20, 40, 20);

        new AlertDialog.Builder(this)
                .setTitle("Vender Joya: " + joya.getNombre())
                .setMessage("Ingrese la cantidad a vender (Stock actual: " + joya.getStock() + "):")
                .setView(etCantidad)
                .setPositiveButton("Vender", (dialog, which) -> {
                    String cantStr = etCantidad.getText().toString().trim();
                    try {
                        int cantidad = Integer.parseInt(cantStr);
                        presenter.venderOIncrementarStock(joya.getId(), cantidad);
                    } catch (NumberFormatException e) {
                        mostrarError("Cantidad inválida.");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoConfirmarEliminar(ProductoJoya joya) {
        String mensaje = getString(R.string.confirm_delete_message, joya.getId() + " - " + joya.getNombre());
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(mensaje)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    presenter.eliminarJoya(joya.getId());
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    // --- MÉTODOS DE LA INTERFAZ VISTA PRINCIPAL (MVP) ---

    @Override
    public void mostrarJoyas(List<ProductoJoya> lista) {
        joyaAdapter.actualizarLista(lista);
    }

    @Override
    public void actualizarResumen(double valorTotal, int totalUnidades) {
        binding.tvValorTotal.setText(String.format(Locale.getDefault(), "$%,.2f", valorTotal));
        binding.tvTotalItems.setText(String.format(Locale.getDefault(), "%d Unids", totalUnidades));
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void mostrarError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void mostrarEstadoVacio(boolean vacio, String mensaje) {
        if (vacio) {
            binding.containerEstadoVacio.setVisibility(View.VISIBLE);
            binding.tvMensajeVacio.setText(mensaje);
            binding.rvJoyas.setVisibility(View.GONE);
        } else {
            binding.containerEstadoVacio.setVisibility(View.GONE);
            binding.rvJoyas.setVisibility(View.VISIBLE);
        }
    }
}
