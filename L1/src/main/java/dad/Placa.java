package dad;

import java.util.Objects;

public class Placa {
	private Integer id;
	private Integer idgrupo;
	private String nombre;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getIdgrupo() {
		return idgrupo;
	}
	public void setIdgrupo(Integer idgrupo) {
		this.idgrupo = idgrupo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id, idgrupo, nombre);
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
		return Objects.equals(id, other.id) && Objects.equals(idgrupo, other.idgrupo)
				&& Objects.equals(nombre, other.nombre);
	}
	@Override
	public String toString() {
		return "Placa [id=" + id + ", idgrupo=" + idgrupo + ", nombre=" + nombre + "]";
	}
	public Placa(Integer id, Integer idgrupo, String nombre) {
		super();
		this.id = id;
		this.idgrupo = idgrupo;
		this.nombre = nombre;
	}
	
	
}