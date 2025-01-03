package fi.laji.imagebank;

import org.junit.Test;

import fi.luomus.commons.languagesupport.LanguageFileReader;

public class TextsTest {

	@Test
	public void all_locales_exist() throws Exception {
		new LanguageFileReader(TestConfig.getConfig()).readUITexts();
	}

}
