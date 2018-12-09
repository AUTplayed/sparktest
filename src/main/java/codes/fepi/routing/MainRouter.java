package codes.fepi.routing;

import codes.fepi.entitiy.Person;
import codes.fepi.logic.PageHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.template.mustache.MustacheTemplateEngine;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import static spark.Spark.*;

public class MainRouter {
	private void init() {
		port(8080);
		staticFiles.location("/public");
		staticFiles.expireTime(60 * 60);
	}

	public void route() {
		init();
		path("/api", this::apiRoute);
		path("/pages", this::pagesRoute);
		try {
			// doing this here should keep the index file in memory for faster response (i think)
			URI uri = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("public/index.html")).toURI();
			byte[] bytes = Files.readAllBytes(Paths.get(uri));
			get("/:page", (req, res) -> bytes);
		} catch (Exception ignored) {
		}
	}

	/**
	 * /api
	 */
	private void apiRoute() {
		ObjectMapper mapper = new ObjectMapper();
		get("/ping", (req, res) -> "pong");
		get("/person", (req, res) -> {
			req.session().attribute("person", new Person(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
			return "generated person";
		});
		post("/person", (req, res) -> {
			Person person = mapper.readValue(req.bodyAsBytes(), Person.class);
			req.session().attribute("person", person);
			return "saved";
		});
	}

	/**
	 * /pages
	 */
	private void pagesRoute() {
		PageHandler handler = new PageHandler();
		page("person", handler::person);
		page("input", handler::person);
		page("index", (req) -> null);
	}

	private void page(String page, Function<Request, Object> handler) {
		String route = String.format("/%s/%s.html", page, page);
		LoggerFactory.getLogger("page").info("registered page: {}", route);
		get(route, (req, res) -> new MustacheTemplateEngine().render(new ModelAndView(handler.apply(req), page + ".html")));
	}
}
