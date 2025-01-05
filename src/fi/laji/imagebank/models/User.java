package fi.laji.imagebank.models;

import java.io.Serializable;
import java.util.Set;

import fi.luomus.commons.containers.rdf.Qname;

public class User implements Serializable {

	private static final long serialVersionUID = 4374799732435367777L;

	public static enum Type { ADMIN, CURATOR, NORMAL }

	private final Qname id;
	private final Type type;
	private final String fullName;

	public User(String userQname, Set<String> roles, String fullName) {
		this(userQname, typeFrom(roles), fullName);
	}

	public User(String userQname, Type type, String fullName) {
		this.id = new Qname(userQname);
		this.type = type;
		this.fullName = fullName;
		if (!id.isSet()) throw new IllegalArgumentException("No user id");
		if (fullName == null || fullName.isEmpty()) throw new IllegalArgumentException("No user fullname");
	}

	private static Type typeFrom(Set<String> roles) {
		if (roles == null || roles.isEmpty()) return Type.NORMAL;
		if (roles.contains("MA.admin")) return Type.ADMIN;
		if (roles.contains("MA.taxonEditorUser")) return Type.ADMIN;
		if (roles.contains("MA.taxonEditorUserDescriptionWriterOnly")) return Type.CURATOR;
		if (roles.contains("MA.luomusSpaceCalendarUser")) return Type.CURATOR;
		return Type.NORMAL;
	}

	public Qname getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public String getFullName() {
		return fullName;
	}

	public boolean isAdmin() {
		return type == Type.ADMIN;
	}

}
