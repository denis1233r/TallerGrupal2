import java.util.ArrayList;
import java.time.Year;

public class CatalogoLibros {

    private ArrayList<Libro> libros;
    public CatalogoLibros() {
        libros = DatosIniciales.cargar();
        ordenarPorCodigoBurbuja();
    }

    public ArrayList<Libro> getLibros() {
        return new ArrayList<>(libros);
    }

    public int getCantidad() {
        return libros.size();
    }

    private void validarDatos(String titulo, String autor,
                              String genero, int anio) {

        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }

        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor es obligatorio.");
        }

        if (genero == null || genero.trim().isEmpty()) {
            throw new IllegalArgumentException("Seleccione un género.");
        }

        if (anio < 1 || anio > Year.now().getValue()) {
            throw new IllegalArgumentException("Año inválido.");
        }
    }

    public void registrar(Libro libro) {
        if (libro == null || libro.getCodigo() <= 0) {
            throw new IllegalArgumentException("Código inválido.");
        }

        validarDatos(
                libro.getTitulo(),
                libro.getAutor(),
                libro.getGenero(),
                libro.getAnio()
        );

        if (buscarIndicePorCodigo(libro.getCodigo()) != -1) {
            throw new IllegalArgumentException("Código repetido.");
        }

        if (libro.getMeGusta() < 0) {
            throw new IllegalArgumentException("Me gusta inválidos.");
        }

        libros.add(libro);
        ordenarPorCodigoBurbuja();
    }

    public int buscarIndicePorCodigo(int codigo) {
        if (codigo <= 0) {
            return -1;
        }

        int inicio = 0;
        int fin = libros.size() - 1;

        while (inicio <= fin) {

            int medio = inicio + (fin - inicio) / 2;

            if (libros.get(medio).getCodigo() == codigo) {
                return medio;
            }

            if (libros.get(medio).getCodigo() < codigo) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }

        return -1;
    }

    public Libro buscarPorCodigo(int codigo) {

        int indice = buscarIndicePorCodigo(codigo);

        if (indice == -1) {
            return null;
        }

        return libros.get(indice);
    }

    public ArrayList<Libro> buscarCoincidencias(String texto) {

        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese un texto.");
        }

        String consulta = texto.trim().toLowerCase();
        ArrayList<Libro> resultados = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.getTitulo().toLowerCase().contains(consulta)
                    || libro.getAutor().toLowerCase().contains(consulta)) {
                resultados.add(libro);
            }
        }

        return resultados;
    }

    public void editarLibro(int codigo, String titulo,
                            String autor, String genero, int anio) {

        validarDatos(titulo, autor, genero, anio);
        int indice = buscarIndicePorCodigo(codigo);
        if (indice == -1) {
            throw new IllegalArgumentException("Libro no encontrado.");
        }

        Libro libro = libros.get(indice);
        libro.setTitulo(titulo.trim());
        libro.setAutor(autor.trim());
        libro.setGenero(genero);
        libro.setAnio(anio);
    }

    public void darMeGusta(int codigo) {

        int indice = buscarIndicePorCodigo(codigo);
        if (indice == -1) {
            throw new IllegalArgumentException("Libro no encontrado.");
        }

        Libro libro = libros.get(indice);
        if (libro.getMeGusta() == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Contador máximo alcanzado.");
        }

        libro.incrementarMeGusta();
    }

    public void ordenarPorCodigoBurbuja() {
        for (int i = 0; i < libros.size() - 1; i++) {
            for (int j = 0; j < libros.size() - 1 - i; j++) {
                if (libros.get(j).getCodigo()
                        > libros.get(j + 1).getCodigo()) {
                    Libro aux = libros.get(j);
                    libros.set(j, libros.get(j + 1));
                    libros.set(j + 1, aux);
                }
            }
        }
    }

    public ArrayList<Libro> obtenerRanking() {
        ArrayList<Libro> ranking = new ArrayList<>(libros);
        for (int i = 0; i < ranking.size() - 1; i++) {

            for (int j = 0; j < ranking.size() - 1 - i; j++) {

                if (ranking.get(j).getMeGusta()
                        < ranking.get(j + 1).getMeGusta()) {

                    Libro aux = ranking.get(j);
                    ranking.set(j, ranking.get(j + 1));
                    ranking.set(j + 1, aux);
                }
            }
        }

        return ranking;
    }
}
