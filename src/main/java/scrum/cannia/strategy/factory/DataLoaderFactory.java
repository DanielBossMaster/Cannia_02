package scrum.cannia.strategy.factory;

import scrum.cannia.strategy.DataLoaderStrategy;
import scrum.cannia.strategy.impl.CsvDataLoader;
import scrum.cannia.strategy.impl.ExcelDataLoader;
import scrum.cannia.strategy.impl.JsonDataLoader;

public class DataLoaderFactory {

    public static DataLoaderStrategy getStrategy(String nombreArchivo) {

        nombreArchivo = nombreArchivo.toLowerCase();
// *** Se implementa Strategy
        if (nombreArchivo.endsWith(".xlsx")) {
            return new ExcelDataLoader();
        }
        if (nombreArchivo.endsWith(".csv")) {
            return new CsvDataLoader();
        }
        if (nombreArchivo.endsWith(".json")) {
            return new JsonDataLoader();
        }

        throw new IllegalArgumentException("Formato no soportado");
    }
}
