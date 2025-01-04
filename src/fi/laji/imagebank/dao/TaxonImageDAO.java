package fi.laji.imagebank.dao;

import fi.luomus.commons.taxonomy.Taxon;

public interface TaxonImageDAO extends AutoCloseable {

	Taxon reloadImages(Taxon t);

}
