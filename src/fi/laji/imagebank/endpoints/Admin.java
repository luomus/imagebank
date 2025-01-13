package fi.laji.imagebank.endpoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import fi.luomus.commons.utils.URIBuilder;
import fi.luomus.kuvapalvelu.model.Media;
import fi.luomus.kuvapalvelu.model.MediaClass;

@WebServlet(urlPatterns = {"/admin/*"})
public class Admin extends ImageBankBaseServlet {

	private static final long serialVersionUID = 6023756994673791965L;

	private static final String REF = "ref";
	private static final String TAXON_ID = "taxonId";
	private static final String TAXON_SEARCH = "taxonSearch";
	private static final String IMAGE_SEARCH = "imageSearch";

	@Override
	protected ResponseData notAuthorizedRequest(HttpServletRequest req, HttpServletResponse res) {
		User user = getUser(req);
		if (user == null) {
			// not logged in
			return redirectTo(getConfig().baseURL()+"/login?next="+getNext(req));
		}
		// not admin
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
		String imageSearch = req.getParameter(IMAGE_SEARCH);
		String taxonSearch = req.getParameter(TAXON_SEARCH);
		String id = getId(req);

		if (given(id)) {
			if (id.startsWith("MM.")) {
				return singleImageEdit(new Qname(id), data, req);
			}
			if (id.startsWith("MX.")) {
				return taxonEdit(new Qname(id), data, taxonSearch, req);
			}
		}

		if (given(imageSearch)) {
			if (imageSearch.startsWith("MM.")) {
				return singleImageEdit(new Qname(id), data, req);
			}
			return imageSearch(imageSearch, data);
		}
		if (given(taxonSearch)) {
			if (taxonSearch.startsWith("MX.")) {
				return taxonEdit(new Qname(taxonSearch), data, null, req);
			}
			return taxonSearch(taxonSearch, data);
		}

		return data.setViewName("admin-main");
	}

	private ResponseData taxonEdit(Qname taxonId, ResponseData data, String taxonSearch, HttpServletRequest req) throws Exception {
		if (!getTaxonomyDAO().getTaxonContainer().hasTaxon(taxonId)) {
			getSession(req).setFlashError(getText("unknown_taxon", req));
			return redirectTo(getConfig().baseURL()+"/admin");
		}
		Taxon t = getTaxonomyDAO().getTaxon(taxonId);
		getTaxonImageDAO().reloadImages(t);
		boolean multiPrimary = t.getMultimedia().stream().filter(i->i.isPrimaryForTaxon()).count() > 1;
		return data.setViewName("admin-taxon")
				.setData("taxon", t)
				.setData(TAXON_SEARCH, taxonSearch)
				.setData("multiPrimary", multiPrimary);
	}

	private ResponseData singleImageEdit(Qname mediaId, ResponseData data, HttpServletRequest req) throws Exception {
		Optional<Media> image = getMediaApiClient().get(MediaClass.IMAGE, mediaId.toString());
		if (!image.isPresent()) {
			getSession(req).setFlashError(getText("unknown_image", req));
			return redirectTo(getConfig().baseURL()+"/admin");
		}

		String taxonSearch = req.getParameter(TAXON_SEARCH);
		Taxon taxon = null;

		Qname taxonId = new Qname(req.getParameter(TAXON_ID));
		if (taxonId.isSet()) {
			if (getTaxonomyDAO().getTaxonContainer().hasTaxon(taxonId)) {
				taxon = getTaxonomyDAO().getTaxon(taxonId);
				data.setData("taxon", taxon);
			}
		}

		data.setViewName("admin-image")
		.setData("image", image.get())
		.setData(TAXON_SEARCH, taxonSearch);

		List<String> ref = new ArrayList<>();
		if (given(taxonSearch)) ref.add(TAXON_SEARCH + "=" + taxonSearch);
		if (taxon != null) ref.add(TAXON_ID + "=" + taxon.getId());

		data.setData(REF, ref.stream().collect(Collectors.joining("&")));
		return data;
	}

	private ResponseData taxonSearch(String taxonSearch, ResponseData data) throws Exception {
		TaxonSearchResponse searchResults = getTaxonomyDAO().search(new TaxonSearch(taxonSearch, 10).setMatchTypes(MatchType.EXACT, MatchType.PARTIAL, MatchType.LIKELY));
		return data.setViewName("admin-taxon-select")
				.setData("results", searchResults)
				.setData(TAXON_SEARCH, taxonSearch)
				.setData(REF, TAXON_SEARCH+"="+taxonSearch);
	}

	private ResponseData imageSearch(String imageSearch, ResponseData data) {
		// TODO Auto-generated method stub
		// jos vain yksi -> redirect to image edit
		return data.setViewName("admin-image-select");
	}

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String imageSearch = req.getParameter(IMAGE_SEARCH);
		String taxonSearch = req.getParameter(TAXON_SEARCH);
		String taxonId = req.getParameter(TAXON_ID);
		String id = getId(req);
		getSession(req).setFlashSuccess(getText("save_success", req));
		URIBuilder redirectURI = new URIBuilder(getConfig().baseURL()+"/admin/"+id);
		if (given(taxonSearch)) redirectURI.addParameter(TAXON_SEARCH, taxonSearch);
		if (given(taxonId)) redirectURI.addParameter(TAXON_ID, taxonId);
		return new ResponseData().setRedirectLocation(redirectURI.toString());
	}

}
