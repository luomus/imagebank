package fi.laji.imagebank.endpoints;

import java.io.InputStream;
import java.nio.file.Paths;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import fi.laji.imagebank.models.User;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.kuvapalvelu.model.Meta;

@WebServlet(urlPatterns = {"/admin/add/*"})
@MultipartConfig(maxFileSize = 50L * 1024 * 1024, maxRequestSize = 80L * 1024 * 1024)
public class APIAdminAddImage extends APIAdminBaseServlet {

	private static final long serialVersionUID = 3915062778420509292L;

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Part filePart = null;
		try {
			filePart = req.getPart("image");
			if (filePart == null || filePart.getSize() == 0) {
				getSession(req).setFlashError(getText("file_missing", req));
				return status(400, res);
			}
		} catch (IllegalStateException e) {
			getSession(req).setFlashError(getText("file_too_large", req));
			return status(400, res);
		}
		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		InputStream stream = filePart.getInputStream();

		Meta meta = meta(req);

		String id = getMediaApiClient().uploadImage(stream, fileName, meta);

		getSession(req).setFlashSuccess(getText("admin_image_add_success", req));
		return new ResponseData(id, "text/plain");
	}

	private Meta meta(HttpServletRequest req) {
		String taxonId = req.getParameter("taxonId");
		String secret = req.getParameter("secret");
		Meta meta = new Meta();
		if (taxonId != null) {
			meta.getIdentifications().addTaxonId(taxonId);
		}
		if ("on".equals(secret)) {
			meta.setSecret(true);
		}
		User user = getUser(req);
		meta.setLicense("MZ.intellectualRightsCC-BY-4.0");
		meta.setUploadedBy(user.getId().toString());
		meta.setRightsOwner("Luomus");
		meta.addCapturer(user.getFullName());
		return meta;
	}

}
