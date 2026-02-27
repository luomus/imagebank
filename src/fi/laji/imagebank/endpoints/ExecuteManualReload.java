package fi.laji.imagebank.endpoints;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.services.ResponseData;

@WebServlet(urlPatterns = {"/api/execute-nightly-reload/*"})
public class ExecuteManualReload extends APIBaseServlet {

	private static final long serialVersionUID = 3155987698752147049L;

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!hasSecretParam(req)) throw new IllegalAccessException();
		getTaxonomyDAO().startNightlyTasks();
		return response("started", "text/plain");
	}

}
