package it.tredi.fca.docway;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import it.highwaytech.apps.generic.Connessione;
import it.highwaytech.apps.generic.Selezione;
import it.highwaytech.broker.Broker;
import it.tredi.fca.Fca;
import it.tredi.fca.entity.FcsRequest;
import it.tredi.utils.maven.ApplicationProperties;
import it.tredi.utils.properties.PropertiesReader;

/**
 * Implementazione di FCA per DocWay (indicizzazione e conversione di file allegati a documenti)
 */
public class DocWayFca extends Fca {

	private static final Logger logger = LogManager.getLogger(DocWayFca.class.getName());
	
	private static final String XWAY_DOCS_TITLE_RULE = "xml,/doc/@nrecord";

	private static final String BROKER_PROPERTIES_FILE_NAME = "it.highwaytech.broker.properties";
	
	private static final String XWAY_HOST_PROPERTY_NAME = "host";
	private static final String XWAY_PORT_PROPERY_NAME = "port";
	
	private static final String XWAY_HOST_DEFAULT_VALUE = "127.0.0.1";
	private static final int XWAY_PORT_DEFAULT_VALUE = 4859;
	
	private static final String FCA_ARTIFACTID = "docway-fca";
	private static final String FCA_GROUPID = "it.tredi";
	
	/**
	 * Connessione al server eXtraWay
	 */
	private Connessione connessione;
	
	/**
	 * Metodo MAIN di avvio del processo. Istanzia l'oggetto FCA ed esegue il metodo RUN
	 * @param args
	 */
	public static void main(String[] args) {
		int exitCode = 0;
		try {
			DocWayFca docwayFca = new DocWayFca();
			docwayFca.run();
			
			logger.info("DocWayFca.main(): shutdown...");
		}
		catch(Exception e) {
			logger.error("DocWayFca.main(): got exception... " + e.getMessage(), e);
			exitCode = 1;
		}
		System.exit(exitCode);
	}
	
	private Broker broker;
	
	/**
	 * Costruttore
	 * @throws Exception
	 */
	public DocWayFca() throws Exception {
		super();
		
		this.broker = new Broker();
		
		// caricamento dell'host e porta di eXtraWay direttamente dal file di properties del broker
		PropertiesReader propertiesReader = new PropertiesReader(BROKER_PROPERTIES_FILE_NAME);
		String host = propertiesReader.getProperty(XWAY_HOST_PROPERTY_NAME, XWAY_HOST_DEFAULT_VALUE);
		int port = propertiesReader.getIntProperty(XWAY_PORT_PROPERY_NAME, XWAY_PORT_DEFAULT_VALUE);
		this.connessione = new Connessione(host, port);
	}
	
	@Override
	public String getAppVersion() {
		 return ApplicationProperties.getInstance().getVersion(FCA_GROUPID, FCA_ARTIFACTID);
	}

	@Override
	public String getAppBuildDate() {
		return ApplicationProperties.getInstance().getBuildDate(FCA_GROUPID, FCA_ARTIFACTID);
	}
	
	@Override
	public void onRunException(Exception e) {
	}

	@Override
	public void onRunFinally() {
		invalidateHost();
	}
	
	/**
	 * Chiamata all'invalidate host del broker (eventuale chiusura delle connessioni al client elasticsearch)
	 */
	private void invalidateHost() {
		if (broker != null) {
			try {
				// chiusura delle connessione con l'host eXtraWay (e connessioni dei client elasticsearch)
				broker.invalidateHost();
			} catch (Exception e) {
				logger.error("DocWayFca.invalidateHost(): got exception on invalidate host... " + e.getMessage(), e);
			}
		}
	}

	@Override
	public List<FcsRequest> loadFcsPendingRequests() throws Exception {
		List<FcsRequest> docs = null;
		
		try {
			// connessione ad extraway
			connessione.connect(DocWayFcaConfig.getInstance().getXwayDbName());
			
			String fcaUser = "xw." + InetAddress.getLocalHost().getCanonicalHostName() + ".fca";
			if (logger.isInfoEnabled())
				logger.info("DocWayFca.loadFcsPendingRequests(): notifying user \"" + fcaUser + "\"" + " on connection " + connessione.getConnection());
			this.connessione.notifyUser(fcaUser);
			
			// oggetto json passato a host FCS contenente parametri aggiuntivi necessari al completamento delle attivita' richieste
			JSONObject json = new JSONObject();
			json.put("xway.host", connessione.getHost());
			json.put("xway.port", connessione.getPort());
			json.put("xway.db", DocWayFcaConfig.getInstance().getXwayDbName());
			String fcacmdParams = json.toString();
			if (logger.isDebugEnabled())
				logger.debug("DocWayFca.loadFcsPendingRequests(): fca command, additional parameters = " + fcacmdParams);
			
			// aggiungo alla query definita da properties il filtro su udType per essere certi di cercare solamente documenti
			String query = "([UD,/xw/@UdType/]=\"doc\") AND (" + DocWayFcaConfig.getInstance().getXwayQuery() + ")";
			
			// esclusione dalla query di tutti i documenti attualmente in fase di elaborazione da parte degli host FCS
			List<String> inprogressIds = getInProgressRequests();
			if (inprogressIds != null && !inprogressIds.isEmpty()) {
				if (logger.isDebugEnabled())
					logger.debug("DocWayFca.loadFcsPendingRequests(): exclude in-progress documents from selection... " + String.join(", ", inprogressIds));
				
				String excludeQuery = "";
				for (String inprogId : inprogressIds) {
					if (inprogId != null && !inprogId.isEmpty())
						excludeQuery += "[docnrecord]=\"" + inprogId + "\" OR ";
				}
				if (!excludeQuery.isEmpty()) {
					excludeQuery = excludeQuery.substring(0, excludeQuery.length()-4); // eliminazione di " OR "
					query += " AND NOT(" + excludeQuery + ")";
				}
			}
			
			Selezione selezione = new Selezione(connessione);
			long count = selezione.Search(query, "", DocWayFcaConfig.getInstance().getXwaySort(), 0, 0);
			if (logger.isInfoEnabled())
				logger.info("DocWayFca.loadFcsPendingRequests(): found " + count + " documents by query: " + query);
			
			if (count > 0) {
				docs = new ArrayList<FcsRequest>();
				int docToLoad = DocWayFcaConfig.getInstance().getXwayQueryMaxDocs();
				if (docToLoad > count)
					docToLoad = (int) count;
				String[] titles = selezione.getTitles(0, XWAY_DOCS_TITLE_RULE, 0, docToLoad);
				if (titles != null && titles.length > 0) {
					for (int i=0; i<titles.length; i++) {
						if (titles[i] != null && !titles[i].isEmpty()) {
							if (logger.isDebugEnabled())
								logger.debug("DocWayFca.loadFcsPendingRequests(): title[" + i + "]: " + titles[i]);
							
							// per come e' definita la titlerule, il titolo corrisponde esattamente all'nrecord del documento
							FcsRequest fcsRequest = new FcsRequest(titles[i], FcsRequest.CONVERSION_TO_PDF);
							fcsRequest.setAdditionalParameters(fcacmdParams);
							docs.add(fcsRequest); 
						}
					}
				}
			}
		}
		finally {
			// rilascio della connessione su extraway
			if (connessione.isConnected())
				connessione.disconnect();
		}
		
		return docs;
	}

}
