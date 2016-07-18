package edu.uniandes.ecos.codeaholics.persistence;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by snaphuman on 6/8/16.
 */
public class Episode {

	public static final String NAME_CEDULA = "cedula";
	public static final String NAME_FECHA = "fecha";
	public static final String NAME_HORA = "hora";
	public static final String NAME_INTENSIDAD = "intensidad";
	public static final String NAME_MEDICAMENTO = "medicamento";
	public static final String NAME_ACTIVIDAD = "actividad";

	@SerializedName("_id")
	private String _id;
	private int cedula;
	private Date fecha;
	private String hora;
	private int nivelDolor;
	private String medicamento;
	private String actividad;

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public int getCedula() {
		return cedula;
	}

	public void setCedula(int cedula) {
		this.cedula = cedula;
	}

	public Date getFecha() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		formatter.applyPattern(String.valueOf(fecha));
		System.out.println(fecha);
		try {
			this.fecha = formatter.parse(String.valueOf(fecha));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getNivelDolor() {
		return nivelDolor;
	}

	public void setNivelDolor(int nivelDolor) {
		this.nivelDolor = nivelDolor;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

}
