package dad;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;


public class RestSensor extends AbstractVerticle {
	
	private Gson gson;
	
	MySQLPool mySqlClient;
	
	@Override
	public void start(Promise<Void> startFuture) {
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("proyectodad").setUser("chema").setPassword("chema");

		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::handle).listen(8085, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});

		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);
		
		router.get("/api/sensores").handler(this::getAllsen);
		router.get("/api/sensores/:id").handler(this::getByidsen);
		router.get("/api/actuadores/:id").handler(this::getByidAc);
		router.get("/api/actuadores").handler(this::getAllac);
		router.get("/api/placas").handler(this::getAllpla);
		router.post("/api/sensores/post").handler(this::addsen);
		router.get("/api/placas/sensores/:idgrupo").handler(this::getAllByidgrupoSensor);
		router.get("/api/placas/actuadores/:idgrupo").handler(this::getAllByidgrupoActuador);

	}
	private void getAllsen(RoutingContext routingContext) {
		mySqlClient.query("SELECT * FROM proyectodad.sensores;", res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Sensor(elem.getInteger("id"),elem.getInteger("idvalor"), elem.getInteger("placaid"),
							elem.getString("nombre"), localDateToDate(elem.getLocalDate("fecha")),
							elem.getDouble("valor"))));
				}
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(result.toString());
				System.out.println(result.toString());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}
	
	private void addsen(RoutingContext routingContext) {
		final Sensor sensor = gson.fromJson(routingContext.getBodyAsString(), Sensor.class);
		List<Tuple>batch=new ArrayList<>();
		Tuple a=Tuple.of(sensor.getId(),sensor.getPlacaid(),sensor.getNombre(),sensor.getFecha().toString(),sensor.getValor());
		batch.add(a);
		mySqlClient.preparedQuery("INSERT INTO proyectodad.sensores(id,placaid,nombre,fecha,valor) VALUES (?,?,?,?,?);", res->{
			if(res.succeeded()) {
				System.out.println(a);
			}else {
				System.out.println("Batch Failed");
			}
		});
	}
	
	private void getByidsen(RoutingContext routingContext) {
		final int par = Integer.parseInt(routingContext.request().getParam("id"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM proyectodad.sensores WHERE placaid = ?",
						Tuple.of(par), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Sensor(elem.getInteger("id"),elem.getInteger("idvalor"),
											elem.getInteger("placaid"), elem.getString("nombre"),
											localDateToDate(elem.getLocalDate("fecha")), elem.getDouble("valor"))));
								}
								System.out.println(result.toString());
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
								.end(result.toString());
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
	
	private void getByidAc(RoutingContext routingContext) {
		final int par = Integer.parseInt(routingContext.request().getParam("id"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM proyectodad.actuadores WHERE placaid = ?",
						Tuple.of(par), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Actuador(elem.getInteger("id"),elem.getInteger("idestado"),
											elem.getInteger("placaid"), elem.getString("nombre"),
											localDateToDate(elem.getLocalDate("fecha")), elem.getInteger("estado"),elem.getString("tipo"))));
								}
								System.out.println(result.toString());
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
								.end(result.toString());
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
	
	private void getByidgrupo(RoutingContext routingContext) {
		final int par = Integer.parseInt(routingContext.request().getParam("idgrupo"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM proyectodad.placas WHERE idgrupo = ?",
						Tuple.of(par), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Placa(elem.getInteger("id"),elem.getInteger("idgrupo"),
											elem.getString("nombre"))));
								}
								System.out.println(result.toString());
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
								.end(result.toString());
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
	
	private void getByidplaca(RoutingContext routingContext) {
		final int par = Integer.parseInt(routingContext.request().getParam("id"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM proyectodad.placas WHERE id = ?",
						Tuple.of(par), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Placa(elem.getInteger("id"),elem.getInteger("idgrupo"),
											elem.getString("nombre"))));
								}
								System.out.println(result.toString());
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
								.end(result.toString());
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
	
	private void getAllByidgrupoSensor(RoutingContext routingContext) {
		final int par = Integer.parseInt(routingContext.request().getParam("idgrupo"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT id FROM proyectodad.placas WHERE idgrupo = ?",
						Tuple.of(par), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									connection.result().preparedQuery("SELECT * FROM proyectodad.sensores WHERE placaid = ?", Tuple.of(elem.getInteger("id")),
											res1->{
												if(res1.succeeded()) {
													RowSet<Row> resultSet1 = res1.result();
													for(Row elem1:resultSet1) {
														result.add(JsonObject.mapFrom(new Sensor(elem1.getInteger("id"),elem1.getInteger("idvalor"),
															elem1.getInteger("placaid"), elem1.getString("nombre"),
															localDateToDate(elem1.getLocalDate("fecha")), elem1.getDouble("valor"))));
													}
													System.out.println(result.toString());
													routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
													.end(result.toString());
												}else {
													System.out.println("Error: " + res1.cause().getLocalizedMessage());
												}
												
											});
								}
								
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
	
	private void getAllByidgrupoActuador(RoutingContext routingContext) {
		final int par = Integer.parseInt(routingContext.request().getParam("idgrupo"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT id FROM proyectodad.placas WHERE idgrupo = ?",
						Tuple.of(par), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									connection.result().preparedQuery("SELECT * FROM proyectodad.actuadores WHERE placaid = ?", Tuple.of(elem.getInteger("id")),
											res1->{
												if(res1.succeeded()) {
													RowSet<Row> resultSet1 = res1.result();
													for(Row elem1:resultSet1) {
														result.add(JsonObject.mapFrom(new Actuador(elem1.getInteger("id"),elem1.getInteger("idestado"),
																elem1.getInteger("placaid"), elem1.getString("nombre"),
																localDateToDate(elem1.getLocalDate("fecha")), elem1.getInteger("estado"),elem1.getString("tipo"))));
													}
													System.out.println(result.toString());
													routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
													.end(result.toString());
												}else {
													System.out.println("Error: " + res1.cause().getLocalizedMessage());
												}
												
											});
								}
								
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
	
	private void getAllac(RoutingContext routingContext) {
		mySqlClient.query("SELECT * FROM proyectodad.actuadores;", res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Actuador(elem.getInteger("id"),elem.getInteger("idestado"), elem.getInteger("placaid"),
							elem.getString("nombre"), localDateToDate(elem.getLocalDate("fecha")),
							elem.getInteger("estado"),elem.getString("tipo"))));
				}
				System.out.println(result.toString());
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(result.toString());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}
	private void getAllpla(RoutingContext routingContext) {
		mySqlClient.query("SELECT * FROM proyectodad.placas;", res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Placa(elem.getInteger("id"),elem.getInteger("idgrupo"),
							elem.getString("nombre"))));
				}
				System.out.println(result.toString());
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(result.toString());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
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
