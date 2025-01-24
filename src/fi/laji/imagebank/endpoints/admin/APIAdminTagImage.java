package fi.laji.imagebank.endpoints.admin;

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
public class APIAdminTagImage extends APIAdminBaseServlet {

	private static final long serialVersionUID = -7542620621210286239L;

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String id = getId(req);

		if (!given(id)) {
			getSession(req).setFlashError(getText("unknown_image", req));
			return status(400, res);
		}

		Optional<Media> media = getMediaApiClient().get(MediaClass.IMAGE, id);
		if (!media.isPresent()) {
			getSession(req).setFlashError(getText("unknown_image", req));
			return status(400, res);
		}
		Meta existingMeta = media.get().getMeta();

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

}
