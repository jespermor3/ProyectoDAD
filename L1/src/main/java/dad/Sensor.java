package dad;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

public class Sensor {
	private Integer id;
	private Integer placaid;
	private String nombre;
	private Date fecha;
	private Double valor;
	public Sensor(Integer id, Integer placaid, String nombre, Date fecha, Double valor) {
		super();
		this.id = id;
		this.placaid = placaid;
		this.nombre = nombre;
		this.fecha = fecha;
		this.valor = valor;
	}
	@Override
	public String toString() {
		LocalDate fecha2=LocalDate.ofInstant(fecha.toInstant(), ZoneId.systemDefault());
		return "Sensor [id=" + id + ", placaid=" + placaid + ", nombre=" + nombre + ", fecha=" + fecha2 + ", valor="
				+ valor + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(fecha, id, nombre, placaid, valor);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sensor other = (Sensor) obj;
		return Objects.equals(fecha, other.fecha) && Objects.equals(id, other.id)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(placaid, other.placaid)
				&& Objects.equals(valor, other.valor);
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	
	
	
	

}
