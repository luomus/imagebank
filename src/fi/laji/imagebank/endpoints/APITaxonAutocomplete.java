package fi.laji.imagebank.endpoints;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.laji.imagebank.dao.DAO.TaxonNameSearchParameters;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.json.JSONArray;
import fi.luomus.commons.services.ResponseData;

@WebServlet(urlPatterns = {"/api/taxon-autocomplete/*"})
public class APITaxonAutocomplete extends APIBaseServlet {

	private static final long serialVersionUID = -7742573949045772578L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String query = req.getParameter("query");
		String group = req.getParameter("group");

		TaxonNameSearchParameters params = new TaxonNameSearchParameters(query, given(group) ? Qname.of(group) : null);

		JSONArray response;
		if (!given(query) || query.length() < 4) {
			response = new JSONArray();
		} else {
			response = getDAO().getTaxonNameSearch(params).getArray("results");
		}
		return new ResponseData(response.toString(), "application/json");
	}

}
