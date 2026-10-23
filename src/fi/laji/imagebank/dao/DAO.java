package fi.laji.imagebank.dao;

import java.util.List;

import fi.laji.imagebank.models.Preferences;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.json.JSONObject;

public interface DAO {

	void savePreference(String userId, String preference, String value) throws Exception;

	String getPreference(String userId, String preference) throws Exception;

	Preferences getPreferences(String userId) throws Exception;

	String getPersonFulName(String personId);

	void markTaxonModified(String taxonId) throws Exception;

	List<String> getModifiedTaxa() throws Exception;

	void clearModifiedTaxa(int hoursToKeep) throws Exception;

	JSONObject getTaxonNameSearch(TaxonNameSearchParameters params);

	public static class TaxonNameSearchParameters {
		private final String query;
		private final Qname informalTaxonGroup;
		public TaxonNameSearchParameters(String query, Qname informalTaxonGroup) {
			this.query = query;
			this.informalTaxonGroup = informalTaxonGroup;
		}
		public String getQuery() {
			return query;
		}
		public Qname getInformalTaxonGroup() {
			return informalTaxonGroup;
		}
	}

}
