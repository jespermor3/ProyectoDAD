package es.us.lsi.dad;

import java.util.Objects;

public class Actuador {
	private Integer id;
	private String nombre;
	private Long fecha;
	private Integer estado;
	private String tipo;
	public Actuador(Integer id, String nombre, Long fecha, Integer estado, String tipo) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.fecha = fecha;
		this.estado = estado;
		this.tipo=tipo;
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
	public Long getFecha() {
		return fecha;
	}
	public void setFecha(Long fecha) {
		this.fecha = fecha;
	}
	public Integer getEstado() {
		return estado;
	}
	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	@Override
	public int hashCode() {
		return Objects.hash(estado, fecha, id, nombre, tipo);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actuador other = (Actuador) obj;
		return Objects.equals(estado, other.estado) && Objects.equals(fecha, other.fecha)
				&& Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(tipo, other.tipo);
	}
	@Override
	public String toString() {
		return "Actuador [id=" + id + ", nombre=" + nombre + ", fecha=" + fecha + ", estado=" + estado + ", tipo="
				+ tipo + "]";
	}
	
	

}
