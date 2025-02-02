package fi.laji.imagebank.endpoints;

import java.util.Collection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.containers.InformalTaxonGroup;
import fi.luomus.commons.services.ResponseData;

@WebServlet(urlPatterns = {"/browse/*"})
public class Browse extends ImageBankBaseServlet {

	private static final long serialVersionUID = 8621449842296848597L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Collection<InformalTaxonGroup> groups = getTaxonomyDAO().getInformalTaxonGroups().values();
		// TODO REMOVE SOME groups here

		return initResponseData(req).setViewName("browse")
				.setData("taxonGroups", groups);
	}

}
