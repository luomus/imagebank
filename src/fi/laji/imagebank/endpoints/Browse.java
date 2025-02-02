package fi.laji.imagebank.endpoints;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.containers.InformalTaxonGroup;
import fi.luomus.commons.containers.LocalizedText;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.utils.Utils;

@WebServlet(urlPatterns = {"/browse/*"})
public class Browse extends ImageBankBaseServlet {

	private static final List<String> DEFAULT_TAXON_RANKS = Utils.list("MX.aggregate", "MX.species");
	private static final long serialVersionUID = 8621449842296848597L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Collection<InformalTaxonGroup> groups = getTaxonomyDAO().getInformalTaxonGroups().values();
		// TODO REMOVE SOME groups here

		return initResponseData(req).setViewName("browse")
				.setData("taxonGroups", groups)
				.setData("speciesTaxonRanks", speciesTaxonRanks())
				.setData("defaultTaxonRanks", DEFAULT_TAXON_RANKS);
	}

	private static Map<String, LocalizedText> speciesTaxonRanks = null;

	private Map<String, LocalizedText> speciesTaxonRanks() {
		if (speciesTaxonRanks == null) {
			Map<String, LocalizedText> taxonRanks = new LinkedHashMap<>();
			boolean include = false;
			for (Map.Entry<String, LocalizedText> e : getTaxonomyDAO().getTaxonRankLabels().entrySet()) {
				if (e.getKey().equals("MX.aggregate")) include = true;
				if (include) {
					taxonRanks.put(e.getKey(), e.getValue());
				}
			}
			speciesTaxonRanks = taxonRanks;
		}
		return speciesTaxonRanks;
	}

}
