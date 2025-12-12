package scrum.cannia.service.creator;

import scrum.cannia.model.Genero;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.TipoEstadoMascota;

    public class MascotaPropietarioCreator extends MascotaCreator {

        private final String nombre;
        private final String especie;
        private final String raza;
        private final String color;
        private final Genero genero;
        private final PropietarioModel propietario;

        public MascotaPropietarioCreator(
                String nombre,
                String especie,
                String raza,
                String color,
                Genero genero,
                PropietarioModel propietario
        ) {
            this.nombre = nombre;
            this.especie = especie;
            this.raza = raza;
            this.color = color;
            this.genero = genero;
            this.propietario = propietario;
        }

        @Override
        protected void asignarDatosComunes(MascotaModel mascota) {
            mascota.setNomMascota(nombre);
            mascota.setEspecie(especie);
            mascota.setRaza(raza);
            mascota.setColor(color);
            mascota.setGenero(genero);
        }

        @Override
        protected void asignarDatosParticulares(MascotaModel mascota) {
            mascota.setPropietario(propietario);
            mascota.setFundacion(null);
            mascota.setTipoEstado(TipoEstadoMascota.PROPIA);
        }
    }


