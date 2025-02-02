package fi.laji.imagebank;

import java.io.File;
import java.io.FileNotFoundException;

import fi.laji.imagebank.endpoints.ImageBankBaseServlet;
import fi.luomus.commons.config.Config;
import fi.luomus.commons.config.ConfigReader;

public class TestConfig {

	public static Config getConfig() {
		try {
			String base = System.getenv("CATALINA_HOME");
			if (base == null) base = "C:/apache-tomcat";
			File file = path(base);
			if (!file.exists()) file = path("E:/apache-tomcat-imagebank");
			if (!file.exists()) file = path(System.getProperty("user.home"));
			if (!file.exists()) throw new FileNotFoundException("Config file not found");
			System.out.println("Using test config " + file.getAbsolutePath());
			Config config = new ConfigReader(file.getAbsolutePath());
			return config;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static File path(String folder) {
		return new File(folder + File.separator + "app-conf" + File.separator + "tests_" + ImageBankBaseServlet.CONFIG_FILE);
	}
}
