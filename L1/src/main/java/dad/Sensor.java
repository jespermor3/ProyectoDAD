package dad;

import java.util.Objects;

public class Sensor {
	private Integer id;
	private String nombre;
	private Long fecha;
	private Double valor;
	
	
	public Sensor(Integer id, String nombre, Long fecha, Double valor) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.fecha = fecha;
		this.valor = valor;
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


	public Double getValor() {
		return valor;
	}


	public void setValor(Double valor) {
		this.valor = valor;
	}


	@Override
	public int hashCode() {
		return Objects.hash(fecha, id, nombre, valor);
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
				&& Objects.equals(nombre, other.nombre) && Objects.equals(valor, other.valor);
	}


	@Override
	public String toString() {
		return "Sensor [id=" + id + ", nombre=" + nombre + ", fecha=" + fecha + ", valor=" + valor + "]";
	}
	
	
	
	

}
