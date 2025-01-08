package fi.laji.imagebank.endpoints;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.laji.imagebank.models.User;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.taxonomy.Taxon;
import fi.luomus.commons.taxonomy.TaxonSearch;
import fi.luomus.commons.taxonomy.TaxonSearch.MatchType;
import fi.luomus.commons.taxonomy.TaxonSearchResponse;

@WebServlet(urlPatterns = {"/admin/*"})
public class Admin extends ImageBankBaseServlet {

	private static final long serialVersionUID = 6023756994673791965L;

	@Override
	protected ResponseData notAuthorizedRequest(HttpServletRequest req, HttpServletResponse res) {
		getSession(req).setFlashError(getText("must_be_admin", req));
		return redirectTo(getConfig().baseURL());
	}

	@Override
	protected boolean authorized(HttpServletRequest req) {
		User user = getUser(req);
		if (user == null) return false;
		return user.isAdmin();
	}

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		ResponseData data = initResponseData(req);
		String imageSearch = req.getParameter("image");
		String taxonSearch = req.getParameter("taxon");
		String id = getId(req);

		if (given(id)) {
			if (id.startsWith("MM.")) {
				return singleImageEdit(new Qname(id), data);
			}
			if (id.startsWith("MX.")) {
				return taxonEdit(new Qname(id), data, taxonSearch, req);
			}
		}

		if (given(imageSearch)) {
			if (imageSearch.startsWith("MM.")) {
				return singleImageEdit(new Qname(id), data);
			}
			return imageSearch(imageSearch, data);
		}
		if (given(taxonSearch)) {
			if (taxonSearch.startsWith("MX.")) {
				return taxonEdit(new Qname(taxonSearch), data, null, req);
			}
			return taxonSearch(taxonSearch, data, req);
		}

		return data.setViewName("admin-main");
	}

	private ResponseData taxonEdit(Qname taxonId, ResponseData data, String ref, HttpServletRequest req) throws Exception {
		if (!getTaxonomyDAO().getTaxonContainer().hasTaxon(taxonId)) {
			getSession(req).setFlashError(getText("unknown_taxon", req));
			return redirectTo(getConfig().baseURL()+"/admin");
		}
		Taxon t = getTaxonomyDAO().getTaxon(taxonId);
		getTaxonImageDAO().reloadImages(t);
		return data.setViewName("admin-taxon").setData("taxon", t).setData("ref", ref);
	}

	private ResponseData singleImageEdit(Qname mediaId, ResponseData data) {
		// TODO Auto-generated method stub
		return data.setViewName("admin-image");
	}

	private ResponseData taxonSearch(String taxonSearch, ResponseData data, HttpServletRequest req) throws Exception {
		TaxonSearchResponse searchResults = getTaxonomyDAO().search(new TaxonSearch(taxonSearch, 10).setMatchTypes(MatchType.EXACT, MatchType.PARTIAL, MatchType.LIKELY));
		return data.setViewName("admin-taxon-select").setData("results", searchResults).setData("ref", "taxon="+taxonSearch).setData("term", taxonSearch);
	}

	private ResponseData imageSearch(String imageSearch, ResponseData data) {
		// TODO Auto-generated method stub
		// jos vain yksi -> redirect to image edit
		return data.setViewName("admin-image-select");
	}

}
