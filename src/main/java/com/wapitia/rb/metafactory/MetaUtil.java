package com.wapitia.rb.metafactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Utility class as a singleton.
 *
 */
public class MetaUtil {

	private static final String META_PROPS_RESOURCE = "/rbmapmetadata.properties";

    public static MetaUtil instance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Load the meta properties from the proper resource.
     * @param metaSuite
     * @return
     * @throws IOException
     */
	public Properties loadRBProperties(String metaSuite) throws IOException {
		InputStream in = MetaUtil.class.getResourceAsStream(META_PROPS_RESOURCE);
		InputStreamReader propsStreamReader = new InputStreamReader(in);
		Properties rbProps = new Properties();
		rbProps.load(propsStreamReader);
		return rbProps;
	}

	/**
	 * Constructor is private as this is a utility class.
	 */
    private MetaUtil() {}

    /**
     * Initialization-on-demand holder idiom
     */
    private static class SingletonHolder {
        static final MetaUtil INSTANCE = new MetaUtil();
    }
	
}
