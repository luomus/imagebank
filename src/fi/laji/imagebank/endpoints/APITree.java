package fi.laji.imagebank.endpoints;

import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.laji.imagebank.dao.TaxonomyCaches.TreeTerms;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.taxonomy.Taxon;

@WebServlet(urlPatterns = {"/api/tree/*"})
public class APITree extends APIBaseServlet {

	private static final long serialVersionUID = 2476064245925550418L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		TreeTerms terms = new TreeTerms(req);
		if (!terms.valid()) {
			getSession(req).setFlashError(getText("invalid_parameters", req));
			return status(400, res);
		}

		List<Taxon> taxa = getTaxonomyDAO().getTree(terms);

		return initResponseData(req).setViewName("api-tree").setData("taxa", taxa);
	}

}
