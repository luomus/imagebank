package fi.laji.imagebank.dao;

import java.util.Map;

import fi.luomus.commons.containers.LocalizedText;

public interface LicenseNameProvider {

	Map<String, LocalizedText> getLicenseFullnames();

}
