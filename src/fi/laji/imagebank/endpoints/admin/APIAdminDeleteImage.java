package fi.laji.imagebank.endpoints.admin;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.services.ResponseData;
import fi.luomus.kuvapalvelu.model.MediaClass;
import fi.luomus.utils.exceptions.NotFoundException;

@WebServlet(urlPatterns = {"/admin/delete/*"})
public class APIAdminDeleteImage extends APIAdminBaseServlet {

	private static final long serialVersionUID = -1999626355155102713L;

	@Override
	protected ResponseData processDelete(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String id = getId(req);

		if (!given(id)) {
			getSession(req).setFlashError(getText("unknown_image", req));
			return status(400, res);
		}

		try {
			getMediaApiClient().delete(MediaClass.IMAGE, id);
		} catch (NotFoundException e) {
			getSession(req).setFlashError(getText("unknown_image", req));
			return status(400, res);
		}

		getSession(req).setFlashSuccess(getText("admin_delete_success", req));
		return new ResponseData("ok", "text/plain");
	}

}
