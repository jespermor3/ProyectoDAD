package dad;

import java.util.Objects;

public class Placa {
	private Integer id;
	private String nombre;
	private Integer sensorid;
	private Integer actuador1id;
	private Integer actuador2id;
	
	
	public Placa(Integer id, String nombre, Integer sensorid, Integer actuador1id, Integer actuador2id) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.sensorid = sensorid;
		this.actuador1id = actuador1id;
		this.actuador2id = actuador2id;
	}
	@Override
	public String toString() {
		return "Placa [id=" + id + ", nombre=" + nombre + ", sensorid=" + sensorid + ", actuador1id=" + actuador1id
				+ ", actuador2id=" + actuador2id + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(actuador1id, actuador2id, id, nombre, sensorid);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Placa other = (Placa) obj;
		return Objects.equals(actuador1id, other.actuador1id) && Objects.equals(actuador2id, other.actuador2id)
				&& Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(sensorid, other.sensorid);
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Integer getSensorid() {
		return sensorid;
	}
	public void setSensorid(Integer sensorid) {
		this.sensorid = sensorid;
	}
	public Integer getActuador1id() {
		return actuador1id;
	}
	public void setActuador1id(Integer actuador1id) {
		this.actuador1id = actuador1id;
	}
	public Integer getActuador2id() {
		return actuador2id;
	}
	public void setActuador2id(Integer actuador2id) {
		this.actuador2id = actuador2id;
	}
	

}
