package fi.laji.imagebank.endpoints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.laji.imagebank.models.User;
import fi.luomus.commons.containers.InformalTaxonGroup;
import fi.luomus.commons.containers.LocalizedText;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.json.JSONArray;
import fi.luomus.commons.json.JSONObject;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.taxonomy.CategorizedTaxonImages;
import fi.luomus.commons.taxonomy.CategorizedTaxonImages.SingleCategoryDef;
import fi.luomus.commons.utils.Utils;

@WebServlet(urlPatterns = {"/browse/*"})
public class Browse extends ImageBankBaseServlet {

	private static final List<String> DEFAULT_TAXON_RANKS = Utils.list("MX.aggregate", "MX.species");
	private static final long serialVersionUID = 8621449842296848597L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String changeGroup = req.getParameter("change");
		if ("true".equals(changeGroup)) {
			return selectGroup(req);
		}

		String groupId = getId(req);
		if (notGiven(groupId)) {
			groupId = groupIdFromPreferences(req, groupId);
			if (given(groupId)) {
				InformalTaxonGroup group = getTaxonomyDAO().getInformalTaxonGroups().get(groupId);
				if (group != null) {
					return redirectTo(getConfig().baseURL()+"/"+slug(group, req) +"/");
				}
			}
		}

		Map<String, InformalTaxonGroup> groups = getTaxonomyDAO().getInformalTaxonGroups();
		InformalTaxonGroup group = groups.get(groupId);

		if (group == null) {
			return selectGroup(req);
		}

		String path = req.getPathInfo();
		if (Utils.countNumberOf("/", path) <= 1) {
			String slug = slug(group, req);
			return redirectTo(getConfig().baseURL()+"/browse/"+slug+"/"+groupId);
		}

		List<SingleCategoryDef> defs = CategorizedTaxonImages.getDefs(getGroupIds(group, groups));

		return initResponseData(req).setViewName("browse")
				.setData("group", group)
				.setData("groupId", groupId)
				.setData("taxonGroups", filteredTaxonGroups())
				.setData("speciesTaxonRanks", speciesTaxonRanks())
				.setData("defaultTaxonRanks", DEFAULT_TAXON_RANKS)
				.setData("defs", defs);
	}

	private ResponseData selectGroup(HttpServletRequest req) throws Exception {
		ResponseData data = initResponseData(req);
		String groupsJson = toJson(filteredTaxonGroups(), data.getDefaultLocale());
		return data
				.setViewName("browse-groupselect")
				.setData("taxonGroupsJson", groupsJson);
	}

	private String toJson(Collection<InformalTaxonGroup> groups, String locale) {
		JSONArray json = new JSONArray();
		for (InformalTaxonGroup group : groups) {
			JSONArray parentQnames = new JSONArray();
			group.getParents().stream().map(qname->qname.toString()).forEach(parentQnames::appendString);
			json.appendObject(new JSONObject()
					.setString("qname", group.getQname().toString())
					.setString("name", group.getName().forLocale(locale))
					.setArray("parentQnames", parentQnames));
		}
		return json.toString();
	}

	private List<Qname> getGroupIds(InformalTaxonGroup group, Map<String, InformalTaxonGroup> groups) {
		List<Qname> ids = new ArrayList<>();
		ids.add(group.getQname());
		for (Qname parentId : group.getParents()) {
			InformalTaxonGroup parent = groups.get(parentId.toString());
			if (parent != null) {
				ids.addAll(getGroupIds(parent, groups));
			}
		}
		return ids;
	}

	private String groupIdFromPreferences(HttpServletRequest req, String groupId) throws Exception {
		User user = getUser(req);
		if (user != null) {
			groupId = getDAO().getPreference(user.getId().toString(), "group");
		}
		return groupId;
	}

	private String slug(InformalTaxonGroup group, HttpServletRequest req) {
		if (group == null) return null;
		String name = group.getName(getLocale(req));
		String slug = name.toLowerCase()
				.replace(" and ", "-")
				.replace(" ja ", "-")
				.replace(" och ", "-")
				.replace(" ym.", "-")
				.replace(" ", "-")
				.replace("(", "-")
				.replace(")", "-")
				.replace("ä", "a")
				.replace("ö", "o")
				.replace("å", "a")
				.replace("é", "e")
				.replace(",", "-")
				.replace(".", "-");
		while (slug.contains("--")) {
			slug = slug.replace("--", "-");
		}
		while (slug.endsWith("-")) {
			slug = slug.substring(0, slug.length()-1);
		}
		return slug;
	}

	private static Map<String, LocalizedText> speciesTaxonRanks = null;

	private Map<String, LocalizedText> speciesTaxonRanks() {
		if (speciesTaxonRanks == null) {
			Map<String, LocalizedText> taxonRanks = new LinkedHashMap<>();
			boolean include = false;
			for (Map.Entry<String, LocalizedText> e : getTaxonomyDAO().getTaxonRankLabels().entrySet()) {
				if (e.getKey().equals("MX.aggregate")) include = true;
				if (include) {
					taxonRanks.put(e.getKey(), e.getValue());
				}
			}
			speciesTaxonRanks = taxonRanks;
		}
		return speciesTaxonRanks;
	}

	private static Collection<InformalTaxonGroup> groups = null;

	private Collection<InformalTaxonGroup> filteredTaxonGroups() throws Exception {
		Map<String, InformalTaxonGroup> allGroups = getTaxonomyDAO().getInformalTaxonGroups();
		if ("a".equals("a") || groups == null) { // XXX
			Map<String, InformalTaxonGroup> map = new LinkedHashMap<>(allGroups);
			remove("MVL.1004", map, allGroups); // simmahäntäiset ja toukkasukahäntäiset
			removeChild("MVL.343", map, allGroups); // putkilokasvit children
			removeChild("MVL.2", map, allGroups); // nisäkkäät children
			removeChild("MVL.27", map, allGroups); // kalat children
			removeChild("MVL.26", map, allGroups); // matelijat ja sammakkoeläimet children
			removeChild("MVL.39", map, allGroups); // äyriäiset children
			removeChild("MVL.1", map, allGroups); // linnut children
			removeChild("MVL.22", map, allGroups); // levät children
			removeChild("MVL.37", map, allGroups); // tuhatjalkaiset
			flat("MVL.222", map); // vesihyönteiset
			flat("MVL.41", map); // muut organismit
			flat("MVL.241", map); // nilvemadot
			remove("MVL.441", map, allGroups); // bakteerit
			remove("MVL.442", map, allGroups); // virukset
			groups = map.values();
		}
		for (InformalTaxonGroup g : allGroups.values()) {
			System.out.println(g.getName("fi") + ": " + g.getQname());
		}
		return groups;
	}

	private void flat(String id, Map<String, InformalTaxonGroup> map) {
		map.remove(id);
	}

	private void remove(String id, Map<String, InformalTaxonGroup> map, Map<String, InformalTaxonGroup> allGroups) {
		map.remove(id);
		removeChild(id, map, allGroups);
	}

	private void removeChild(String id, Map<String, InformalTaxonGroup> map, Map<String, InformalTaxonGroup> allGroups) {
		map.entrySet().removeIf(e -> allParents(e.getValue(), allGroups).contains(id));
	}

	private Set<String> allParents(InformalTaxonGroup group, Map<String, InformalTaxonGroup> allGroups) {
		Set<String> parents = new HashSet<>();
		if (group == null) return parents;
		for (Qname parentId : group.getParents()) {
			parents.add(parentId.toString());
			parents.addAll(allParents(allGroups.get(parentId.toString()), allGroups));
		}
		return parents;
	}

}
