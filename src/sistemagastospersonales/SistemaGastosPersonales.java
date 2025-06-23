package sistemagastospersonales;

import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SistemaGastosPersonales extends JFrame {

    // Componentes
    private JTextField descripcionField;
    private JTextField montoField;
    private JDateChooser fechaChooser;
    private JTextField saldoInicialField;
    private JComboBox<String> tipoCombo;
    private JTextArea reporteArea;
    private JLabel saldoLabel;
    private JLabel alertaLabel;
    private HistogramaPanel histogramaPanel;

    // Arreglo y variables
    private Transaccion[] transacciones;
    private int contador;
    private double saldoInicial;
    private double saldoactual;
    private int idContador = 1;

    public SistemaGastosPersonales() {
        super("Sistema de Gastos Personales");
        setSize(700, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Inicializar arreglo y contador
        transacciones = new Transaccion[100];
        contador = 0;
        // Panel entrada datos 
        JPanel panelEntrada = new JPanel(new GridLayout(9, 5, 8, 8)); // Tamaño del espacio donde se encuentran los Textfield, Jlabel, etc
        panelEntrada.setBorder(BorderFactory.createTitledBorder("Registrar Transacción")); // Le pone un borde con título al panel.

        panelEntrada.add(new JLabel("Saldo Inicial:"));
        saldoInicialField = new JTextField(" ");
        panelEntrada.add(saldoInicialField);

        panelEntrada.add(new JLabel("Descripción:"));
        descripcionField = new JTextField();
        panelEntrada.add(descripcionField);

        panelEntrada.add(new JLabel("Monto:"));
        montoField = new JTextField();
        panelEntrada.add(montoField);

        panelEntrada.add(new JLabel("Categoria:"));
        tipoCombo = new JComboBox<>(new String[]{"Ingreso", "Gasto"});
        panelEntrada.add(tipoCombo);

        panelEntrada.add(new JLabel("Fecha:"));
        fechaChooser = new JDateChooser();
        fechaChooser.setDateFormatString("dd/MM/yyyy");
        panelEntrada.add(fechaChooser);

        JButton agregarButton = new JButton("Agregar Transacción");
        panelEntrada.add(agregarButton);

        JButton eliminarButton = new JButton("Eliminar todo");
        panelEntrada.add(eliminarButton);

        JButton eliminartraButton = new JButton("Eliminar transacción");
        panelEntrada.add(eliminartraButton);

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

        JButton reporteButton = new JButton("Mostrar Reporte General");
        panelAbajo.add(reporteButton);

        JButton histogramaButton = new JButton("Mostrar Gráfico");
        panelAbajo.add(histogramaButton);

        JButton reportecatButton = new JButton("Mostrar reporte por categoria ");
        panelAbajo.add(reportecatButton);

        add(panelAbajo, BorderLayout.SOUTH);

        // Panel para el histograma gráfico
        histogramaPanel = new HistogramaPanel();
        histogramaPanel.setPreferredSize(new Dimension(680, 200));
        histogramaPanel.setBorder(BorderFactory.createTitledBorder("Gráfico de Transacciones"));
        add(histogramaPanel, BorderLayout.CENTER);

        // Acción botón agregar
        agregarButton.addActionListener(e -> {
            try {
                if (contador == 0) {
                    saldoInicial = Double.parseDouble(saldoInicialField.getText());
                    saldoInicialField.setEditable(false);
                    saldoactual = saldoInicial;
                }

                String desc = descripcionField.getText();
                double monto = Double.parseDouble(montoField.getText());
                String tipo = (String) tipoCombo.getSelectedItem();
                Date fechaSeleccionada = fechaChooser.getDate();
                String fecha = " ";

                if (fechaSeleccionada != null) {
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    fecha = formato.format(fechaSeleccionada);
                } else {
                    JOptionPane.showMessageDialog(this, "Debes seleccionar una fecha.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (desc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Descripción no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //Aqui es para validar si se puede agregar otra transacción
                // Siempre y cuando el saldo no sea menor o igual a 0
                // Crear transacción temporal
                Transaccion t = new Transaccion(idContador++, desc, monto, tipo, fecha);

                // Calcular saldo futuro antes de agregar
                double saldoFuturo = saldoactual;
                if (tipo.equals("Ingreso")) {
                    saldoFuturo += monto;
                } else {
                    saldoFuturo -= monto;
                }

                // Verificar si el saldo seria menor a 0
                if (saldoFuturo < 0) {
                    JOptionPane.showMessageDialog(this, "No puedes agregar más transacciones, el saldo es 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Si todo está bien, agregar la transacción
                transacciones[contador++] = t;
                saldoactual = saldoFuturo; // actualizamos el saldo manualmente
                actualizarSaldo();

                //Limpia los campos para poder agregar nuevos
                reporteArea.append(t + "\n");
                descripcionField.setText("");
                montoField.setText("");
                fechaChooser.setDate(null);
                alertaLabel.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Monto o saldo inicial inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción botón eliminar
        eliminarButton.addActionListener(e -> {
            // Confirmar con el usuario
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar todas las transacciones?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                contador = 0;
                saldoInicialField.setEditable(true);
                saldoInicialField.setText("");
                descripcionField.setText("");
                montoField.setText("");
                fechaChooser.setDate(null);
                reporteArea.setText("");
                alertaLabel.setText("");
                saldoLabel.setText("Saldo: Lps.0.0");
                saldoInicial = 0;
                saldoactual = 0;
                transacciones = new Transaccion[100]; // Reinicia el arreglo
                histogramaPanel.repaint(); // Actualiza el gráfico
            }
        }
        );

        // Acción botón eliminar transacción seleccionada
        eliminartraButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ingresa el ID de la transacción que deseas eliminar:");

            if (input != null && !input.isEmpty()) {
                try {
                    int idEliminar = Integer.parseInt(input);
                    boolean encontrado = false;

                    for (int i = 0; i < contador; i++) {
                        if (transacciones[i].getId() == idEliminar) {
                            for (int j = i; j < contador - 1; j++) {
                                transacciones[j] = transacciones[j + 1];
                            }
                            transacciones[contador - 1] = null;
                            contador--;
                            encontrado = true;
                            break;
                        }
                    }

                    if (encontrado) {
                        actualizarIds();          // <-- Aquí reasignas los IDs
                        alertaLabel.setText("Transacción eliminada exitosamente.");
                        actualizarSaldo();
                        mostrarReporte();
                        histogramaPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró una transacción con ese ID.");
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "ID inválido.");
                }
            }
        });

        // Acción botón reporte
        reporteButton.addActionListener(e -> mostrarReporte());
        reportecatButton.addActionListener(e -> mostrarReporteporcategoria());

        // Acción botón histograma
        //repaint le dice al panel que se vuelva a dibujar para mostrar los cambios recientes.
        histogramaButton.addActionListener(e -> histogramaPanel.repaint());

        setVisible(true);
    }

    private void actualizarIds() {
        for (int i = 0; i < contador; i++) {
            transacciones[i].setId(i + 1);
        }
    }

    private void actualizarSaldo() {
        saldoactual = saldoInicial;  //	Reinicia el saldo desde el valor base

        // recorre todas las transacciones guardadas en el arreglo transacciones
        //contador te dice cuántas transacciones hay actualmente.
        for (int i = 0; i < contador; i++) {
            Transaccion t = transacciones[i]; // Guarda temporalmente la transacción en la posición i
            /*Evalúa si la transacción es un ingreso o un gasto:
             -> Si es "Ingreso" → suma el monto al saldo.
             -> Si es "Gasto" → resta el monto del saldo.*/
            if (t.getTipo().equalsIgnoreCase("Ingreso")) {
                saldoactual += t.getMonto();
            } else {
                saldoactual -= t.getMonto();
            }
        }

        saldoLabel.setText("Saldo: Lps. " + String.format("%.2f", saldoactual)); //Actualiza el texto visible del saldo

        if (saldoactual <= 0) { //Muestra alerta si estás sin fondos
            alertaLabel.setText("¡Alerta! Presupuesto agotado o saldo negativo.");
        } else {
            alertaLabel.setText(" ");
        }
    }

    private void mostrarReporte() {

        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE TRANSACCIONES\n\n");
        reporte.append(String.format("%-5s %-12s %-10s %-20s %10s\n", "ID", "Fecha", "Tipo", "Descripción", "Monto"));
        reporte.append("---------------------------------------------------------------------\n");

        for (int i = 0; i < contador; i++) {
            Transaccion t = transacciones[i];
            reporte.append(String.format("%-5d %-12s %-10s %-20s %10.2f\n",
                    t.getId(),
                    t.getFecha(),
                    t.getTipo(),
                    t.getDescripcion(),
                    t.getMonto()));
        }

        reporte.append("\nSaldo disponible: Lps. ").append(String.format("%.2f", calcularSaldo()));

        // Mostrar en una ventana nueva
        //Crea un área de texto (JTextArea) y le pone dentro el contenido completo del reporte.
        JTextArea areaTexto = new JTextArea(reporte.toString());
        areaTexto.setEditable(false); // Hace que el usuario no pueda modificar el texto del reporte
        JScrollPane scroll = new JScrollPane(areaTexto); // Mete el área de texto dentro de un scroll, por si el reporte es largo.
        scroll.setPreferredSize(new Dimension(500, 400)); // Le da un tamaño preferido para la ventana (500x400 píxeles).

        JOptionPane.showMessageDialog(this, scroll, "Reporte de Transacciones", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarReporteporcategoria() {
        if (contador == 0) { //Se revisa si no hay datos (contador == 0).
            JOptionPane.showMessageDialog(this, "No hay transacciones registradas.", "", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        //Declaración de variables
        double totalGastos = 0;
        double totalIngresos = 0;

        // StringBuilder es una forma eficiente de ir armando texto largo sin crear muchas cadenas nuevas.
        StringBuilder ingresos = new StringBuilder("****** Ingresos ****** \n");
        ingresos.append(String.format("%-5s %-12s %-20s %10s\n", "ID", "Fecha", "Descripción", "Monto"));
        ingresos.append("-----------------------------------------------------------\n");

        StringBuilder gastos = new StringBuilder("****** Gastos ******\n");
        gastos.append(String.format("%-5s %-12s %-20s %10s\n", "ID", "Fecha", "Descripción", "Monto"));
        gastos.append("-----------------------------------------------------------\n");

        for (int i = 0; i < contador; i++) { //Recorre todas las transacciones guardadas en el arreglo.
            Transaccion t = transacciones[i];

            if (t.getTipo().equalsIgnoreCase("Ingreso")) { 
                ingresos.append(String.format("%-5d %-12s %-20s %10.2f\n", //Agrega una línea a la tabla de ingresos con la información de la transacción.
                        t.getId(),
                        t.getFecha(),
                        t.getDescripcion(),
                        t.getMonto()));
                totalIngresos += t.getMonto(); //Suma el monto al total de ingresos.
            } else {
                gastos.append(String.format("%-5d %-12s %-20s %10.2f\n",
                        t.getId(),
                        t.getFecha(),
                        t.getDescripcion(),
                        t.getMonto()));
                totalGastos += t.getMonto();
            }
        }
  
        //Al final de cada sección, se agrega la suma total de ingresos y gastos.
        ingresos.append(String.format("\nTotal de Ingresos: Lps. %.2f", totalIngresos)); 
        gastos.append(String.format("\nTotal de Gastos: Lps. %.2f", totalGastos));
        // Combina los dos textos en uno solo.
        String reporteCompleto = ingresos.toString() + "\n\n" + gastos.toString();

        // Mostrar en ventana
        JFrame ventanaReporte = new JFrame("Reporte por Categoría");
        ventanaReporte.setSize(600, 500);
        ventanaReporte.setLocationRelativeTo(this); //Centrar la ventana

        JTextArea areaTexto = new JTextArea(reporteCompleto);//Crea un área de texto donde se mostrará el reporte completo (solo lectura).
        areaTexto.setEditable(false);

        JScrollPane scroll = new JScrollPane(areaTexto);// Añade barras de desplazamiento al área de texto, por si el contenido es muy largo.
        ventanaReporte.add(scroll);

        ventanaReporte.setVisible(true); //Muestra la ventana con el reporte.
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
            super.paintComponent(g); // Limpia el panel antes de dibujar (borra lo anterior).

            if (contador == 0) { //Se revisa si no hay datos (contador == 0).
                g.setColor(Color.BLACK);
                g.drawString("No hay transacciones para mostrar histograma", 230, getHeight() / 2);
                return;
            }

            // Contadores
            int numIngresos = 0, numGastos = 0;
            double totalIngresos = 0, totalGastos = 0;

            for (int i = 0; i < contador; i++) {
                if (transacciones[i].getTipo().equalsIgnoreCase("Ingreso")) {
                    numIngresos++; // Cuenta cuántos ingresos hay
                    totalIngresos += transacciones[i].getMonto(); // Suma monto de ingresos
                } else {
                    numGastos++;
                    totalGastos += transacciones[i].getMonto();
                }
            }

            // Datos para dibujar
            int anchoBarra = 120;
            int alturaMax = 150;
            int espacioEntreBarras = 150;
            int xIngresos = 100;
            int xGastos = xIngresos + espacioEntreBarras;
            int yBase = getHeight() - 70;

            double maxTotal = Math.max(totalIngresos, totalGastos); // Sirve para escalar las barras proporcionalmente

            // Dibujar barra Ingresos
            g.setColor(new Color(0, 153, 255)); // Azul vivo
            int alturaIngresos = (int) ((totalIngresos / maxTotal) * alturaMax);  // Escala la altura con base al mayor valor
            g.fillRect(xIngresos, yBase - alturaIngresos, anchoBarra, alturaIngresos); //dibuja la barra azul con fillRect.

            // Dibujar barra Gastos
            g.setColor(new Color(255, 102, 102)); // Rojo suave
            int alturaGastos = (int) ((totalGastos / maxTotal) * alturaMax);
            g.fillRect(xGastos, yBase - alturaGastos, anchoBarra, alturaGastos);

            //Escribe las etiquetas debajo de cada barra para que se sepa cuál es cuál.
            g.setColor(Color.BLACK);
            g.drawString("Ingresos", xIngresos + 20, yBase + 20);
            g.drawString("Gastos", xGastos + 30, yBase + 20);

            // Monto y cantidad arriba de las barras
            g.drawString(" Lps. " + String.format("%.2f", totalIngresos), xIngresos - 10, yBase - alturaIngresos - 10);
            g.drawString(" Lps. " + String.format("%.2f", totalGastos), xGastos - 10, yBase - alturaGastos - 10);

            // Línea base
            g.drawLine(100, yBase, getWidth() - 100, yBase);
        }
    }

    // Clase interna para Transacción
    private class Transaccion {

        private String descripcion;
        private double monto;
        private String tipo;
        private String fecha;
        private int id;

        // Este es el constructor de la clase Transaccion.
        //Lo que hace es recibir los datos
        public Transaccion(int id, String descripcion, double monto, String tipo, String fecha) {
            // this se usa para diferenciar las variables del objeto de los parámetros del método
            this.id = id;
            this.descripcion = descripcion;
            this.monto = monto;
            this.tipo = tipo;
            this.fecha = fecha;

        }

        // getTipo() y getMonto() sirven para calcular el saldo.
        public String getTipo() {
            return tipo;
        }

        public double getMonto() {
            return monto;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() { // toString() sirve para mostrar la transacción en pantalla.
            return "[" + id + "] [" + fecha + "] " + tipo + ": " + descripcion + " - Lps." + String.format("%.2f", monto);
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getFecha() {
            return fecha;
        }
    }

    // Inicia la ventana gráfica de forma segura
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaGastosPersonales());
    }
}
