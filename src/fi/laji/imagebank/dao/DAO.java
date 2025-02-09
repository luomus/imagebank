package fi.laji.imagebank.dao;

import fi.laji.imagebank.models.Preferences;

public interface DAO {

	void savePreference(String userId, String preference, String value) throws Exception;

	String getPreference(String userId, String preference) throws Exception;

	Preferences getPreferences(String userId) throws Exception;

	String getPersonFulName(String personId);

}
