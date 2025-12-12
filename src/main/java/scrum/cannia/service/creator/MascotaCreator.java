package scrum.cannia.service.creator;

import scrum.cannia.model.MascotaModel;

public abstract class MascotaCreator {

    // TEMPLATE METHOD
    public final MascotaModel crearMascota() {

        MascotaModel mascota = new MascotaModel();

        validarDatos();
        asignarDatosComunes(mascota);
        asignarDatosParticulares(mascota);

        return mascota;
    }


    protected void validarDatos() {}


    protected abstract void asignarDatosComunes(MascotaModel mascota);


    protected abstract void asignarDatosParticulares(MascotaModel mascota);
}

