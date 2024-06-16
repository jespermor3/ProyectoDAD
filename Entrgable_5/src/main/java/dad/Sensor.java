package dad;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Sensor {
	private Integer id;
	private Integer idgrupo;
	private Integer idvalor;
	private Integer placaid;
	private String nombre;
	private Long fecha;
	private Double valor;
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
	public Integer getIdvalor() {
		return idvalor;
	}
	public void setIdvalor(Integer idvalor) {
		this.idvalor = idvalor;
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
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	@Override
	public int hashCode() {
		return Objects.hash(fecha, id, idgrupo, idvalor, nombre, placaid, valor);
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
				&& Objects.equals(idgrupo, other.idgrupo) && Objects.equals(idvalor, other.idvalor)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(placaid, other.placaid)
				&& Objects.equals(valor, other.valor);
	}
	@Override
	public String toString() {
		return "Sensor [id=" + id + ", idgrupo=" + idgrupo + ", idvalor=" + idvalor + ", placaid=" + placaid
				+ ", nombre=" + nombre + ", fecha=" + fecha + ", valor=" + valor + "]";
	}
	public Sensor(Integer id, Integer idgrupo, Integer idvalor, Integer placaid, String nombre, Long fecha,
			Double valor) {
		super();
		this.id = id;
		this.idgrupo = idgrupo;
		this.idvalor = idvalor;
		this.placaid = placaid;
		this.nombre = nombre;
		this.fecha = fecha;
		this.valor = valor;
	}
	

}
