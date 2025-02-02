package fi.laji.imagebank;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zaxxer.hikari.HikariDataSource;

import fi.laji.imagebank.dao.DAOImple;
import fi.laji.imagebank.dao.DataSourceDefinition;

public class DAOTests {

	private DAOImple dao = null;
	private HikariDataSource dataSource = null;

	@Before
	public void init() {
		dataSource = DataSourceDefinition.initDataSource(TestConfig.getConfig().connectionDescription());
		dao = new DAOImple(dataSource);
	}

	@After
	public void destroy() {
		if (dataSource != null) dataSource.close();
	}

	@Test
	public void preferences() throws Exception {
		dao.savePreference("MA.123", "preference", "value");
		assertEquals("value", dao.getPreference("MA.123", "preference"));
		dao.savePreference("MA.123", "preference", "value2");
		assertEquals("value2", dao.getPreference("MA.123", "preference"));
		assertEquals(null, dao.getPreference("MA.123", "foobar"));
		dao.savePreference("MA.123", "preference2", "value3");

		assertEquals("{\"preference\":\"value2\",\"preference2\":\"value3\"}",
				dao.getPreferences("MA.123").getJson());
	}

}
