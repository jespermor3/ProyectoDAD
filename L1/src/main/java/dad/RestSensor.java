package dad;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;


public class RestSensor extends AbstractVerticle {
	
	private Map<Integer, Sensor> sensores=new HashMap<Integer, Sensor>();
	private Map<Integer, Actuador> actuadores=new HashMap<Integer, Actuador>();
	private List<Sensor> lsen=new ArrayList<>();
	private List<Placa> lpl=new ArrayList<>();
	private List<Actuador> lac=new ArrayList<>();
	private Map<Integer, Placa> placa=new HashMap<Integer, Placa>();
	private Gson gson;
	
	MySQLPool mySqlClient;
	
	@Override
	public void start(Promise<Void> startFuture) {
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("proyectodad").setUser("chema").setPassword("chema");

		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);
		
		getAllsen();
		getAllac();
		getAllpla();

	}
	private void getAllsen() {
		mySqlClient.query("SELECT * FROM proyectodad.sensores;", res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Sensor(elem.getInteger("id"), elem.getInteger("placaid"),
							elem.getString("nombre"), localDateToDate(elem.getLocalDate("fecha")),
							elem.getDouble("valor"))));
				}
				System.out.println(result.toString());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}
	
	private void getAllac() {
		mySqlClient.query("SELECT * FROM proyectodad.actuadores;", res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Actuador(elem.getInteger("id"), elem.getInteger("placaid"),
							elem.getString("nombre"), localDateToDate(elem.getLocalDate("fecha")),
							elem.getInteger("estado"),elem.getString("tipo"))));
				}
				System.out.println(result.toString());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}
	private void getAllpla() {
		mySqlClient.query("SELECT * FROM proyectodad.placas;", res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Placa(elem.getInteger("id"),
							elem.getString("nombre"))));
				}
				System.out.println(result.toString());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}
	
	private void getByName(String username) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM ejemplodad.users WHERE username = ?",
						Tuple.of(username), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Sensor(elem.getInteger("id"), elem.getInteger("placaid"),
											elem.getString("nombre"), localDateToDate(elem.getLocalDate("fecha")),
											elem.getDouble("valor"))));
								}
								System.out.println(result.toString());
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	
	
	
	private Date localDateToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
