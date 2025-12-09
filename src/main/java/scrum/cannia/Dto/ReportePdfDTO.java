package scrum.cannia.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportePdfDTO {
    private String titulo;
    private String fechaGeneracion;
    private String filtroAplicado;
    private List<Map<String, Object>> productos;
    private Map<String, Object> estadisticas;
    private String tipoGrafico;
    private byte[] graficoBytes; // Opcional: para incluir imagen del gráfico
}