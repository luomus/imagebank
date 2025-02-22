package fi.laji.imagebank.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.taxonomy.Taxon;
import fi.luomus.commons.utils.Cached;
import fi.luomus.commons.utils.Cached.CacheLoader;

public class TaxonomyCaches {

	public static class TreeTerms {
		Qname groupId;
		Qname rank;
		String order;
		boolean onlyFinnish;
		public TreeTerms(HttpServletRequest req) {
			groupId = new Qname(getId(req));
			rank = new Qname(req.getParameter("rank"));
			order = req.getParameter("order");
			onlyFinnish = "taxa_finnish".equals(req.getParameter("taxa"));
		}
		public boolean valid() {
			return given(groupId, rank, order);
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
			result = prime * result + (onlyFinnish ? 1231 : 1237);
			result = prime * result + ((order == null) ? 0 : order.hashCode());
			result = prime * result + ((rank == null) ? 0 : rank.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TreeTerms other = (TreeTerms) obj;
			if (groupId == null) {
				if (other.groupId != null)
					return false;
			} else if (!groupId.equals(other.groupId))
				return false;
			if (onlyFinnish != other.onlyFinnish)
				return false;
			if (order == null) {
				if (other.order != null)
					return false;
			} else if (!order.equals(other.order))
				return false;
			if (rank == null) {
				if (other.rank != null)
					return false;
			} else if (!rank.equals(other.rank))
				return false;
			return true;
		}
	}

	private final TaxonomyDAOImple dao;

	public TaxonomyCaches(TaxonomyDAOImple dao) {
		this.dao = dao;
	}

	public void clearCaches() {
		treeCache.invalidateAll();
	}

	public List<Taxon> getTree(TreeTerms terms) {
		return treeCache.get(terms);
	}

	private Cached<TreeTerms, List<Taxon>> treeCache = new Cached<>(treeLoader(), 24, TimeUnit.HOURS, 500);

	private CacheLoader<TreeTerms, List<Taxon>> treeLoader() {
		return new CacheLoader<TaxonomyCaches.TreeTerms, List<Taxon>>() {
			@Override
			public List<Taxon> load(TreeTerms terms) {
				List<Taxon> taxa = new ArrayList<>();
				for (Taxon t : dao.getTaxonContainer().getAll()) {
					if (t.getInformalTaxonGroupsNoOrder().contains(terms.groupId) && terms.rank.equals(t.getTaxonRank())) {
						if (!terms.onlyFinnish || t.isFinnish()) taxa.add(t);
					}
				}
				if ("order_taxonomic".equals(terms.order)) {
					Collections.sort(taxa, Taxon.TAXONOMIC_ORDER_COMPARATOR);
				} else {
					Collections.sort(taxa, Taxon.TAXON_ALPHA_COMPARATOR);
				}
				return taxa;
			}
		};
	}

	private static String getId(HttpServletRequest req) {
		String path = req.getPathInfo();
		if (path == null || path.equals("/")) {
			return "";
		}
		String qname = path.substring(path.lastIndexOf("/")+1);
		return qname;
	}

	private static boolean given(Object ...objects) {
		for (Object o : objects) {
			if (!given(o)) return false;
		}
		return true;
	}

	private static boolean given(Object o) {
		return o != null && !o.toString().isEmpty();
	}

}
