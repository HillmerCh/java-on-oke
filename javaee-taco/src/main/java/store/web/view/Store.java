package store.web.view;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import store.model.entity.Taco;

@Named
@SessionScoped
public class Store implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private String baseUri;
	private transient Client client;

	@NotNull
	@NotEmpty
	protected String name;
	@NotNull
	protected Double price;
	protected List<Taco> tacoList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public List<Taco> getTacoList() {
		return tacoList;
	}

	@PostConstruct
	private void init() {
		try {
			InetAddress inetAddress = InetAddress.getByName(
					((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
							.getServerName());

			baseUri = FacesContext.getCurrentInstance().getExternalContext().getRequestScheme() + "://"
					+ inetAddress.getHostName() + ":"
					+ FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort()
					+ "/javaee-taco/rest/tacos";
			this.client = ClientBuilder.newClient();
			this.getAllTacos();
		} catch (IllegalArgumentException | NullPointerException | WebApplicationException | UnknownHostException ex) {
			logger.severe("Processing of HTTP response failed.");
			ex.printStackTrace();
		}
	}

	private void getAllTacos() {
		this.tacoList = this.client.target(this.baseUri).path("/").request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Taco>>() {
				});
	}

	public void addTaco() {
		Taco taco = new Taco(this.name, this.price);
		this.client.target(baseUri).request(MediaType.APPLICATION_JSON).post(Entity.json(taco));
		this.name = null;
		this.price = null;
		this.getAllTacos();
	}

	public void removeTaco(String tacoId) {
		this.client.target(baseUri).path(tacoId).request().delete();
		this.getAllTacos();
	}
}
