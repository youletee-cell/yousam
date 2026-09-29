package com.example.joyeria_yuosam.modelo;

import androidx.annotation.NonNull;

/**
 * TDA Elemento: Representa una pieza individual del catálogo de Joyería YouSam.
 * Aplica encapsulamiento estricto y las 8 reglas de negocio especificadas.
 * 
 * Autor: TDA Joyería YouSam
 */
public class ProductoJoya {
    public static final int STOCK_MAXIMO_PERMITIDO = 10;

    private final String id;
    private String nombre;
    private String categoria;
    private String material;
    private double precio;
    private int stock;

    /**
     * 1. Constructor con validaciones de negocio.
     * Validaciones: Campos no vacíos, precio > 0, stock entre 0 y 10.
     */
    public ProductoJoya(String id, String nombre, String categoria, String material, double precio, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID único del producto no puede estar vacío.");
        }
        this.id = id.trim().toUpperCase();
        setNombre(nombre);
        setCategoria(categoria);
        setMaterial(material);
        setPrecio(precio);
        setStock(stock);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre comercial es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    /**
     * 2. getCategoria() / setCategoria(String categoria): Acceso y mutación encapsulada.
     */
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría de la joya no puede estar vacía.");
        }
        this.categoria = categoria.trim();
    }

    /**
     * 3. getMaterial() / setMaterial(String material): Acceso y mutación encapsulada.
     */
    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        if (material == null || material.trim().isEmpty()) {
            throw new IllegalArgumentException("El material de la joya no puede estar vacío.");
        }
        this.material = material.trim();
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (!Double.isFinite(precio) || precio <= 0) {
            throw new IllegalArgumentException("El precio base debe ser estricta y numéricamente mayor a cero.");
        }
        this.precio = precio;
    }

    /**
     * 4. getStock() / setStock(int stock): Acceso y mutación encapsulada con control estricto de stock <= 10.
     */
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0 || stock > STOCK_MAXIMO_PERMITIDO) {
            throw new IllegalArgumentException("El stock debe estar en el rango de 0 a " + STOCK_MAXIMO_PERMITIDO + " unidades.");
        }
        this.stock = stock;
    }

    /**
     * 5. verificarStock(int cantidadRequerida): Retorna un boolean validando si 1 <= cantidadRequerida <= stock.
     */
    public boolean verificarStock(int cantidadRequerida) {
        return cantidadRequerida >= 1 && cantidadRequerida <= this.stock;
    }

    /**
     * 6. disminuirStock(int cantidadVendida): Resta unidades tras venta exitosa (valida con verificarStock).
     */
    public void disminuirStock(int cantidadVendida) {
        if (!verificarStock(cantidadVendida)) {
            throw new IllegalArgumentException("Stock insuficiente o cantidad no válida. Disponibles: " + this.stock);
        }
        setStock(this.stock - cantidadVendida);
    }

    /**
     * 7. calcularPrecioConDescuento(double porcentajeDescuento): Aplica rebaja porcentual sobre el precio base.
     */
    public double calcularPrecioConDescuento(double porcentajeDescuento) {
        if (!Double.isFinite(porcentajeDescuento) || porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0% y 100%.");
        }
        return this.precio * (1.0 - (porcentajeDescuento / 100.0));
    }

    /**
     * 8. calcularCostoAcumuladoRecursivo(int n): Método RECURSIVO que calcula el costo acumulado de N unidades.
     * Caso Base: f(0) = 0
     * Paso Recursivo: f(n) = precio + f(n - 1)
     */
    public double calcularCostoAcumuladoRecursivo(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("La cantidad n no puede ser negativa.");
        }
        if (n == 0) {
            return 0.0; // Caso base
        }
        return this.precio + calcularCostoAcumuladoRecursivo(n - 1);
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductoJoya{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", material='" + material + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                '}';
    }
}
