package fi.laji.imagebank.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.taxonomy.Taxon;
import fi.luomus.commons.utils.Cached;
import fi.luomus.commons.utils.Cached.CacheLoader;
import fi.luomus.commons.utils.Utils;

public class TaxonomyCaches {

	private static final Object LOCK = new Object();

	public static class SpeciesTerms {
		Qname groupId;
		String order;
		boolean onlyFinnish;
		List<Qname> taxonRanks;
		public int page;
		public int pageSize;
		public SpeciesTerms(HttpServletRequest req) {
			groupId = new Qname(getId(req));
			order = req.getParameter("order");
			onlyFinnish = "taxa_finnish".equals(req.getParameter("taxa"));
			taxonRanks = taxonRanks(req.getParameter("taxonRanks"));
			page = iVal(req.getParameter("page"));
			pageSize = iVal(req.getParameter("pageSize"));
		}
		public boolean valid() {
			if (!given(groupId, order, page, pageSize)) return false;
			if (pageSize > 1000) return false;
			return true;
		}
		private int iVal(String parameter) {
			try {
				return Integer.valueOf(parameter);
			} catch (Exception e) {
				return -1;
			}
		}
		private List<Qname> taxonRanks(String parameter) {
			if (!given(parameter)) return Collections.emptyList();
			return Utils.list(parameter.split(",")).stream().map(s->new Qname(s)).collect(Collectors.toList());
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
			result = prime * result + (onlyFinnish ? 1231 : 1237);
			result = prime * result + ((taxonRanks == null) ? 0 : taxonRanks.hashCode());
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
			SpeciesTerms other = (SpeciesTerms) obj;
			if (groupId == null) {
				if (other.groupId != null)
					return false;
			} else if (!groupId.equals(other.groupId))
				return false;
			if (onlyFinnish != other.onlyFinnish)
				return false;
			if (taxonRanks == null) {
				if (other.taxonRanks != null)
					return false;
			} else if (!taxonRanks.equals(other.taxonRanks))
				return false;
			return true;
		}
	}

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
		totalSpeciesCache.invalidateAll();
		taxonomicOrderChain = null;
		alphaOrderChain = null;
	}

	public List<Taxon> getTree(TreeTerms terms) {
		return treeCache.get(terms);
	}

	private Cached<TreeTerms, List<Taxon>> treeCache = new Cached<>(treeLoader(), 24, TimeUnit.HOURS, 500);

	private CacheLoader<TreeTerms, List<Taxon>> treeLoader() {
		return new CacheLoader<TaxonomyCaches.TreeTerms, List<Taxon>>() {
			@Override
			public List<Taxon> load(TreeTerms terms) {
				if ("order_alphabetic".equals(terms.order)) {
					return alphaOrder(terms);
				}
				return taxonomicOrder(terms);
			}

			private List<Taxon> taxonomicOrder(TreeTerms terms) {
				Set<Qname> acceptedRanks = acceptedRanks(terms);
				Set<Taxon> taxa = new HashSet<>();
				for (Taxon t : dao.getTaxonContainer().getAll()) {
					if (t.getInformalTaxonGroupsNoOrder().contains(terms.groupId) && terms.rank.equals(t.getTaxonRank())) {
						if (!terms.onlyFinnish || t.isFinnish()) {
							taxa.add(t);
							for (Qname parentRank : acceptedRanks) {
								Taxon parent = t.getParentOfRank(parentRank);
								if (parent != null) taxa.add(parent);
							}
						}
					}
				}
				List<Taxon> sorted = new ArrayList<>(taxa);
				Collections.sort(sorted, Taxon.TAXONOMIC_ORDER_COMPARATOR);
				return sorted;
			}

			private List<Taxon> alphaOrder(TreeTerms terms) {
				List<Taxon> taxa = new ArrayList<>();
				for (Taxon t : dao.getTaxonContainer().getAll()) {
					if (t.getInformalTaxonGroupsNoOrder().contains(terms.groupId) && terms.rank.equals(t.getTaxonRank())) {
						if (!terms.onlyFinnish || t.isFinnish()) {
							taxa.add(t);
						}
					}
				}
				Collections.sort(taxa, Taxon.TAXON_ALPHA_COMPARATOR);
				return taxa;
			}

			private Set<Qname> acceptedRanks(TreeTerms terms) {
				List<Qname> rankOrder = Arrays.asList(
						new Qname("MX.order"),
						new Qname("MX.suborder"),
						new Qname("MX.superfamily"),
						new Qname("MX.family"),
						new Qname("MX.subfamily"),
						new Qname("MX.tribe"),
						new Qname("MX.genus")
						);
				int index = rankOrder.indexOf(terms.rank);
				Set<Qname> ranks = new HashSet<>(rankOrder.subList(0, index + 1));
				ranks.remove(new Qname("MX.tribe"));
				ranks.add(terms.rank);
				return ranks;
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
			if (o == null) return false;
			if (o.getClass() == int.class) {
				return given((int)o);
			}
			if (!given(o)) return false;
		}
		return true;
	}

	private static boolean given(Object o) {
		return o != null && !o.toString().isEmpty();
	}

	private static boolean given(int i) {
		return i > 0;
	}

	private static class Node {
		private final Taxon self;
		private Node prev;
		private Node next;
		public Node(Taxon self) {
			this.self = self;
		}
	}

	private Map<Taxon, Node> taxonomicOrderChain = null;

	private Map<Taxon, Node> getTaxonomicOrderChain() {
		if (taxonomicOrderChain == null) {
			synchronized (LOCK) {
				if (taxonomicOrderChain == null) {
					taxonomicOrderChain = initChain(Taxon.TAXONOMIC_ORDER_COMPARATOR);
				}
			}
		}
		return taxonomicOrderChain;
	}

	private Map<Taxon, Node> alphaOrderChain = null;

	private Map<Taxon, Node> getAlphaOrderChain() {
		if (alphaOrderChain == null) {
			synchronized (LOCK) {
				if (alphaOrderChain == null) {
					alphaOrderChain = initChain(Taxon.TAXON_ALPHA_COMPARATOR);
				}
			}
		}
		return alphaOrderChain;
	}

	private Map<Taxon, Node> initChain(Comparator<? super Taxon> comparator) {
		List<Taxon> all = new ArrayList<>(dao.getTaxonContainer().getAll());
		Map<Taxon, Node> chain = new LinkedHashMap<>(all.size());
		Collections.sort(all, comparator);
		Node prevNode = null;
		for (Taxon currentTaxon : all) {
			Node currentNode = new Node(currentTaxon);
			currentNode.prev = prevNode;
			if (prevNode != null) prevNode.next = currentNode;
			chain.put(currentTaxon, currentNode);
			prevNode = currentNode;
		}
		return chain;
	}

	public Taxon next(Taxon self) {
		Node nextNode = getTaxonomicOrderChain().get(self).next;
		if (nextNode != null) return nextNode.self;
		return null;
	}

	public Taxon prev(Taxon self) {
		Node prevNode = getTaxonomicOrderChain().get(self).prev;
		if (prevNode != null) return prevNode.self;
		return null;
	}

	public List<Taxon> getSpecies(SpeciesTerms terms) {
		Map<Taxon, Node> chain = getChain(terms);
		int skip = (terms.page - 1) * terms.pageSize;
		return chain.keySet().stream()
				.filter(taxon->matches(taxon, terms))
				.skip(skip)
				.limit(terms.pageSize)
				.collect(Collectors.toList());
	}

	private Map<Taxon, Node> getChain(SpeciesTerms terms) {
		Map<Taxon, Node> chain = "order_alphabetic".equals(terms.order) ? getAlphaOrderChain() : getTaxonomicOrderChain();
		return chain;
	}

	private boolean matches(Taxon taxon, SpeciesTerms terms) {
		if (!taxon.isSpecies()) return false;
		if (!taxon.getInformalTaxonGroupsNoOrder().contains(terms.groupId)) return false;
		if (terms.onlyFinnish && !taxon.isFinnish()) return false;
		if (!terms.taxonRanks.isEmpty() && !terms.taxonRanks.contains(taxon.getTaxonRank())) return false;
		return true;
	}

	private Cached<SpeciesTerms, Integer> totalSpeciesCache = new Cached<>(speciesCountLoader(), 24, TimeUnit.HOURS, 500);

	public int getSpeciesCount(SpeciesTerms terms) {
		return totalSpeciesCache.get(terms);
	}

	private CacheLoader<SpeciesTerms, Integer> speciesCountLoader() {
		return new CacheLoader<SpeciesTerms, Integer>() {
			@Override
			public Integer load(SpeciesTerms terms) {
				return (int) getTaxonomicOrderChain().keySet().stream().filter(taxon->matches(taxon, terms)).count();
			}
		};
	}

}
