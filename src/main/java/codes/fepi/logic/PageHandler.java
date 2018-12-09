package codes.fepi.logic;

import codes.fepi.entitiy.Person;
import spark.Request;

public class PageHandler {

	public Object person(Request req) {
		Person person = req.session().attribute("person");
		return person;
	}
}
