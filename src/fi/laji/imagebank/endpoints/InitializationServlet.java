package fi.laji.imagebank.endpoints;

import javax.servlet.annotation.WebServlet;

import fi.luomus.commons.containers.rdf.Qname;

@WebServlet(name="ThreadStarterServlet", loadOnStartup=1, urlPatterns={"/init"})
public class InitializationServlet extends ImageBankBaseServlet {

	private static final long serialVersionUID = -7554833707007928043L;

	@Override
	protected void applicationInit() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println(" === ImageBank STARTING UP  === ");
				try {
					getTaxonomyDAO().getTaxon(new Qname("MX.1"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		t.setName("Initialization Thread");
		t.start();
	}

}
