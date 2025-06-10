package sistemagastospersonales;

import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SistemaGastosPersonales extends JFrame {

    /* 1. Agregar
       2. Modificar
       3. Eliminar (todo o selección)
       4. Mostrar reporte
       5. Mostrar reporte por categoria (gastos, ingresos) */
    // Componentes
    private JTextField descripcionField; //
    private JTextField montoField;
    private JDateChooser fechaChooser;
    private JTextField saldoInicialField;
    private JComboBox<String> tipoCombo;
    private JTextArea reporteArea;
    private JLabel saldoLabel, alertaLabel;
    private HistogramaPanel histogramaPanel;

    // Arreglo y variables
    private Transaccion[] transacciones;
    private int contador;
    private double saldoInicial;

    public SistemaGastosPersonales() {
        super("Sistema de Gastos Personales");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Inicializar arreglo y contador
        transacciones = new Transaccion[100];
        contador = 0;

        // Panel entrada datos 
        JPanel panelEntrada = new JPanel(new GridLayout(7, 3, 6, 6)); // Tamaño del espacio donde se encuentran los Textfield, Jlabel, etc
        panelEntrada.setBorder(BorderFactory.createTitledBorder("Registrar Transacción"));

        panelEntrada.add(new JLabel("Saldo Inicial:"));
        saldoInicialField = new JTextField(" ");
        panelEntrada.add(saldoInicialField);
        panelEntrada.add(new JLabel("Saldo Inicial:"));
        saldoInicialField = new JTextField(" ");
        panelEntrada.add(saldoInicialField);

        panelEntrada.add(new JLabel("Descripción:"));
        descripcionField = new JTextField();
        panelEntrada.add(descripcionField);

        panelEntrada.add(new JLabel("Monto:"));
        montoField = new JTextField();
        panelEntrada.add(montoField);

        panelEntrada.add(new JLabel("Tipo:"));
        tipoCombo = new JComboBox<>(new String[]{"Ingreso", "Gasto"});
        panelEntrada.add(tipoCombo);

        panelEntrada.add(new JLabel("Fecha:"));
        fechaChooser = new JDateChooser();
        fechaChooser.setDateFormatString("dd/MM/yyyy");
        panelEntrada.add(fechaChooser);

        JButton agregarButton = new JButton("Agregar Transacción");
        panelEntrada.add(agregarButton);

        alertaLabel = new JLabel("");
        alertaLabel.setForeground(Color.RED);
        panelEntrada.add(alertaLabel);

        add(panelEntrada, BorderLayout.NORTH);

        // Área de reporte
        reporteArea = new JTextArea();
        reporteArea.setEditable(false);
        JScrollPane scrollReporte = new JScrollPane(reporteArea);
        scrollReporte.setBorder(BorderFactory.createTitledBorder("Reporte de Transacciones"));

        // Panel saldo y botones
        JPanel panelAbajo = new JPanel(new FlowLayout());

        saldoLabel = new JLabel("Saldo: Lps.0.0 ");
        panelAbajo.add(saldoLabel);

        JButton reporteButton = new JButton("Mostrar Reporte");
        panelAbajo.add(reporteButton);

        JButton histogramaButton = new JButton("Mostrar Histograma");
        panelAbajo.add(histogramaButton);

        /*JButton eliminarButton = new JButton("Eliminar");
        panelAbajo.add(eliminarButton); */ //Tengo que terminar de configurarlo
        add(panelAbajo, BorderLayout.SOUTH);

        // Panel para el histograma gráfico
        histogramaPanel = new HistogramaPanel();
        histogramaPanel.setPreferredSize(new Dimension(680, 200));
        histogramaPanel.setBorder(BorderFactory.createTitledBorder("Histograma de Transacciones"));
        add(histogramaPanel, BorderLayout.CENTER);

        // Acción botón agregar
        agregarButton.addActionListener(e -> {
            try {
                if (contador == 0) {
                    saldoInicial = Double.parseDouble(saldoInicialField.getText());
                    saldoInicialField.setEditable(false);
                }

                String desc = descripcionField.getText();
                double monto = Double.parseDouble(montoField.getText());
                String tipo = (String) tipoCombo.getSelectedItem();

                Date fechaSeleccionada = fechaChooser.getDate();
                String fecha = "";
                if (fechaSeleccionada != null) {
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    fecha = formato.format(fechaSeleccionada);
                } else {
                    JOptionPane.showMessageDialog(this, "Debes seleccionar una fecha.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Transaccion t = new Transaccion(desc, monto, tipo, fecha);
                if (contador < transacciones.length) {
                    transacciones[contador++] = t;
                    actualizarSaldo();
                    reporteArea.append(t + "\n");
                    descripcionField.setText("");
                    montoField.setText("");
                    fechaChooser.setDate(null); // Limpia el calendario
                    alertaLabel.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pueden agregar más transacciones.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Monto o saldo inicial inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción botón reporte
        reporteButton.addActionListener(e -> mostrarReporte());

        // Acción botón histograma (pinta el panel)
        histogramaButton.addActionListener(e -> {
            histogramaPanel.repaint();
        });

        setVisible(true);
    }

    private void actualizarSaldo() {
        double saldo = saldoInicial;
        for (int i = 0; i < contador; i++) {
            if (transacciones[i].getTipo().equalsIgnoreCase("Ingreso")) {
                saldo += transacciones[i].getMonto();
            } else {
                saldo -= transacciones[i].getMonto();
            }
        }
        saldoInicialField.setText(String.format(" Lps. %.2f", saldo)); // actualiza en el Textfield
        saldoLabel.setText("Saldo: Lps. " + String.format("%.2f", saldo));
        if (saldo <= 0) {
            alertaLabel.setText("¡Alerta! Presupuesto agotado o saldo negativo.");
        } else {
            alertaLabel.setText(" ");
        }
    }

    private void mostrarReporte() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("Reporte de Transacciones:\n");
        for (int i = 0; i < contador; i++) {
            reporte.append(transacciones[i]).append("\n");
        }
        reporte.append("\nSaldo disponible: Lps. ").append(String.format("%.2f", calcularSaldo()));
        reporteArea.setText(reporte.toString());
    }

    private double calcularSaldo() {
        double saldo = saldoInicial;
        for (int i = 0; i < contador; i++) {
            if (transacciones[i].getTipo().equalsIgnoreCase("Ingreso")) {
                saldo += transacciones[i].getMonto();
            } else {
                saldo -= transacciones[i].getMonto();
            }
        }
        return saldo;
    }

    // Panel personalizado para dibujar histograma
    private class HistogramaPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Contar ingresos y gastos
            int ingresos = 0, gastos = 0;
            double totalIngresos = 0, totalGastos = 0;
            for (int i = 0; i < contador; i++) {
                if (transacciones[i].getTipo().equalsIgnoreCase("Ingreso")) {
                    ingresos++;
                    totalIngresos += transacciones[i].getMonto();
                } else {
                    gastos++;
                    totalGastos += transacciones[i].getMonto();
                }
            }

            // Dibujar barras proporcionales al total monto
            int anchoBarra = 100;
            int alturaMax = 150;
            int xIngresos = 100;
            int xGastos = 300;
            int yBase = getHeight() - 50;

            g.setColor(Color.BLUE);
            int alturaIngresos = (int) ((totalIngresos / Math.max(totalIngresos, totalGastos)) * alturaMax);
            g.fillRect(xIngresos, yBase - alturaIngresos, anchoBarra, alturaIngresos);
            g.drawString("Ingresos: Lps." + String.format("%.2f", totalIngresos), xIngresos, yBase + 20);

            g.setColor(Color.RED);
            int alturaGastos = (int) ((totalGastos / Math.max(totalIngresos, totalGastos)) * alturaMax);
            g.fillRect(xGastos, yBase - alturaGastos, anchoBarra, alturaGastos);
            g.drawString("Gastos: Lps." + String.format("%.2f", totalGastos), xGastos, yBase + 20);

            // Si no hay datos, mostrar mensaje
            if (contador == 0) {
                g.setColor(Color.BLACK);
                g.drawString("No hay transacciones para mostrar histograma", 150, yBase - 100);
            }
        }
    }

    // Clase interna para Transacción
    private class Transaccion {

        private String descripcion;
        private double monto;
        private String tipo;
        private String fecha;

        public Transaccion(String descripcion, double monto, String tipo, String fecha) {
            this.descripcion = descripcion;
            this.monto = monto;
            this.tipo = tipo;
            this.fecha = fecha;
        }

        public String getTipo() {
            return tipo;
        }

        public double getMonto() {
            return monto;
        }

        @Override
        public String toString() {
            return "[" + fecha + "] " + tipo + ": " + descripcion + " - $" + String.format("%.2f", monto);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaGastosPersonales());
    }
}
