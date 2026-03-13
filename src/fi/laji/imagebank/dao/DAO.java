package fi.laji.imagebank.dao;

import java.util.List;

import fi.laji.imagebank.models.Preferences;

public interface DAO {

	void savePreference(String userId, String preference, String value) throws Exception;

	String getPreference(String userId, String preference) throws Exception;

	Preferences getPreferences(String userId) throws Exception;

	String getPersonFulName(String personId);

	void markTaxonModified(String taxonId) throws Exception;

	List<String> getModifiedTaxa() throws Exception;

	void clearModifiedTaxa(int hoursToKeep) throws Exception;


}
