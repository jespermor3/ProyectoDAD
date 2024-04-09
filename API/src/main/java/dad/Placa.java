package dad;

import java.util.Objects;

public class Placa {
	private Integer id;
	private String nombre;
	
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
	@Override
	public int hashCode() {
		return Objects.hash(id, nombre);
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
		return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
	}
	public Placa(Integer id, String nombre) {
		super();
		this.id = id;
		this.nombre = nombre;
	}
	@Override
	public String toString() {
		return "Placa [id=" + id + ", nombre=" + nombre + "]";
	}
	
	
	
	

}
