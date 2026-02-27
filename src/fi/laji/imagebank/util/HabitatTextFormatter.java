package fi.laji.imagebank.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.luomus.commons.containers.LocalizedText;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.taxonomy.Taxon;
import fi.luomus.commons.taxonomy.iucn.HabitatObject;
import fi.luomus.commons.utils.Utils;

public class HabitatTextFormatter {

	private final Map<String, LocalizedText> habitats;
	private final Map<String, LocalizedText> habitatTypes;

	public HabitatTextFormatter(Map<String, LocalizedText> habitats, Map<String, LocalizedText> habitatTypes) {
		this.habitats = habitats;
		this.habitatTypes = habitatTypes;
	}

	public String format(Taxon taxon, String locale) {
		if (taxon.getPrimaryHabitat() == null) return "";
		List<String> items = new ArrayList<>();
		addIfPresent(items, formatHabitat(taxon.getPrimaryHabitat(), locale));
		for (HabitatObject secondary : taxon.getSecondaryHabitats()) {
			String formatted = formatHabitat(secondary, locale);
			if (given(formatted) && !items.contains(formatted)) {
				items.add(formatted);
			}
		}
		return toHtmlList(items);
	}

	private String toHtmlList(List<String> items) {
		if (items.isEmpty()) return "";

		StringBuilder builder = new StringBuilder("<ul>");
		for (String item : items) {
			builder.append("<li>").append(item).append("</li>");
		}
		builder.append("</ul>");
		return builder.toString();
	}

	private void addIfPresent(List<String> list, String value) {
		if (given(value) && !list.contains(value)) {
			list.add(value);
		}
	}

	private String formatHabitat(HabitatObject habitat, String locale) {
		if (habitat.getHabitat() == null) return null;

		String base = resolveLocalizedText(habitats.get(habitat.getHabitat().toString()), locale);
		if (!given(base)) return null;

		StringBuilder builder = new StringBuilder(Utils.upperCaseFirst(base));
		for (Qname specifier : habitat.getHabitatSpecificTypes()) {
			if (specifier == null) continue;
			String specifierText = resolveLocalizedText(habitatTypes.get(specifier.toString()), locale);
			if (!given(specifierText)) continue;
			builder.append(" &nbsp; <i>")
			.append(specifierText)
			.append("</i>");
		}
		return builder.toString();
	}

	private String resolveLocalizedText(LocalizedText text, String locale) {
		if (text == null) return null;
		String value = text.forLocale(locale);
		if (!given(value)) return null;
		return trimAfterDash(value);
	}

	private String trimAfterDash(String text) {
		int index = text.indexOf("–");
		if (index >= 0) {
			return text.substring(index + 1).trim();
		}
		return text.trim();
	}

	private boolean given(String s) {
		return s != null && !s.trim().isEmpty();
	}

}