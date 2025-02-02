package fi.laji.imagebank.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.zaxxer.hikari.HikariDataSource;

import fi.laji.imagebank.models.Preferences;
import fi.luomus.commons.db.connectivity.SimpleTransactionConnection;
import fi.luomus.commons.db.connectivity.TransactionConnection;
import fi.luomus.commons.utils.Utils;

public class DAOImple implements DAO {

	private final HikariDataSource dataSource;

	public DAOImple(HikariDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void savePreference(String userId, String preference, String value) throws Exception {
		PreparedStatement p = null;
		try (TransactionConnection con = new SimpleTransactionConnection(dataSource.getConnection())) {
			con.startTransaction();
			p = con.prepareStatement(" DELETE FROM preferences WHERE userid = ? AND preference = ? ");
			p.setString(1, userId);
			p.setString(2, preference);
			p.execute();
			p.close();
			p = con.prepareStatement(" INSERT INTO preferences (userid, preference, value) VALUES (?, ?, ?) ");
			p.setString(1, userId);
			p.setString(2, preference);
			p.setString(3, value);
			p.execute();
			con.commitTransaction();
		} finally {
			Utils.close(p);
		}
	}

	@Override
	public String getPreference(String userId, String preference) throws Exception {
		PreparedStatement p = null;
		ResultSet rs = null;
		try (TransactionConnection con = new SimpleTransactionConnection(dataSource.getConnection())) {
			p = con.prepareStatement(" SELECT value FROM preferences WHERE userid = ? AND preference = ? ");
			p.setString(1, userId);
			p.setString(2, preference);
			rs = p.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
			return null;
		} finally {
			Utils.close(p, rs);
		}
	}

	@Override
	public Preferences getPreferences(String userId) throws Exception {
		if (userId == null) return null;
		Preferences preferences = new Preferences();
		PreparedStatement p = null;
		ResultSet rs = null;
		try (TransactionConnection con = new SimpleTransactionConnection(dataSource.getConnection())) {
			p = con.prepareStatement(" SELECT preference, value FROM preferences WHERE userid = ? ORDER BY preference ");
			p.setString(1, userId.toString());
			rs = p.executeQuery();
			while (rs.next()) {
				preferences.put(rs.getString(1), rs.getString(2));
			}
			return preferences;
		} finally {
			Utils.close(p, rs);
		}
	}

}
