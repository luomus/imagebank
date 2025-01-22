package fi.laji.imagebank.endpoints;

import java.util.Optional;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.json.JSONObject;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.kuvapalvelu.model.Media;
import fi.luomus.kuvapalvelu.model.MediaClass;
import fi.luomus.kuvapalvelu.model.Meta;

@WebServlet(urlPatterns = {"/admin/tag/*"})
public class APIAdminTagImage extends Admin {

	private static final long serialVersionUID = -7542620621210286239L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String id = getId(req);

		if (!given(id)) {
			getSession(req).setFlashError(getText("unknown_image", req));
			return status(400, res);
		}

		Meta existingMeta = null;
		try {
			Optional<Media> media = getMediaApiClient().get(MediaClass.IMAGE, id);
			if (!media.isPresent()) {
				getSession(req).setFlashError(getText("unknown_image", req));
				return status(400, res);
			}
			existingMeta = media.get().getMeta();
		} catch (Exception e) {
			getSession(req).setFlashError(e.getMessage());
			return status(400, res);
		}

		// {"type":{"value":"MM.typeEnumLive","label":"Luonnossa otettu"},"sex":{"value":"MY.sexM","label":"koiras"}}
		JSONObject json = new JSONObject(readBody(req));

		if (json.hasKey("type")) existingMeta.setType(json.getObject("type").getString("value"));
		if (json.hasKey("sex")) existingMeta.addSex(json.getObject("sex").getString("value"));
		if (json.hasKey("lifeStage")) existingMeta.addLifeStage(json.getObject("lifeStage").getString("value"));
		if (json.hasKey("plantLifeStage")) existingMeta.addPlantLifeStage(json.getObject("plantLifeStage").getString("value"));
		if (json.hasKey("side")) existingMeta.setSide(json.getObject("side").getString("value"));

		getMediaApiClient().update(MediaClass.IMAGE,id, existingMeta);

		return new ResponseData("ok", "text/plain");
	}

	@Override
	protected ResponseData processDelete(HttpServletRequest req, HttpServletResponse res) throws Exception {
		throw new UnsupportedOperationException();
	}

}
