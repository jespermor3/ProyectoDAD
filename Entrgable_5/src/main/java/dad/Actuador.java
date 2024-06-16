package dad;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Actuador {
	private Integer id;
	private Integer idgrupo;
	private Integer idestado;
	private  Integer placaid;
	private String nombre;
	private Long fecha;
	private Integer estado;
	private String tipo;
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
	public Integer getIdestado() {
		return idestado;
	}
	public void setIdestado(Integer idestado) {
		this.idestado = idestado;
	}
	public Integer getPlacaid() {
		return placaid;
	}
	public void setPlacaid(Integer placaid) {
		this.placaid = placaid;
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
		return Objects.hash(estado, fecha, id, idestado, idgrupo, nombre, placaid, tipo);
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
				&& Objects.equals(id, other.id) && Objects.equals(idestado, other.idestado)
				&& Objects.equals(idgrupo, other.idgrupo) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(placaid, other.placaid) && Objects.equals(tipo, other.tipo);
	}
	@Override
	public String toString() {
		return "Actuador [id=" + id + ", idgrupo=" + idgrupo + ", idestado=" + idestado + ", placaid=" + placaid
				+ ", nombre=" + nombre + ", fecha=" + fecha + ", estado=" + estado + ", tipo=" + tipo + "]";
	}
	public Actuador(Integer id, Integer idgrupo, Integer idestado, Integer placaid, String nombre, Long fecha,
			Integer estado, String tipo) {
		super();
		this.id = id;
		this.idgrupo = idgrupo;
		this.idestado = idestado;
		this.placaid = placaid;
		this.nombre = nombre;
		this.fecha = fecha;
		this.estado = estado;
		this.tipo = tipo;
	}

}
