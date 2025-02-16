package fi.laji.imagebank.dao;

import java.util.List;

import fi.laji.imagebank.dao.TaxonomyCaches.TreeTerms;
import fi.luomus.commons.taxonomy.Taxon;

public interface TaxonomyDAO extends fi.luomus.commons.taxonomy.TaxonomyDAO {

	List<Taxon> getTree(TreeTerms terms);

}
