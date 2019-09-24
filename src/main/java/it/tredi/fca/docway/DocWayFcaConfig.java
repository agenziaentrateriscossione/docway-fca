package it.tredi.fca.docway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.tredi.utils.properties.PropertiesReader;

public class DocWayFcaConfig {

	private static final Logger logger = LogManager.getLogger(DocWayFca.class.getName());
	
	private static final String PROPERTIES_FILE_NAME = "it.tredi.docway-fca.properties";
	
	private static final String XWAY_DBNAME_PROPERTY = "xway.dbname";
	private static final String XWAY_QUERY_PROPERTY = "xway.query";
	private static final String XWAY_SORT_PROPERTY = "xway.sort";
	private static final String XWAY_QUERY_MAX_DOCS_PROPERTY = "xway.query.max.docs";
	
	private static final int XWAY_QUERY_MAX_DOCS_DEFAULT_VALUE = 500;

	// Singleton
    private static DocWayFcaConfig instance = null;
    
    private String xwayDbName;
	private String xwayQuery;
	private String xwaySort;
	
	private int xwayQueryMaxDocs = XWAY_QUERY_MAX_DOCS_DEFAULT_VALUE;
    
    /**
     * Costruttore privato
     */
    private DocWayFcaConfig() throws Exception {
    	PropertiesReader propertiesReader = new PropertiesReader(PROPERTIES_FILE_NAME); 
    	
    	this.xwayDbName = propertiesReader.getProperty(XWAY_DBNAME_PROPERTY, null);
    	if (this.xwayDbName == null || this.xwayDbName.isEmpty())
    		throw new Exception("Unable to connect to xway db... No dbname defined on " + PROPERTIES_FILE_NAME);
    	
    	this.xwayQuery = propertiesReader.getProperty(XWAY_QUERY_PROPERTY, null);
    	if (this.xwayQuery == null || this.xwayQuery.isEmpty())
    		throw new Exception("Unable to connect to xway db... No query defined on " + PROPERTIES_FILE_NAME);
    	
    	this.xwaySort = propertiesReader.getProperty(XWAY_SORT_PROPERTY, null);
    	
    	this.xwayQueryMaxDocs = propertiesReader.getIntProperty(XWAY_QUERY_MAX_DOCS_PROPERTY, XWAY_QUERY_MAX_DOCS_DEFAULT_VALUE);
    	
    	if (logger.isDebugEnabled()) {
    		logger.debug("------------------- DOCWAY-FCA CONFIGURATION PARAMETERS -------------------");
    		logger.debug(XWAY_DBNAME_PROPERTY + " = " + this.xwayDbName);
    		logger.debug(XWAY_QUERY_PROPERTY + " = " + this.xwayQuery);
    		logger.debug(XWAY_SORT_PROPERTY + " = " + this.xwaySort);
    		logger.debug(XWAY_QUERY_MAX_DOCS_DEFAULT_VALUE + " = " + String.valueOf(this.xwayQueryMaxDocs));
    	}
    }
    
    /**
     * Ritorna l'oggetto contenente tutte le configurazioni dell'implementazione di DocWay di FCA
	 * @return
	 */
	public static DocWayFcaConfig getInstance() throws Exception {
		if (instance == null) {
			synchronized (DocWayFcaConfig.class) {
				if (instance == null) {
					if (logger.isInfoEnabled())
						logger.info("DocWayFcaConfig instance is null... create one");
					instance = new DocWayFcaConfig();
				}
			}
		}
		return instance;
	}
	
	public String getXwayDbName() {
		return xwayDbName;
	}

	public String getXwayQuery() {
		return xwayQuery;
	}

	public String getXwaySort() {
		return xwaySort;
	}

	public int getXwayQueryMaxDocs() {
		return xwayQueryMaxDocs;
	}
	
}
