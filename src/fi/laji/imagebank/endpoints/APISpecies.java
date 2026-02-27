package fi.laji.imagebank.endpoints;

import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.laji.imagebank.dao.TaxonomyCaches.SpeciesTerms;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.taxonomy.Taxon;

@WebServlet(urlPatterns = {"/api/species/*"})
public class APISpecies extends APIBaseServlet {

	private static final long serialVersionUID = 2476064245925550418L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		SpeciesTerms terms = new SpeciesTerms(req);
		if (!terms.valid()) {
			getSession(req).setFlashError(getText("invalid_parameters", req));
			return status(400, res);
		}

		List<Taxon> taxa = getTaxonomyDAO().getSpecies(terms);

		return initResponseData(req).setViewName("api-species")
				.setData("taxa", taxa)
				.setData("prevPage", prevPage(terms))
				.setData("nextPage", nextPage(terms))
				.setData("currentPage", terms.page)
				.setData("lastPage", lastPage(terms));
	}

	private int lastPage(SpeciesTerms terms) {
		int count = getTaxonomyDAO().getSpeciesCount(terms);
		int lastPage = (count + terms.pageSize - 1) / terms.pageSize;
		return lastPage;
	}

	private Integer nextPage(SpeciesTerms terms) {
		int lastPage = lastPage(terms);
		if (terms.page >= lastPage) return null;
		return terms.page + 1;
	}

	private Integer prevPage(SpeciesTerms terms) {
		if (terms.page == 1) return null;
		return terms.page - 1;
	}

}
