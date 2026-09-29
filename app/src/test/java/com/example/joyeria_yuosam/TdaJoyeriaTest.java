package com.example.joyeria_yuosam;

import com.example.joyeria_yuosam.modelo.Joyeria;
import com.example.joyeria_yuosam.modelo.ProductoJoya;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Pruebas unitarias para validar las 8 reglas del TDA ProductoJoya y las operaciones del TDA Joyeria.
 * 
 * Autor: TDA Joyería YouSam
 */
public class TdaJoyeriaTest {

    private ProductoJoya joyaTest;
    private Joyeria joyeriaTest;

    @Before
    public void setUp() {
        joyaTest = new ProductoJoya("JOY-001", "Anillo Solitario Imperial", "Anillo", "Oro 24K", 3500.00, 8);
        joyeriaTest = new Joyeria();
        joyeriaTest.agregarJoya(joyaTest);
    }

    @Test
    public void testAtributosYEncapsulamiento() {
        assertEquals("JOY-001", joyaTest.getId());
        assertEquals("Anillo Solitario Imperial", joyaTest.getNombre());
        assertEquals("Anillo", joyaTest.getCategoria());
        assertEquals("Oro 24K", joyaTest.getMaterial());
        assertEquals(3500.00, joyaTest.getPrecio(), 0.001);
        assertEquals(8, joyaTest.getStock());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidacionStockMaximo() {
        // No debe permitir stock > 10
        joyaTest.setStock(11);
    }

    @Test
    public void testVerificarYDisminuirStock() {
        assertTrue(joyaTest.verificarStock(5));
        joyaTest.disminuirStock(3);
        assertEquals(5, joyaTest.getStock());

        // Verificar que no permite vender más del stock disponible
        assertFalse(joyaTest.verificarStock(6));
    }

    @Test
    public void testCalcularPrecioConDescuento() {
        // 20% descuento en $3,500.00 = $2,800.00
        double precioDescuento = joyaTest.calcularPrecioConDescuento(20.0);
        assertEquals(2800.00, precioDescuento, 0.001);
    }

    @Test
    public void testMetodoRecursivo() {
        // f(0) = 0
        assertEquals(0.0, joyaTest.calcularCostoAcumuladoRecursivo(0), 0.001);
        // f(1) = 3500.00
        assertEquals(3500.00, joyaTest.calcularCostoAcumuladoRecursivo(1), 0.001);
        // f(4) = 4 * 3500.00 = 14,000.00
        assertEquals(14000.00, joyaTest.calcularCostoAcumuladoRecursivo(4), 0.001);
    }

    @Test
    public void testContenedorJoyeria() {
        ProductoJoya joya2 = new ProductoJoya("JOY-002", "Collar Esmeralda", "Collar", "Platino", 4800.00, 2);
        joyeriaTest.agregarJoya(joya2);

        assertEquals(2, joyeriaTest.getListaJoyas().size());

        // Filtrado
        List<ProductoJoya> filtrados = joyeriaTest.buscarPorFiltro("esmeralda");
        assertEquals(1, filtrados.size());
        assertEquals("JOY-002", filtrados.get(0).getId());

        // Valor total del inventario: (8 * 3500) + (2 * 4800) = 28000 + 9600 = 37600
        assertEquals(37600.00, joyeriaTest.calcularValorTotalInventario(), 0.001);
        assertEquals(10, joyeriaTest.calcularTotalUnidades());
    }
}
