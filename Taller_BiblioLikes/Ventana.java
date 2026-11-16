import javax.swing.*;
import java.time.Year;
import java.util.ArrayList;

public class Ventana extends JFrame {

    private CatalogoLibros catalogo;

    // Componentes de Registro
    private JTextField txtCodigo;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JComboBox<String> cbGeneros;
    private JSpinner spAnio;
    private JButton btnRegistrar;
    private JLabel lblTotal;

    // Componentes de búsqueda
    private JTextField txtBuscarCodigo;
    private JTextField txtBuscarTexto;
    private JButton btnBuscarCodigo;
    private JButton btnBuscarTexto;
    private JList<Libro> listaResultados;
    private JScrollPane scrollResultados;
    private DefaultListModel<Libro> modeloResultados;

    private JTextField txtEditarTitulo;
    private JTextField txtEditarAutor;
    private JComboBox<String> cbEditarGenero;
    private JSpinner spEditarAnio;
    private JLabel lblDetalle;
    private JLabel lblMeGusta;
    private JButton btnGuardar;
    private JButton btnMeGusta;

    private JList<String> listaRanking;
    private JScrollPane scrollRanking;
    private JButton btnActualizarRanking;
    private JLabel lblTituloRanking;
    private DefaultListModel<String> modeloRanking;

    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JLabel lblCodigo;
    private JLabel lblTitulo;
    private JLabel lblAutor;
    private JLabel lblAnio;
    private JLabel lblGenero;
    private JTextField denisRojasCristianChicaizaTextField;

    private int codigoSeleccionado = -1;
    private boolean busquedaPorTexto = false;
    private String consultaActiva = "";

    private final String[] generos = {
            "Programación",
            "Bases de datos",
            "Redes",
            "Seguridad",
            "Sistemas",
            "Software",
            "IA",
            "Gestión"
    };

