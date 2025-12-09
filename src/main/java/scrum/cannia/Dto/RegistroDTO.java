package scrum.cannia.Dto;
import lombok.Data;

@Data
public class RegistroDTO {

    private String usuario;
    private String contrasena;
    private String rol;

    private String numDoc;
    private String nombrePro;
    private String apellidoPro;
    private String direccionPro;
    private String telefonoPro;
    private String correoPro;

    private String numLicencia;
    private String nombreVete;
    private String apellidoVete;
    private String direccionVete;
    private String telefonoVete;
    private String correoVete;

    private String nombreFundacion;
    private String descripcionFundacion;
    private String direccionFundacion;
    private String telefonoFundacion;
    private String emailFundacion;

    private String nomMascota;
    private String especie;
    private String raza;
    private String color;
    private String fechaNacimiento;
    private String genero;

    private String lote;
    private String fechaAplicacion;
    private String fechaRefuerzo;
    private String fechaVencimiento;
    private String laboratorio;

    private String peso;
    private String anamnesis;
    private String diagnostico;
    private String tratamiento;
    private String fechaHora;


}
