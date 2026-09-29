package com.example.joyeria_yuosam.presentador;

import com.example.joyeria_yuosam.modelo.ProductoJoya;

import java.util.List;

/**
 * Contrato MVP que define la interacción entre la Vista Principal y el Presentador.
 * 
 * Autor: TDA Joyería YouSam
 */
public interface JoyeriaContrato {

    interface VistaPrincipal {
        void mostrarJoyas(List<ProductoJoya> lista);
        void actualizarResumen(double valorTotal, int totalUnidades);
        void mostrarMensaje(String mensaje);
        void mostrarError(String error);
        void mostrarEstadoVacio(boolean vacio, String mensaje);
    }

    interface PresentadorPrincipal {
        void cargarJoyeria();
        void buscarJoyas(String query);
        void registrarJoya(ProductoJoya joya);
        void venderOIncrementarStock(String id, int cantidadAVender);
        void actualizarJoya(ProductoJoya joya);
        void eliminarJoya(String id);
    }
}