    public Ventana() {

        catalogo = new CatalogoLibros();

        setTitle("BiblioLikes - Biblioteca Universitaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(panel1);

        setSize(1000, 700);
        setLocationRelativeTo(null);

        configurarRegistro();
        configurarBusqueda();
        configurarRanking();

        actualizarTotal();
        actualizarRanking();

        limpiarSeleccion();
    }

    private void configurarRegistro() {

        cbGeneros.removeAllItems();

        for (String genero : generos) {
            cbGeneros.addItem(genero);
        }

        int actual = Year.now().getValue();

        spAnio.setModel(
                new SpinnerNumberModel(actual, 1, actual, 1)
        );

        btnRegistrar.addActionListener(e -> registrarLibro());
    }

    private void registrarLibro() {

        try {

            int codigo = Integer.parseInt(txtCodigo.getText().trim());

            Libro nuevo = new Libro(
                    codigo,
                    txtTitulo.getText().trim(),
                    txtAutor.getText().trim(),
                    (String) cbGeneros.getSelectedItem(),
                    (Integer) spAnio.getValue(),
                    0
            );

            catalogo.registrar(nuevo);

            actualizarTotal();
            actualizarRanking();

            txtCodigo.setText("");
            txtTitulo.setText("");
            txtAutor.setText("");

            cbGeneros.setSelectedIndex(0);
            spAnio.setValue(Year.now().getValue());

            JOptionPane.showMessageDialog(
                    this,
                    "Libro registrado correctamente."
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un código numérico válido."
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage()
            );
        }
    }


    private void configurarBusqueda() {

        modeloResultados = new DefaultListModel<>();

        listaResultados = new JList<>(modeloResultados);

        listaResultados.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        scrollResultados.setViewportView(listaResultados);

        btnBuscarCodigo.addActionListener(e -> buscarCodigo());

        btnBuscarTexto.addActionListener(e -> buscarTexto());

        listaResultados.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                Libro libro = listaResultados.getSelectedValue();

                if (libro != null) {
                    mostrarLibro(libro);
                }
            }
        });

        btnGuardar.addActionListener(e -> guardarCambios());

        btnMeGusta.addActionListener(e -> agregarMeGusta());
    }

    private void buscarCodigo() {

        limpiarSeleccion();
        modeloResultados.clear();

        try {

            int codigo = Integer.parseInt(
                    txtBuscarCodigo.getText().trim()
            );

            if (codigo <= 0) {
                throw new IllegalArgumentException(
                        "El código debe ser positivo."
                );
            }

            Libro libro = catalogo.buscarPorCodigo(codigo);

            if (libro == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Libro no encontrado."
                );

                return;
            }

            busquedaPorTexto = false;
            consultaActiva = "";

            modeloResultados.addElement(libro);

            listaResultados.setSelectedIndex(0);

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un código numérico válido."
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage()
            );
        }
    }

    private void buscarTexto() {

        limpiarSeleccion();
        modeloResultados.clear();

        try {

            String texto = txtBuscarTexto.getText().trim();

            ArrayList<Libro> resultados =
                    catalogo.buscarCoincidencias(texto);

            busquedaPorTexto = true;
            consultaActiva = texto;

            for (Libro libro : resultados) {
                modeloResultados.addElement(libro);
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Coincidencias encontradas: " + resultados.size()
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage()
            );
        }
    }

    private void mostrarLibro(Libro libro) {

        codigoSeleccionado = libro.getCodigo();

        lblDetalle.setText(
                "Código: " + libro.getCodigo()
        );

        txtEditarTitulo.setText(libro.getTitulo());

        txtEditarAutor.setText(libro.getAutor());

        cbEditarGenero.setSelectedItem(libro.getGenero());

        spEditarAnio.setValue(libro.getAnio());

        lblMeGusta.setText(
                "Me gusta: " + libro.getMeGusta()
        );

        btnGuardar.setEnabled(true);
        btnMeGusta.setEnabled(true);
    }

    private void limpiarSeleccion() {

        codigoSeleccionado = -1;

        if (listaResultados != null) {
            listaResultados.clearSelection();
        }

        if (lblDetalle != null) {
            lblDetalle.setText("Seleccione un libro.");
        }

        if (lblMeGusta != null) {
            lblMeGusta.setText("Me gusta: 0");
        }

        if (txtEditarTitulo != null) {
            txtEditarTitulo.setText("");
            txtEditarAutor.setText("");
        }

        if (btnGuardar != null) {
            btnGuardar.setEnabled(false);
            btnMeGusta.setEnabled(false);
        }
    }

    private void guardarCambios() {

        if (codigoSeleccionado <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un libro."
            );

            return;
        }

        int codigo = codigoSeleccionado;

        try {

            catalogo.editarLibro(
                    codigo,
                    txtEditarTitulo.getText().trim(),
                    txtEditarAutor.getText().trim(),
                    (String) cbEditarGenero.getSelectedItem(),
                    (Integer) spEditarAnio.getValue()
            );

            refrescarResultados(codigo);

            actualizarRanking();

            JOptionPane.showMessageDialog(
                    this,
                    "Cambios guardados correctamente."
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage()
            );
        }
    }

    private void refrescarResultados(int codigo) {

        limpiarSeleccion();

        ArrayList<Libro> resultados;

        if (busquedaPorTexto) {

            resultados = catalogo.buscarCoincidencias(
                    consultaActiva
            );

        } else {

            resultados = new ArrayList<>();

            Libro libro = catalogo.buscarPorCodigo(codigo);

            if (libro != null) {
                resultados.add(libro);
            }
        }

        modeloResultados.clear();

        int indice = -1;

        for (int i = 0; i < resultados.size(); i++) {

            Libro libro = resultados.get(i);

            modeloResultados.addElement(libro);

            if (libro.getCodigo() == codigo) {
                indice = i;
            }
        }

        if (indice >= 0) {
            listaResultados.setSelectedIndex(indice);
        } else {
            limpiarSeleccion();
        }
    }


    private void agregarMeGusta() {

        if (codigoSeleccionado <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un libro primero."
            );

            return;
        }

        int codigo = codigoSeleccionado;

        try {

            catalogo.darMeGusta(codigo);

            refrescarResultados(codigo);

            actualizarRanking();

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage()
            );
        }
    }


    private void configurarRanking() {

        modeloRanking = new DefaultListModel<>();

        listaRanking.setModel(modeloRanking);

        btnActualizarRanking.addActionListener(
                e -> actualizarRanking()
        );

        tabbedPane1.addChangeListener(e -> {

            if (tabbedPane1.getSelectedIndex() == 2) {
                actualizarRanking();
            }
        });
    }

    private void actualizarRanking() {

        if (modeloRanking == null) {
            return;
        }

        modeloRanking.clear();

        ArrayList<Libro> ranking = catalogo.obtenerRanking();

        int posicion = 1;

        for (Libro libro : ranking) {

            modeloRanking.addElement(
                    posicion +
                            ". Código: " + libro.getCodigo() +
                            " | " + libro.getTitulo() +
                            " | Me gusta: " + libro.getMeGusta()
            );

            posicion++;
        }
    }

    private void actualizarTotal() {

        lblTotal.setText(
                "Cantidad actual de libros: " +
                        catalogo.getCantidad()
        );
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Ventana ventana = new Ventana();

            ventana.setVisible(true);
        });
    }
}
