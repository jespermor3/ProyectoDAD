package dad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestSensor extends AbstractVerticle {
	
	private Map<Integer, Sensor> sensores=new HashMap<Integer, Sensor>();
	private Map<Integer, Actuador> actuadores=new HashMap<Integer, Actuador>();
	private List<Sensor> lsen=new ArrayList<>();
	private List<Placa> lpl=new ArrayList<>();
	private List<Actuador> lac=new ArrayList<>();
	private Map<Integer, Placa> placa=new HashMap<Integer, Placa>();
	private Gson gson;
	
	public void start(Promise<Void> startFuture) {
		createSomeData(25);
		createSomeDataAc(25);
		createSomeDataPl(25);
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::handle).listen(8084, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
		
		router.route("/api/sensores*").handler(BodyHandler.create());
		router.get("/api/sensores/param").handler(this::getAllWithParams);
		router.get("/api/sensores").handler(this::getAll);
		router.get("/api/sensores/:id").handler(this::getOne);
		router.post("/api/sensores/add").handler(this::addOne);
		router.delete("/api/sensores/del/:id").handler(this::deleteOne);
		router.put("/api/sensores/put/:id").handler(this::putOne);
		router.put("/api/sensores/last/:id").handler(this::getLast);
		
		router.route("/api/actuadores*").handler(BodyHandler.create());
		router.get("/api/actuadores/param").handler(this::getAllWithParamsAc);
		router.get("/api/actuadores").handler(this::getAllAc);
		router.get("/api/actuadores/:id").handler(this::getOneAC);
		router.post("/api/actudores/add").handler(this::addOneAc);
		router.delete("/api/actuadores/del/:id").handler(this::deleteOneAc);
		router.put("/api/actuadores/put/:id").handler(this::putOneAc);
		
		router.route("/api/placas*").handler(BodyHandler.create());
		router.get("/api/placas/param").handler(this::getAllWithParamsPl);
		router.get("/api/placas").handler(this::getAllPl);
		router.get("/api/placas/:id").handler(this::getOnePl);
		router.post("/api/placas/add").handler(this::addOnePl);
		router.delete("/api/placas/del/:id").handler(this::deleteOnePl);
		router.put("/api/placas/put/:id").handler(this::putOnePl);
	}

	private void createSomeData(int number) {
		Random rnd = new Random();
		IntStream.range(0, number).forEach(elem -> {
			int id = rnd.nextInt();
			sensores.put(id, new Sensor(id, "Nombre_" + id, 
					new Date(Calendar.getInstance().getTimeInMillis() + id).getTime(),  0.+id));
		});
		
	}
	
	private void createSomeDataAc(int number) {
		Random rnd = new Random();
		IntStream.range(0, number).forEach(elem -> {
			int id = rnd.nextInt();
			actuadores.put(id, new Actuador(id, "Nombre_" + id, 
					new Date(Calendar.getInstance().getTimeInMillis() + id).getTime(),  id,"Tipo"+id));
		});
		
	}
	
	private void createSomeDataPl(int number) {
		Random rnd = new Random();
		
		List<Integer> auxac=actuadores.keySet().stream().toList();
		List<Integer> auxsen=sensores.keySet().stream().toList();
		IntStream.range(0, number).forEach(elem -> {
			int id = rnd.nextInt();
			Integer rn=rnd.nextInt(0, number-1);
			placa.put(id, new Placa(Integer.valueOf(id), "Nombre_" + id, auxsen.get(rn),
					auxac.get(rn),auxac.get(rn+1)));
		});
		
	}
	

	private void getAllWithParams(RoutingContext routingContext) {
		lsen=sensores.entrySet().stream().map(x->x.getValue()).toList();
		final String id = routingContext.queryParams().contains("id") ? routingContext.queryParam("id").get(0) : null;
		final String nombre = routingContext.queryParams().contains("nombre") ? routingContext.queryParam("nombre").get(0) : null;
		
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(lsen.stream().filter(elem ->{
					boolean res = true;
					res = res && id != null ? elem.getId().equals(Integer.valueOf(id)) : true;
					res = res && nombre != null ? elem.getNombre().equals(nombre) : true;
					return res;
				}).toList()));
	}

	private void getAll(RoutingContext routingContext) {
		lsen=sensores.entrySet().stream().map(x->x.getValue()).toList();
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
		.end(gson.toJson(lsen));
	}

	private void addOne(RoutingContext routingContext) {
		final Sensor sensor = gson.fromJson(routingContext.getBodyAsString(), Sensor.class);
		sensores.put(sensor.getId(), sensor);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(sensor));
	}

	private void deleteOne(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("userid"));
		if (sensores.containsKey(id)) {
			Sensor sensor = sensores.get(id);
			sensores.remove(id);
			routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
					.end(gson.toJson(sensor));
		} else {
			routingContext.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
					.end();
		}
	}

	private void putOne(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("id"));
		Sensor ds = sensores.get(id);
		final Sensor element = gson.fromJson(routingContext.getBodyAsString(), Sensor.class);
		ds.setNombre(element.getNombre());
		ds.setFecha(element.getFecha());
		ds.setValor(element.getValor());
		sensores.put(ds.getId(), ds);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(element));
	}

	private void getOne(RoutingContext routingContext) {
		int id=0;
		try {
		id = Integer.parseInt(routingContext.request().getParam("id"));
		if (sensores.containsKey(id)) {
			Sensor ds = sensores.get(id);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(ds));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
					.end();
		}} catch (Exception e) {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
			.end();
}
	}
	
	private void getLast(RoutingContext routingContext) {
		int id=0;
		try {
		id = Integer.parseInt(routingContext.request().getParam("id"));
		if (sensores.containsKey(id)) {
			Integer aux=id;
			Sensor last=lsen.stream().filter(x->x.getId().equals(aux)).max(Comparator.comparing(Sensor::getFecha)).get();
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(last));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
					.end();
		}} catch (Exception e) {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
			.end();
}
	}

	private void getAllWithParamsAc(RoutingContext routingContext) {
		lac=actuadores.entrySet().stream().map(x->x.getValue()).toList();
		final String id = routingContext.queryParams().contains("id") ? routingContext.queryParam("id").get(0) : null;
		final String nombre = routingContext.queryParams().contains("nombre") ? routingContext.queryParam("nombre").get(0) : null;
		
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(lac.stream().filter(elem ->{
					boolean res = true;
					res = res && id != null ? elem.getId().equals(Integer.valueOf(id)) : true;
					res = res && nombre != null ? elem.getNombre().equals(nombre) : true;
					return res;
				}).toList()));
	}

	private void getAllAc(RoutingContext routingContext) {
		lac=actuadores.entrySet().stream().map(x->x.getValue()).toList();
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
		.end(gson.toJson(lac));
	}

	private void getOneAC(RoutingContext routingContext) {
		int id=0;
		try {
		id = Integer.parseInt(routingContext.request().getParam("id"));
		
		if (actuadores.containsKey(id)) {
			Actuador ds = actuadores.get(id);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(ds));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
					.end();
		}} catch (Exception e) {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
			.end();
}
	}

	private void addOneAc(RoutingContext routingContext) {
		final Actuador ac = gson.fromJson(routingContext.getBodyAsString(), Actuador.class);
		actuadores.put(ac.getId(), ac);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(ac));
	}

	private void putOneAc(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("id"));
		Actuador ds = actuadores.get(id);
		final Actuador element = gson.fromJson(routingContext.getBodyAsString(), Actuador.class);
		ds.setNombre(element.getNombre());
		ds.setFecha(element.getFecha());
		ds.setEstado(element.getEstado());
		actuadores.put(ds.getId(), ds);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(element));
	}

	private void deleteOneAc(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("id"));
		if (actuadores.containsKey(id)) {
			Actuador ac = actuadores.get(id);
			actuadores.remove(id);
			routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
					.end(gson.toJson(ac));
		} else {
			routingContext.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
					.end();
		}
	}

	private void getAllWithParamsPl(RoutingContext routingContext) {
		lpl=placa.entrySet().stream().map(x->x.getValue()).toList();
		final String id = routingContext.queryParams().contains("id") ? routingContext.queryParam("id").get(0) : null;
		final String nombre = routingContext.queryParams().contains("nombre") ? routingContext.queryParam("nombre").get(0) : null;
		
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(lpl.stream().filter(elem ->{
					boolean res = true;
					res = res && id != null ? elem.getId().equals(Integer.valueOf(id)) : true;
					res = res && nombre != null ? elem.getNombre().equals(nombre) : true;
					return res;
				}).toList()));
	}

	private void getAllPl(RoutingContext routingContext) {
		lpl=placa.entrySet().stream().map(x->x.getValue()).toList();
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
		.end(gson.toJson(lpl));
	}

	private void getOnePl(RoutingContext routingContext) {
		int id=0;
		try {
		id = Integer.parseInt(routingContext.request().getParam("id"));
		
		if (placa.containsKey(id)) {
			Placa ds = placa.get(id);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(ds));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
					.end();
		}} catch (Exception e) {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
			.end();
}
	}
	
	private void addOnePl(RoutingContext routingContext) {
		final Placa pl = gson.fromJson(routingContext.getBodyAsString(), Placa.class);
		placa.put(pl.getId(), pl);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(pl));
	}
	private void deleteOnePl(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("id"));
		if (placa.containsKey(id)) {
			Placa ac = placa.get(id);
			placa.remove(id);
			routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
					.end(gson.toJson(ac));
		} else {
			routingContext.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
					.end();
		}
	}
	private void putOnePl(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("id"));
		Placa ds = placa.get(id);
		final Placa element = gson.fromJson(routingContext.getBodyAsString(), Placa.class);
		ds.setNombre(element.getNombre());
		ds.setSensorid(element.getSensorid());
		ds.setActuador1id(element.getActuador1id());
		ds.setActuador2id(element.getActuador2id());
		placa.put(ds.getId(), ds);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(element));
	}

}
