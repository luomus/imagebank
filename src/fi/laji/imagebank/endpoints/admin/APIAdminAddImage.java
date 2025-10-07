package fi.laji.imagebank.endpoints.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import fi.laji.imagebank.models.User;
import fi.laji.imagebank.util.Constant;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.session.SessionHandler;
import fi.luomus.commons.taxonomy.Taxon;
import fi.luomus.kuvapalvelu.model.Meta;

@WebServlet(urlPatterns = {"/admin/add/*"})
@MultipartConfig(maxFileSize = 50L * 1024 * 1024, maxRequestSize = 80L * 1024 * 1024)
public class APIAdminAddImage extends APIAdminBaseServlet {

	private static final long serialVersionUID = 3915062778420509292L;

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Collection<Part> fileParts;
		try {
			fileParts = req.getParts().stream()
					.filter(part -> "images".equals(part.getName()) && part.getSize() > 0)
					.collect(Collectors.toList());

			if (fileParts.isEmpty()) {
				getSession(req).setFlashError(getText("file_missing", req));
				return status(400, res);
			}
		} catch (IllegalStateException e) {
			getSession(req).setFlashError(getText("file_too_large", req));
			return status(400, res);
		}

		String taxonId = req.getParameter("taxonId");
		String capturer = req.getParameter("capturer");
		String rightsOwner = req.getParameter("rightsOwner");
		String userSubmittedLicense = req.getParameter("license");
		String license = !given(userSubmittedLicense) ? "MZ.intellectualRightsCC-BY-4.0" : userSubmittedLicense;

		User user = getUser(req);

		List<String> uploadedIds = new ArrayList<>();

		for (Part filePart : fileParts) {
			String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
			try (InputStream stream = filePart.getInputStream()) {
				Meta meta = meta(req, taxonId, capturer, rightsOwner, license, user);
				String id = getMediaApiClient().uploadImage(stream, fileName, meta);
				uploadedIds.add(id);
			} catch (IOException e) {
				getSession(req).setFlashError(getText("file_upload_failed", req));
				return status(500, res);
			}
		}

		if (taxonId != null) {
			reloadImages(taxonId);
		}

		SessionHandler ses = getSession(req);
		ses.setFlashSuccess(getText("admin_image_add_success", req));
		ses.setObject(Constant.NEW_IMAGES, uploadedIds);

		if (given(userSubmittedLicense)) {
			String currentLicense = getUsersDefaultLicense(req);
			if (!userSubmittedLicense.equals(currentLicense)) {
				getDAO().savePreference(user.getId().toString(), Constant.USER_DEFAULT_LICENSE, userSubmittedLicense);
				invalidatePreferences(req);
			}

		}

		if (uploadedIds.size() == 1) {
			return new ResponseData(uploadedIds.get(0), "text/plain");
		}
		if (taxonId == null) throw new IllegalStateException("Impossible state");

		return new ResponseData(taxonId, "text/plain");
	}

	private void reloadImages(String taxonId) throws Exception {
		getTaxonImageDAO().reloadImages(getTaxon(taxonId));
	}

	private Taxon getTaxon(String taxonId) throws Exception {
		return getTaxonomyDAO().getTaxon(new Qname(taxonId));
	}

	private Meta meta(HttpServletRequest req, String taxonId, String capturer, String rightsOwner, String license, User user) {
		String secret = req.getParameter("secret");
		Meta meta = new Meta();
		if (taxonId != null) {
			meta.getIdentifications().addTaxonId(taxonId);
		}
		if ("on".equals(secret)) {
			meta.setSecret(true);
		}
		meta.setUploadedBy(user.getId().toString());
		meta.addCapturer(!given(capturer) ? user.getFullName() : capturer);
		meta.setRightsOwner(!given(rightsOwner) ? "Luomus" : rightsOwner);
		meta.setLicense(!given(license) ? "MZ.intellectualRightsCC-BY-4.0" : license);
		return meta;
	}

}
