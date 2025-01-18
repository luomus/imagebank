package fi.laji.imagebank.dao;

import java.util.List;

import fi.luomus.commons.containers.Image;
import fi.luomus.commons.taxonomy.Taxon;

public interface TaxonImageDAO extends AutoCloseable {

	Taxon reloadImages(Taxon t);

	List<Image> search(String searchTerm);

}
