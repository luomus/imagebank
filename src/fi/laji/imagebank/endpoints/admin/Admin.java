package fi.laji.imagebank.endpoints.admin;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import fi.laji.imagebank.dao.TaxonomyDAO;
import fi.laji.imagebank.endpoints.ImageBankBaseServlet;
import fi.laji.imagebank.models.Preferences;
import fi.laji.imagebank.models.User;
import fi.laji.imagebank.util.Constant;
import fi.luomus.commons.containers.Image;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.json.JSONObject;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.taxonomy.Taxon;
import fi.luomus.commons.taxonomy.TaxonSearch;
import fi.luomus.commons.taxonomy.TaxonSearch.MatchType;
import fi.luomus.commons.taxonomy.TaxonSearchResponse;
import fi.luomus.commons.utils.URIBuilder;
import fi.luomus.kuvapalvelu.model.Media;
import fi.luomus.kuvapalvelu.model.MediaClass;
import fi.luomus.kuvapalvelu.model.Meta;
import fi.luomus.utils.exceptions.ApiException;
import fi.luomus.utils.exceptions.NotFoundException;

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
		String imageSearch = trim(req.getParameter(IMAGE_SEARCH));
		String taxonSearch = trim(req.getParameter(TAXON_SEARCH));
		String taxonId = trim(req.getParameter(TAXON_ID));
		String id = trim(getId(req));

		if (given(id)) {
			if (id.startsWith("MM.")) {
				return singleImageEdit(new Qname(id), data, imageSearch, req);
			}
			if (id.startsWith("MX.")) {
				return taxonEdit(new Qname(id), data, taxonSearch, req);
			}
		}

		if (given(taxonId)) {
			return taxonEdit(new Qname(taxonId), data, taxonSearch, req);
		}

		if (given(imageSearch)) {
			if (imageSearch.startsWith("MM.")) {
				return singleImageEdit(new Qname(imageSearch), data, null, req);
			}
			if (imageSearch.startsWith("http://tun.fi/MM.")) {
				return singleImageEdit(Qname.fromURI(imageSearch), data, null, req);
			}
			if (imageSearch.contains("/MM.")) {
				return singleImageEdit(parseImageId(imageSearch), data, null, req);
			}
			return imageSearch(imageSearch, data, req);
		}
		if (given(taxonSearch)) {
			if (taxonSearch.startsWith("MX.")) {
				return taxonEdit(new Qname(taxonSearch), data, null, req);
			}
			return taxonSearch(taxonSearch, data);
		}

		return data.setViewName("admin-main");
	}

	private String trim(String parameter) {
		return parameter == null ? null : parameter.trim();
	}

	private Qname parseImageId(String imageSearch) {
		Matcher matcher = Pattern.compile(".*/(MM\\.\\d+)/.*").matcher(imageSearch);
		if (matcher.matches()) {
			return new Qname(matcher.group(1));
		}
		throw new IllegalStateException("Malformed image id?");
	}

	private ResponseData taxonEdit(Qname taxonId, ResponseData data, String taxonSearch, HttpServletRequest req) throws Exception {
		if (!getTaxonomyDAO().getTaxonContainer().hasTaxon(taxonId)) {
			getSession(req).setFlashError(getText("unknown_taxon", req));
			return redirectTo(getConfig().baseURL()+"/admin");
		}
		TaxonomyDAO dao = getTaxonomyDAO();
		Taxon t = dao.getTaxon(taxonId);
		getTaxonImageDAO().reloadImages(t);
		boolean multiPrimary = t.getMultimedia().stream().filter(i->i.isPrimaryForTaxon()).count() > 1;
		List<String> newImages = newImages(req);
		selectedTagsFromPreferences(data, req);
		return data.setViewName("admin-taxon")
				.setData("taxon", t)
				.setData("nextTaxon", next(t, t.isSpecies(), t.isFinnish(), dao))
				.setData("prevTaxon", prev(t, t.isSpecies(), t.isFinnish(), dao))
				.setData(TAXON_SEARCH, taxonSearch)
				.setData("multiPrimary", multiPrimary)
				.setData("newImages", newImages);
	}

	private void selectedTagsFromPreferences(ResponseData data, HttpServletRequest req) {
		try {
			Preferences preferences = getPreferences(getSession(req));
			if (preferences == null) return;

			String selectedTags = preferences.get("admin_selected_tags");
			if (!given(selectedTags)) return;

			String decoded = new String(Base64.getDecoder().decode(selectedTags), StandardCharsets.UTF_8);
			JSONObject json = new JSONObject(decoded);

			// {"type":{"value":"MM.typeEnumLive","label":"Luonnossa otettu"},"sex":{"value":"MY.sexM","label":"koiras"}}
			if (json.hasKey("type")) data.setData("admin_selected_tag_type", json.getObject("type").getString("value"));
			if (json.hasKey("sex")) data.setData("admin_selected_tag_sex", json.getObject("sex").getString("value"));
			if (json.hasKey("lifeStage")) data.setData("admin_selected_tag_lifeStage", json.getObject("lifeStage").getString("value"));
			if (json.hasKey("plantLifeStage")) data.setData("admin_selected_tag_plantLifeStage", json.getObject("plantLifeStage").getString("value"));
			if (json.hasKey("side")) data.setData("admin_selected_tag_side", json.getObject("side").getString("value"));
		} catch (Exception e) {
			// something wrong with admin_selected_tags data - ignore it
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> newImages(HttpServletRequest req) {
		Object o = getSession(req).getObject(Constant.NEW_IMAGES);
		if (o == null) return null;
		return (List<String>) o;
	}

	private Taxon prev(Taxon t, boolean expectSpecies, boolean expectFinnish, TaxonomyDAO dao) {
		Taxon prev = dao.prev(t);
		while (prev != null) {
			if (expectSpecies == prev.isSpecies() && (!expectFinnish || prev.isFinnish())) {
				return prev;
			}
			prev = dao.prev(prev);
		}
		return null;
	}

	private Taxon next(Taxon t, boolean expectSpecies, boolean expectFinnish, TaxonomyDAO dao) {
		Taxon next = dao.next(t);
		while (next != null) {
			if (expectSpecies == next.isSpecies() && (!expectFinnish || next.isFinnish())) {
				return next;
			}
			next = dao.next(next);
		}
		return null;
	}

	private ResponseData singleImageEdit(Qname mediaId, ResponseData data, String imageSearch, HttpServletRequest req) throws Exception {
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
				data.setData(TAXON_ID, taxon.getId().toString());
			}
		}

		data.setViewName("admin-image")
		.setData("image", image.get())
		.setData(TAXON_SEARCH, taxonSearch)
		.setData(IMAGE_SEARCH, imageSearch);

		String uploadedBy = image.get().getMeta().getUploadedBy();
		String modifiedBy = image.get().getMeta().getModifiedBy();

		if (given(uploadedBy)) {
			String fullName = getDAO().getPersonFulName(uploadedBy);
			if (given(fullName)) data.setData("uploadedByFullName", fullName);
		}
		if (given(modifiedBy)) {
			String fullName = getDAO().getPersonFulName(modifiedBy);
			if (given(fullName)) data.setData("modifiedByFullName", fullName);
		}

		List<String> ref = new ArrayList<>();
		if (given(taxonSearch)) ref.add(TAXON_SEARCH + "=" + taxonSearch);
		if (taxon != null) ref.add(TAXON_ID + "=" + taxon.getId());
		if (given(imageSearch)) ref.add(IMAGE_SEARCH + "=" + imageSearch);

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

	private ResponseData imageSearch(String imageSearch, ResponseData data, HttpServletRequest req) throws Exception {
		List<Image> images = getTaxonImageDAO().search(imageSearch);
		if (images.size() == 1) {
			return singleImageEdit(images.get(0).getId(), data, null, req);
		}
		return data.setViewName("admin-image-select")
				.setData("results", images)
				.setData(IMAGE_SEARCH, imageSearch)
				.setData(REF, IMAGE_SEARCH+"="+imageSearch);
	}

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String imageSearch = req.getParameter(IMAGE_SEARCH);
		String taxonSearch = req.getParameter(TAXON_SEARCH);
		String taxonId = req.getParameter(TAXON_ID);
		String id = getId(req);

		if (!given(id)) {
			getSession(req).setFlashError(getText("unknown_image", req));
			return redirectTo(getConfig().baseURL()+"/admin");
		}

		Meta meta = null;
		try {
			meta = parseMeta(req);
			validate(id, meta, req);
		} catch (IllegalArgumentException e) {
			getSession(req).setFlash(e.getMessage());
			return redirectTo(getConfig().baseURL()+"/admin/"+id);
		} catch (NotFoundException e) {
			getSession(req).setFlashError(getText("unknown_image", req));
			return redirectTo(getConfig().baseURL()+"/admin");
		}

		saveMeta(id, meta);

		getSession(req).setFlashSuccess(getText("save_success", req));
		URIBuilder redirectURI = new URIBuilder(getConfig().baseURL()+"/admin/"+id);
		if (given(taxonSearch)) redirectURI.addParameter(TAXON_SEARCH, taxonSearch);
		if (given(taxonId)) redirectURI.addParameter(TAXON_ID, taxonId);
		if (given(imageSearch)) redirectURI.addParameter(IMAGE_SEARCH, imageSearch);
		return new ResponseData().setRedirectLocation(redirectURI.toString());
	}

	private void validate(String id, Meta meta, HttpServletRequest req) throws ApiException, NotFoundException, Exception {
		Optional<Media> existing = getMediaApiClient().get(MediaClass.IMAGE, id);
		if (!existing.isPresent()) throw new NotFoundException();
		Meta existingMeta = existing.get().getMeta();

		if (existingMeta.isSecret()) {
			if (!meta.getIdentifications().getTaxonIds().isEmpty()) throw validationFailure("taxonIds", "Secret media must not be made a taxon image", req);
		}
		for (String taxonId : meta.getIdentifications().getTaxonIds()) {
			if (!getTaxonomyDAO().getTaxonContainer().hasTaxon(new Qname(taxonId))) throw validationFailure("taxonIds", "unknown_taxon", req);
		}
		for (String primaryFoTaxon : meta.getPrimaryForTaxon()) {
			if (!meta.getIdentifications().getTaxonIds().contains(primaryFoTaxon)) throw validationFailure("primaryForTaxon", "Primary taxon id not found in taxon ids", req);
		}
	}

	private void saveMeta(String id, Meta meta) throws ApiException, NotFoundException {
		getMediaApiClient().update(MediaClass.IMAGE, id, meta);
	}

	private Meta parseMeta(HttpServletRequest req) {
		Meta meta = new Meta();

		User user = getUser(req);
		meta.setModifiedBy(user.getId().toString());

		meta.getCapturers().addAll(params(req, "capturers"));
		meta.setRightsOwner(param(req, "rightsOwner"));
		meta.setLicense(param(req, "license"));
		try {
			meta.setCaptureDateTime(date(req, "captureDateTime"));
		} catch (Exception e) {
			throw validationFailure("captureDateTime", "invalid_datetime", req);
		}
		params(req, "taxonIds").forEach(meta.getIdentifications()::addTaxonId);
		params(req, "verbatim").forEach(meta.getIdentifications()::addVerbatim);
		params(req, "primaryForTaxon").forEach(meta::addPrimaryForTaxon);
		meta.setType(param(req, "type"));
		params(req, "sex").forEach(meta::addSex);
		params(req, "lifeStage").forEach(meta::addLifeStage);
		params(req, "plantLifeStage").forEach(meta::addPlantLifeStage);
		meta.setSide(param(req, "side"));
		meta.setCaption(param(req, "caption"));
		params(req, "documentIds").forEach(meta::addDocumentId);
		params(req, "tags").forEach(meta::addTag);
		meta.setSortOrder(intV(req, "sortOrder"));
		meta.setFullResolutionMediaAvailable(boolV(req, "fullResolutionMediaAvailable"));
		meta.setSecret(boolV(req, "secret"));

		String fi = param(req, "taxonDescriptionCaptionFI");
		String sv = param(req, "taxonDescriptionCaptionSV");
		String en = param(req, "taxonDescriptionCaptionEN");
		if (given(fi)) meta.getTaxonDescriptionCaption().put("fi", fi);
		if (given(sv)) meta.getTaxonDescriptionCaption().put("sv", sv);
		if (given(en)) meta.getTaxonDescriptionCaption().put("en", en);
		return meta;
	}

	private Boolean boolV(HttpServletRequest req, String param) {
		String s = param(req, param);
		if (!given(s)) return null;
		try {
			return Boolean.valueOf(s);
		} catch (Exception e) {
			throw validationFailure(param, "invalid_boolean", req);
		}
	}

	private Integer intV(HttpServletRequest req, String param) {
		String s = param(req, param);
		if (!given(s)) return null;
		try {
			Integer i = Integer.valueOf(s);
			if (i < 0) throw new IllegalArgumentException();
			return i;
		} catch (Exception e) {
			throw validationFailure(param, "invalid_integer", req);
		}
	}

	private DateTime date(HttpServletRequest req, String param) {
		String s = param(req, param);
		if (!given(s)) return null;
		return DateTime.parse(s);
	}

	private String param(HttpServletRequest req, String param) {
		String val = req.getParameter(param);
		if (val == null) return null;
		val = val.trim();
		if (!given(val)) return null;
		return val;
	}

	private List<String> params(HttpServletRequest req, String param) {
		String[] rawValues = req.getParameterValues(param);
		if (rawValues == null) return Collections.emptyList();
		List<String> values = new ArrayList<>();
		for (String v : rawValues) {
			if (v == null) continue;
			v = v.trim();
			if (!given(v)) continue;
			values.add(v);
		}
		return values;
	}

	private IllegalArgumentException validationFailure(String param, String error, HttpServletRequest req) {
		String label = getText("label_"+param, req);
		String errorText = getLocalizedTexts().hasText(error) ? getText(error, req) : error;
		return new IllegalArgumentException(label + ": " + errorText);
	}

}
