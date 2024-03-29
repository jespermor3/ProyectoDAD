package es.us.lsi.dad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestServidor extends AbstractVerticle {
	
	private Map<Integer, Sensor> sensores=new HashMap<Integer, Sensor>();
	private List<Sensor> lsen=new ArrayList<>();
	private Gson gson;
	
	
	public void start(Promise<Void> startFuture) {
		createSomeData(25);
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
		
		router.route("/api/sensores*").handler(BodyHandler.create());
		//router.get("/api/sensores").handler(this::getAllWithParams);
		router.get("/api/sensores").handler(this::getAll);
		router.get("/api/sensores/:id").handler(this::getOne);
		router.post("/api/sensores").handler(this::addOne);
		router.delete("/api/sensores/:id").handler(this::deleteOne);
		router.put("/api/sensores/:id").handler(this::putOne);
	}

	private void createSomeData(int number) {
		Random rnd = new Random();
		IntStream.range(0, number).forEach(elem -> {
			int id = rnd.nextInt();
			sensores.put(id, new Sensor(id, "Nombre_" + id, 
					new Date(Calendar.getInstance().getTimeInMillis() + id).getTime(),  0.+id));
		});
		
	}
	
	@SuppressWarnings("unused")
	private void getAllWithParams(RoutingContext routingcontext1) {
		lsen=sensores.entrySet().stream().map(x->x.getValue()).toList();
		final String id = routingcontext1.queryParams().contains("id") ? routingcontext1.queryParam("id").get(0) : null;
		final String nombre = routingcontext1.queryParams().contains("nombre") ? routingcontext1.queryParam("nombre").get(0) : null;
		
		routingcontext1.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(lsen.stream().filter(elem ->{
					boolean res = true;
					res = res && id != null ? elem.getId().equals(Integer.valueOf(id)) : true;
					res = res && nombre != null ? elem.getNombre().equals(nombre) : true;
					return res;
				}).toList()));
	}
	
	private void getAll(RoutingContext routingcontext1) {
		lsen=sensores.entrySet().stream().map(x->x.getValue()).toList();
		routingcontext1.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
		.end(gson.toJson(lsen));
	}

	private void addOne(RoutingContext routingcontext1) {
		final Sensor sensor = gson.fromJson(routingcontext1.getBodyAsString(), Sensor.class);
		sensores.put(sensor.getId(), sensor);
		routingcontext1.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(sensor));
	}

	private void deleteOne(RoutingContext routingcontext1) {
		int id = Integer.parseInt(routingcontext1.request().getParam("userid"));
		if (sensores.containsKey(id)) {
			Sensor sensor = sensores.get(id);
			sensores.remove(id);
			routingcontext1.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
					.end(gson.toJson(sensor));
		} else {
			routingcontext1.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
					.end();
		}
	}

	private void putOne(RoutingContext routingcontext1) {
		int id = Integer.parseInt(routingcontext1.request().getParam("id"));
		Sensor ds = sensores.get(id);
		final Sensor element = gson.fromJson(routingcontext1.getBodyAsString(), Sensor.class);
		ds.setNombre(element.getNombre());
		ds.setFecha(element.getFecha());
		ds.setValor(element.getValor());
		sensores.put(ds.getId(), ds);
		routingcontext1.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(element));
	}

	private void getOne(RoutingContext routingcontext1) {
		int id = Integer.parseInt(routingcontext1.request().getParam("userid"));
		if (sensores.containsKey(id)) {
			Sensor ds = sensores.get(id);
			routingcontext1.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(ds));
		} else {
			routingcontext1.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
					.end();
		}
	}

}
