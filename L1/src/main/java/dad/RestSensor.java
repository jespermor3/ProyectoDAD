package dad;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystemOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;


public class RestSensor extends AbstractVerticle {
	
	private Gson gson;
	
	public static Map<Integer, Sensor> sensores=new HashMap<>();
	
	MySQLPool mySqlClient;
	//MqttClient mqttClient;
	
	@Override
	public void start(Promise<Void> startFuture) {
		gson =  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("proyectodad").setUser("chema").setPassword("chema");
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "localhost", s -> {

			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					System.out.println("Suscripción " + mqttClient.clientId());
				}
			});

			mqttClient.publishHandler(handler -> {
				System.out.println("Mensaje recibido:");
				System.out.println("    Topic: " + handler.topicName().toString());
				System.out.println("    Id del mensaje: " + handler.messageId());
				System.out.println("    Contenido: " + handler.payload().toString());
			});
			mqttClient.publish("topic_1", Buffer.buffer("Ejemplo"), MqttQoS.AT_LEAST_ONCE, false, false);
		});

		PoolOptions poolOptions = new PoolOptions().setMaxSize(2);
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::handle).listen(8085, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});

		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);
		
		router.route("/api/*").handler(BodyHandler.create());
		router.get("/api/sensores").handler(this::getAllsen);
		router.get("/api/sensores/:id").handler(this::getByidsen);
		router.get("/api/actuadores/:id").handler(this::getByidAc);
		router.get("/api/actuadores").handler(this::getAllac);
		router.get("/api/placas").handler(this::getAllpla);
		router.post("/api/sensores/new").handler(this::addOneSen);
		router.post("/api/actuadores/new").handler(this::addOneAct);
		router.post("/api/placas/new").handler(this::addOnePla);
		router.get("/api/placas/sensores/:idgrupo").handler(this::getAllByidgrupoSensor);
		router.get("/api/placas/actuadores/:idgrupo").handler(this::getAllByidgrupoActuador);
		router.get("/api/sensores/last/:id").handler(this::getLastidsen);
        router.get("/api/actuadores/last/:id").handler(this::getLastidAc);
        router.get("/api/placas/sensores/last/:idgrupo").handler(this::getLastByidgrupoSensor);
        router.get("/api/placas/actuadores/last/:idgrupo").handler(this::getLastByidgrupoActuador);

	}
	private void getAllsen(RoutingContext routingContext) {
		mySqlClient.query("SELECT * FROM proyectodad.sensores;", res -> {
			if (res.succeeded()) {
				// Get the result set
				routingContext.response().setStatusCode(200);
				RowSet<Row> resultSet = res.result();
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Sensor(elem.getInteger("id"),elem.getInteger("idvalor"), elem.getInteger("placaid"),
							elem.getString("nombre"), elem.getLong("fecha"),
							elem.getDouble("valor"))));
				}
				
				System.out.println(result.toString());
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(result.toString());
				System.out.println(routingContext.response().getStatusCode());
				
				
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}
	
	
	
	private void addOneSen(RoutingContext routingContext) {
		final Sensor sensor = gson.fromJson(routingContext.getBodyAsString(), Sensor.class);
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("INSERT INTO sensores(id,placaid,nombre,valor) VALUES ("+sensor.getId()+","+
			sensor.getPlacaid()+", '"+sensor.getNombre()+"' ,"+sensor.getValor()+");", res->{
					if(res.succeeded()) {
						System.out.println(sensor);
						routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
						.end(gson.toJson(sensor));
						
					}else {
						routingContext.response().setStatusCode(0).putHeader("content-type", "application/json; charset=utf-8")
						.end(gson.toJson(sensor));
						System.out.println(
						res.cause().getMessage());
						System.out.println(
						res.cause().getLocalizedMessage());
						System.out.println("Failed");
						
					}
					connection.result().close();
				});
			} else {
				routingContext.response().setStatusCode(0).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(sensor));
				System.out.println(connection.cause().toString());
			}
		});
	}
	
	private void addOnePla(RoutingContext routingContext) {
		final Placa sensor = gson.fromJson(routingContext.getBodyAsString(), Placa.class);
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("INSERT INTO placa(id,placaid,nombre,fecha,valor) VALUES "
						+ "("+sensor.getId()+","+sensor.getNombre()+");", res->{
					if(res.succeeded()) {
						System.out.println(sensor);
					}else {
						System.out.println(
						res.cause().getMessage());
						System.out.println(
						res.cause().getLocalizedMessage());
						System.out.println("Failed");
						
					}
					connection.result().close();
				});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(sensor));
	}
	
	private void addOneAct(RoutingContext routingContext) {
		final Actuador sensor = gson.fromJson(routingContext.getBodyAsString(), Actuador.class);
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("INSERT INTO actuadores(id,placaid,nombre,estado,tipo) VALUES ("+sensor.getId()+","+
			sensor.getPlacaid()+", '"+sensor.getNombre()+" ',"+sensor.getEstado()+", '"+sensor.getTipo()+"');", res->{
					if(res.succeeded()) {
						System.out.println(sensor);
					}else {
						System.out.println(
						res.cause().getMessage());
						System.out.println(
						res.cause().getLocalizedMessage());
						System.out.println("Failed");
						
					}
					connection.result().close();
				});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(sensor));
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
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Sensor(elem.getInteger("id"),elem.getInteger("idvalor"),
											elem.getInteger("placaid"), elem.getString("nombre"),
											elem.getLong("fecha"), elem.getDouble("valor"))));
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
											elem.getLong("fecha"), elem.getInteger("estado"),elem.getString("tipo"))));
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
															elem1.getLong("fecha"), elem1.getDouble("valor"))));
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
																elem1.getLong("fecha"), elem1.getInteger("estado"),elem1.getString("tipo"))));
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
							elem.getString("nombre"), elem.getLong("fecha"),
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
	
	private void getLastidsen(RoutingContext routingContext) {
		final int par = Integer.parseInt(routingContext.request().getParam("id"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM proyectodad.sensores WHERE fecha = (SELECT MAX(fecha) FROM proyectodad.sensores WHERE placaid = ?);",
						Tuple.of(par), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Sensor(elem.getInteger("id"),elem.getInteger("idvalor"),
											elem.getInteger("placaid"), elem.getString("nombre"),
											elem.getLong("fecha"), elem.getDouble("valor"))));
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
	
	private void getLastidAc(RoutingContext routingContext) {
		final int par = Integer.parseInt(routingContext.request().getParam("id"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM proyectodad.actuadores WHERE fecha = (SELECT MAX(fecha) FROM proyectodad.actuadores WHERE placaid = ?);",
						Tuple.of(par), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Actuador(elem.getInteger("id"),elem.getInteger("idestado"),
											elem.getInteger("placaid"), elem.getString("nombre"),
											elem.getLong("fecha"), elem.getInteger("estado"),elem.getString("tipo"))));
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
	
	private void getLastByidgrupoSensor(RoutingContext routingContext) {
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
									connection.result().preparedQuery("SELECT * FROM proyectodad.sensores WHERE fecha = (SELECT MAX(fecha) FROM proyectodad.sensores WHERE placaid = ?);", Tuple.of(elem.getInteger("id")),
											res1->{
												if(res1.succeeded()) {
													RowSet<Row> resultSet1 = res1.result();
													for(Row elem1:resultSet1) {
														result.add(JsonObject.mapFrom(new Sensor(elem1.getInteger("id"),elem1.getInteger("idvalor"),
															elem1.getInteger("placaid"), elem1.getString("nombre"),
															elem1.getLong("fecha"), elem1.getDouble("valor"))));
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
	
	private void getLastByidgrupoActuador(RoutingContext routingContext) {
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
									connection.result().preparedQuery("SELECT * FROM proyectodad.actuadores WHERE fecha = (SELECT MAX(fecha) FROM proyectodad.actuadores WHERE placaid = ?);", Tuple.of(elem.getInteger("id")),
											res1->{
												if(res1.succeeded()) {
													RowSet<Row> resultSet1 = res1.result();
													for(Row elem1:resultSet1) {
														result.add(JsonObject.mapFrom(new Actuador(elem1.getInteger("id"),elem1.getInteger("idestado"),
																elem1.getInteger("placaid"), elem1.getString("nombre"),
																elem1.getLong("fecha"), elem1.getInteger("estado"),elem1.getString("tipo"))));
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

	
	
	
	private Date localDateToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
