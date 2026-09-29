package com.example.joyeria_yuosam.presentador;

import com.example.joyeria_yuosam.modelo.Joyeria;
import com.example.joyeria_yuosam.modelo.JoyeriaDatabaseHelper;
import com.example.joyeria_yuosam.modelo.ProductoJoya;

import java.util.List;

/**
 * Presentador (Presenter) en la arquitectura MVP.
 * Coordina la comunicación entre la vista y las fuentes de datos (TDA Joyeria y SQLite).
 * 
 * Autor: TDA Joyería YouSam
 */
public class JoyeriaPresenter implements JoyeriaContrato.PresentadorPrincipal {

    private final JoyeriaContrato.VistaPrincipal vista;
    private final Joyeria joyeriaModel;
    private final JoyeriaDatabaseHelper dbHelper;

    public JoyeriaPresenter(JoyeriaContrato.VistaPrincipal vista, JoyeriaDatabaseHelper dbHelper) {
        this.vista = vista;
        this.dbHelper = dbHelper;
        this.joyeriaModel = new Joyeria();
    }

    @Override
    public void cargarJoyeria() {
        try {
            dbHelper.cargarEnJoyeria(joyeriaModel);
            List<ProductoJoya> lista = joyeriaModel.getListaJoyas();
            actualizarVistaConLista(lista, null);
        } catch (Exception e) {
            vista.mostrarError("Error al cargar la base de datos de joyas: " + e.getMessage());
        }
    }

    @Override
    public void buscarJoyas(String query) {
        try {
            List<ProductoJoya> filtrados = joyeriaModel.buscarPorFiltro(query);
            actualizarVistaConLista(filtrados, query);
        } catch (Exception e) {
            vista.mostrarError("Error al filtrar inventario: " + e.getMessage());
        }
    }

    @Override
    public void registrarJoya(ProductoJoya joya) {
        try {
            if (joyeriaModel.buscarJoya(joya.getId()) != null) {
                vista.mostrarError("Ya existe una joya con el código ID " + joya.getId());
                return;
            }

            boolean insertado = dbHelper.insertarJoya(joya);
            if (insertado) {
                joyeriaModel.agregarJoya(joya);
                vista.mostrarMensaje("Joya registrada correctamente en SQLite.");
                cargarJoyeria();
            } else {
                vista.mostrarError("Error al insertar en la base de datos.");
            }
        } catch (Exception e) {
            vista.mostrarError("Error al registrar joya: " + e.getMessage());
        }
    }

    @Override
    public void venderOIncrementarStock(String id, int cantidadAVender) {
        try {
            ProductoJoya joya = joyeriaModel.buscarJoya(id);
            if (joya == null) {
                vista.mostrarError("La joya especificada no existe.");
                return;
            }

            // Usar regla de negocio verificarStock y disminuirStock del TDA ProductoJoya
            if (!joya.verificarStock(cantidadAVender)) {
                vista.mostrarError("No se puede vender esa cantidad. Stock disponible: " + joya.getStock());
                return;
            }

            joya.disminuirStock(cantidadAVender);
            boolean actualizado = dbHelper.actualizarStockJoya(id, joya.getStock());

            if (actualizado) {
                vista.mostrarMensaje("Venta registrada. Stock actualizado a " + joya.getStock() + " unidades.");
                cargarJoyeria();
            } else {
                vista.mostrarError("Error al actualizar el stock en SQLite.");
            }
        } catch (Exception e) {
            vista.mostrarError("Error al procesar la venta: " + e.getMessage());
        }
    }

    @Override
    public void actualizarJoya(ProductoJoya joya) {
        try {
            boolean actualizado = dbHelper.actualizarJoya(joya);
            if (actualizado) {
                vista.mostrarMensaje("Joya actualizada correctamente.");
                cargarJoyeria();
            } else {
                vista.mostrarError("Error al actualizar la joya en la base de datos.");
            }
        } catch (Exception e) {
            vista.mostrarError("Error al actualizar joya: " + e.getMessage());
        }
    }

    @Override
    public void eliminarJoya(String id) {
        try {
            boolean eliminado = dbHelper.eliminarJoya(id);
            if (eliminado) {
                joyeriaModel.eliminarJoya(id);
                vista.mostrarMensaje("Joya eliminada del inventario.");
                cargarJoyeria();
            } else {
                vista.mostrarError("Error al eliminar la joya de la base de datos.");
            }
        } catch (Exception e) {
            vista.mostrarError("Error al eliminar joya: " + e.getMessage());
        }
    }

    private void actualizarVistaConLista(List<ProductoJoya> lista, String queryBusqueda) {
        double valorTotal = joyeriaModel.calcularValorTotalInventario();
        int totalUnidades = joyeriaModel.calcularTotalUnidades();

        vista.actualizarResumen(valorTotal, totalUnidades);
        vista.mostrarJoyas(lista);

        boolean esVacio = lista.isEmpty();
        if (esVacio) {
            String mensaje = (queryBusqueda != null && !queryBusqueda.trim().isEmpty())
                    ? "No se encontraron joyas que coincidan con \"" + queryBusqueda + "\""
                    : "No hay joyas registradas en el inventario.";
            vista.mostrarEstadoVacio(true, mensaje);
        } else {
            vista.mostrarEstadoVacio(false, "");
        }
    }
}
