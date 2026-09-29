package com.example.joyeria_yuosam.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * TDA Contenedor: Administra la colección en memoria del inventario de Joyería YouSam.
 * 
 * Autor: TDA Joyería YouSam
 */
public class Joyeria {
    private final List<ProductoJoya> listaJoyas;

    public Joyeria() {
        this.listaJoyas = new ArrayList<>();
    }

    public List<ProductoJoya> getListaJoyas() {
        return new ArrayList<>(listaJoyas);
    }

    public void setListaJoyas(List<ProductoJoya> nuevasJoyas) {
        this.listaJoyas.clear();
        if (nuevasJoyas != null) {
            this.listaJoyas.addAll(nuevasJoyas);
        }
    }

    /**
     * Agrega una nueva joya a la colección asegurando unicidad de ID.
     */
    public boolean agregarJoya(ProductoJoya joya) {
        if (joya == null) {
            throw new IllegalArgumentException("La joya no puede ser nula.");
        }
        if (buscarJoya(joya.getId()) != null) {
            throw new IllegalArgumentException("Ya existe una joya registrada con el código " + joya.getId());
        }
        return this.listaJoyas.add(joya);
    }

    /**
     * Elimina una joya por su ID único.
     */
    public boolean eliminarJoya(String id) {
        if (id == null) return false;
        ProductoJoya joya = buscarJoya(id);
        if (joya != null) {
            return this.listaJoyas.remove(joya);
        }
        return false;
    }

    /**
     * Busca una joya por su ID exacto.
     */
    public ProductoJoya buscarJoya(String id) {
        if (id == null || id.trim().isEmpty()) return null;
        String idNormalizado = id.trim().toUpperCase();
        for (ProductoJoya j : listaJoyas) {
            if (j.getId().equalsIgnoreCase(idNormalizado)) {
                return j;
            }
        }
        return null;
    }

    /**
     * Filtra la colección reactivamente por ID, nombre, categoría o material.
     */
    public List<ProductoJoya> buscarPorFiltro(String consulta) {
        if (consulta == null || consulta.trim().isEmpty()) {
            return getListaJoyas();
        }
        String q = consulta.trim().toLowerCase();
        List<ProductoJoya> resultado = new ArrayList<>();
        for (ProductoJoya j : listaJoyas) {
            if (j.getId().toLowerCase().contains(q)
                    || j.getNombre().toLowerCase().contains(q)
                    || j.getCategoria().toLowerCase().contains(q)
                    || j.getMaterial().toLowerCase().contains(q)) {
                resultado.add(j);
            }
        }
        return resultado;
    }

    /**
     * Calcula el valor total del inventario ($) sumando el costo acumulado recursivo de cada joya.
     */
    public double calcularValorTotalInventario() {
        double total = 0.0;
        for (ProductoJoya j : listaJoyas) {
            total += j.calcularCostoAcumuladoRecursivo(j.getStock());
        }
        return total;
    }

    /**
     * Calcula la cantidad total de unidades físicas disponibles en stock.
     */
    public int calcularTotalUnidades() {
        int total = 0;
        for (ProductoJoya j : listaJoyas) {
            total += j.getStock();
        }
        return total;
    }

    /**
     * Vacía la colección en memoria.
     */
    public void limpiar() {
        this.listaJoyas.clear();
    }
}
