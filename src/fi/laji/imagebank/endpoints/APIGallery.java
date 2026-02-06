package fi.laji.imagebank.endpoints;

import java.util.Collections;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.containers.Image;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.taxonomy.CategorizedTaxonImages;
import fi.luomus.commons.taxonomy.CategorizedTaxonImages.ImageCategory;
import fi.luomus.commons.taxonomy.Taxon;

@WebServlet(urlPatterns = {"/api/gallery/*"})
public class APIGallery extends APIBaseServlet {

	private static final long serialVersionUID = -8223461524892099190L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Qname taxonId = Qname.of(req.getParameter("taxonId"));
		String categoryId = req.getParameter("category");
		if (!taxonId.isSet() || !given(categoryId)) return status(400, res);

		Taxon taxon = getTaxonomyDAO().getTaxon(taxonId);

		List<Image> images = images(categoryId, taxon);

		return initResponseData(req).setViewName("api-gallery")
				.setData("taxon", taxon)
				.setData("category", categoryId)
				.setData("images", images);
	}

	private List<Image> images(String categoryId, Taxon taxon) {
		if ("uncategorized".equals(categoryId)) {
			return taxon.getCategorizedMultimedia().getUncategorizedImages();
		}
		CategorizedTaxonImages taxonImages = taxon.getCategorizedMultimedia();
		for (ImageCategory category : taxonImages.getCategories()) {
			if (category.getId().equals(categoryId)) {
				return category.getImages();
			}
			for (ImageCategory subcategory : category.getSubcategories()) {
				if (subcategory.getId().equals(categoryId)) {
					return subcategory.getImages();
				}
			}
		}
		return Collections.emptyList();
	}

}
