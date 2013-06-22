/**
 * Copyright (C) 2009 Future Invent Informationsmanagement GmbH. All rights
 * reserved. <http://www.fuin.org/>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fuin.kickstart4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.fuin.utils4j.Utils4J;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses a XML configuration.
 */
public final class ConfigParser {

    private final ConfigHandler handler;

    /**
     * Default constructor.
     */
    public ConfigParser() {
        super();
        handler = new ConfigHandler();
    }

    /**
     * Starts the parsing process.
     * 
     * @param url
     *            URL of the XML configuration file.
     * 
     * @throws InvalidConfigException
     *             Error parsing the configuration.
     */
    public void parse(final URL url) throws InvalidConfigException {
        try {
            final String encoding = parseEncoding(url);
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            final SAXParser saxParser = factory.newSAXParser();
            final Reader reader = new BufferedReader(new InputStreamReader(url.openStream(),
                    encoding));
            try {
                saxParser.parse(new InputSource(reader), handler);
            } finally {
                reader.close();
            }
        } catch (final SAXException e) {
            throw new InvalidConfigException(url, e);
        } catch (final ParserConfigurationException e) {
            throw new InvalidConfigException(url, e);
        } catch (final IOException e) {
            throw new InvalidConfigException(url, e);
        }
    }

    /**
     * Determines the encoding from the
     * "&lt;?xml version="1.0" encoding="UTF-8"?&gt" header in the documents. If
     * no encoding is found within the first three lines "UTF-8" is returned.
     * 
     * @param url
     *            URL with the XML document.
     * 
     * @return Encoding attribute.
     * 
     * @throws InvalidConfigException
     *             Error reading the XML document.
     */
    private String parseEncoding(final URL url) throws InvalidConfigException {
        try {
            String encoding = "UTF-8";
            final LineNumberReader reader = new LineNumberReader(new BufferedReader(
                    new InputStreamReader(url.openStream())));
            try {
                final StringBuffer sb = new StringBuffer();
                int count = 1;
                String line;
                while (((line = reader.readLine()) != null) && (count < 3)) {
                    sb.append(line);
                    count++;                    
                }
                final int start = sb.indexOf("encoding=\"");
                if (start > -1) {
                    final int end = sb.indexOf("\"", start + 10);
                    if (end > -1) {
                        encoding = sb.substring(start + 10, end);
                    }
                }
            } finally {
                reader.close();
            }
            return encoding;

        } catch (final IOException e) {
            throw new InvalidConfigException(url, e);
        }
    }

    /**
     * Copies the values from the handler into the config.
     * 
     * @param config
     *            Configuration to use.
     * 
     * @throws InvalidConfigException
     *             Error copying the values.
     */
    public void copyToConfig(final Config config) throws InvalidConfigException {

        final List elements = handler.getElements();
        for (int i = 0; i < elements.size(); i++) {
            final ConfigElement element = (ConfigElement) elements.get(i);
            if (element.getName().equals("file")) {
                final Map atts = element.getAttributes();
                final String path = (String) atts.get("path");
                checkNotNull(element, "path", path);
                final String file = (String) atts.get("file");
                checkNotNull(element, "file", file);
                final String hash = (String) atts.get("hash");
                checkNotNull(element, "hash", hash);
                final String size = (String) atts.get("size");
                checkNotNull(element, "size", size);
                final boolean unzip = getBoolean(atts, "unzip", false);
                final boolean loadAlways = getBoolean(atts, "loadAlways", false);
                final boolean addToClasspath = getBoolean(atts, "addToClasspath", false);
                final String srcFileUrl = (String) atts.get("srcFileUrl");
                checkNotNull(element, "srcFileUrl", srcFileUrl);
                final int order = getInteger(atts, "order", 0);
                config.getSrcFiles().add(
                        new SrcFile(path, file, hash, Long.valueOf(size).longValue(), unzip,
                                loadAlways, addToClasspath, srcFileUrl, order));
            } else if (element.getName().equals("mkdir")) {
                final Map atts = element.getAttributes();
                final String path = (String) atts.get("path");
                checkNotNull(element, "path", path);
                config.getMkDirs().add(new MkDir(path));
            } else if (element.getName().equals("dir")) {
                final Map atts = element.getAttributes();
                final String path = (String) atts.get("path");
                checkNotNull(element, "path", path);
                final String srcPathUrl = (String) atts.get("srcPathUrl");
                checkNotNull(element, "srcPathUrl", srcPathUrl);
                config.getSrcDirs().add(new SrcDir(path, srcPathUrl));
            } else {
                put(config, element.getName(), element.getText());
            }
        }

    }

    /**
     * Set a property in the configuration by it's name. If the key is not known
     * it is ignored.
     * 
     * @param config
     *            Configuration to populate.
     * @param key
     *            Name of the property.
     * @param value
     *            Value of the property.
     */
    // CHECKSTYLE:OFF Cyclomatic Complexity is OK here...
    public final void put(final Config config, final String key, final String value) {
        if (key.equals("title")) {
            config.setTitle(value);
        } else if (key.equals("vendor")) {
            config.setVendor(value);
        } else if (key.equals("description")) {
            config.setDescription(value);
        } else if (key.equals("exitAfterExecute")) {
            config.setExitAfterExecute(toBoolean(value));
        } else if (key.equals("destPath")) {
            config.setDestPath(value);
        } else if (key.equals("idFilename")) {
            config.setIdFilename(value);
        } else if (key.equals("silentInstall")) {
            config.setSilentInstall(toBoolean(value));
        } else if (key.equals("silentUpdate")) {
            config.setSilentUpdate(toBoolean(value));
        } else if (key.equals("locale")) {
            config.setLocale(value);
        } else if (key.equals("lazyLoading")) {
            config.setLazyLoading(toBoolean(value));
        } else if (key.equals("showStartFrame")) {
            config.setShowStartFrame(toBoolean(value));
        } else if (key.equals("startFrameDelaySeconds")) {
            config.setStartFrameDelaySeconds(toInt(value));
        } else if (key.equals("javaExe")) {
            config.setJavaExe(value);
        } else if (key.equals("javaArgs")) {
            config.setJavaArgs(value);
        } else if (key.equals("version")) {
            config.setVersion(value);
        } else if (key.equals("msgFileUrl")) {
            config.setMsgFileUrl(value);
        } else if (key.equals("logFilename")) {
            config.setLogFilename(value);
        } else if (key.equals("lookAndFeelClassName")) {
            config.setLookAndFeelClassName(value);
        } else if (key.equals("xmlEncoding")) {
            config.setXmlEncoding(value);
        }
    }

    // CHECKSTYLE:ON

    private boolean toBoolean(final String str) {
        if (str == null) {
            return false;
        }
        final String trimmed = str.trim();
        if (trimmed.length() == 0) {
            return false;
        }
        return Boolean.valueOf(trimmed).booleanValue();
    }

    private int toInt(final String str) {
        if (str == null) {
            return 0;
        }
        final String trimmed = str.trim();
        if (trimmed.length() == 0) {
            return 0;
        }
        return Integer.valueOf(trimmed).intValue();
    }

    private static void checkNotNull(final ConfigElement element, final String attrName,
            final String attrValue) throws InvalidConfigException {
        if (attrValue == null) {
            throw new InvalidConfigException("Element '" + element.getName()
                    + "' missing required attribute '" + attrName + "'!");
        }
    }

    private static boolean getBoolean(final Map atts, final String key, final boolean defaultValue) {
        final String value = (String) atts.get(key);
        if (value == null) {
            return defaultValue;
        }
        final String str = value.trim();
        if (str.length() == 0) {
            return defaultValue;
        }
        return Boolean.valueOf(str).booleanValue();
    }

    private static int getInteger(final Map atts, final String key, final int defaultValue) {
        final String value = (String) atts.get(key);
        if (value == null) {
            return defaultValue;
        }
        final String str = value.trim();
        if (str.length() == 0) {
            return defaultValue;
        }
        return Integer.valueOf(str).intValue();
    }

    /**
     * Element in the configuration.
     */
    private static final class ConfigElement {

        private final String name;

        private final Map attributes;

        private String text = null;

        public ConfigElement(final String name, final Attributes atts) {
            super();

            this.name = name;
            this.attributes = new HashMap();

            final int length = atts.getLength();
            for (int i = 0; i < length; i++) {
                attributes.put(atts.getQName(i), atts.getValue(i));
            }
        }

        public final Map getAttributes() {
            return attributes;
        }

        public final String getName() {
            return name;
        }

        public final String getText() {
            return text;
        }

        public final void setText(final String text) {
            if (text == null) {
                this.text = null;
            } else {
                this.text = text.trim();
                if (this.text.length() == 0) {
                    this.text = null;
                }
            }
        }

        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append(name);
            sb.append("=");
            sb.append(text);
            sb.append(" {");
            int count = 0;
            final Iterator it = attributes.keySet().iterator();
            while (it.hasNext()) {
                final String key = (String) it.next();
                final String value = (String) attributes.get(key);
                if (count > 0) {
                    sb.append(", ");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
                count++;
            }
            sb.append("}");
            return sb.toString();
        }

    }

    /**
     * Handler for parsing the XML.
     */
    private static class ConfigHandler extends DefaultHandler {

        private final List elements = new ArrayList();

        private ConfigElement element = null;

        private int level = 0;

        public void startElement(final String uri, final String localName, final String qName,
                final Attributes atts) throws SAXException {

            if (level == 1) {
                element = new ConfigElement(qName, atts);
                elements.add(element);
            }
            level++;

        }

        public void characters(final char[] ch, final int start, final int length)
                throws SAXException {
            if ((level == 2) && (element != null)) {
                element.setText(String.copyValueOf(ch, start, length));
            }
        }

        public void endElement(final String uri, final String localName, final String qName)
                throws SAXException {
            if (level == 1) {
                element = null;
            }
            level--;
        }

        public List getElements() {
            return elements;
        }

    }

    /**
     * Fills an object from an XML configuration file.
     * 
     * @param config
     *            Configuration to populate.
     * @param configFile
     *            Configuration file.
     * 
     * @throws InvalidConfigException
     *             Error reading the file.
     */
    public static void parse(final Config config, final File configFile)
            throws InvalidConfigException {
        Utils4J.checkNotNull("config", config);
        Utils4J.checkNotNull("configFile", configFile);
        Utils4J.checkValidFile(configFile);
        try {
            parse(config, configFile.toURI().toURL());
        } catch (final MalformedURLException ex) {
            throw new RuntimeException("Error creating file URL '" + configFile + "'!", ex);
        }
    }

    /**
     * Fills an object from an XML configuration file.
     * 
     * @param config
     *            Configuration to populate.
     * @param configFileURL
     *            URL of the configuration file.
     * 
     * @throws InvalidConfigException
     *             Error reading the file.
     */
    public static void parse(final Config config, final URL configFileURL)
            throws InvalidConfigException {

        Utils4J.checkNotNull("config", config);
        Utils4J.checkNotNull("configFileURL", configFileURL);

        final ConfigParser parser = new ConfigParser();
        parser.parse(configFileURL);
        parser.copyToConfig(config);

    }

    /**
     * Creates an object from a XML configuration file.
     * 
     * @param configFileURL
     *            URL of the configuration file.
     * 
     * @return Configuration filled with values from the file.
     * 
     * @throws InvalidConfigException
     *             Error reading the file.
     */
    public static Config create(final URL configFileURL) throws InvalidConfigException {
        Utils4J.checkNotNull("configFileURL", configFileURL);
        final Config config = new Config();
        parse(config, configFileURL);
        return config;
    }

    /**
     * Creates an object from a XML configuration file.
     * 
     * @param configFile
     *            Configuration file.
     * 
     * @return Configuration filled with values from the file.
     * 
     * @throws InvalidConfigException
     *             Error reading the file.
     */
    public static Config create(final File configFile) throws InvalidConfigException {
        Utils4J.checkNotNull("configFile", configFile);
        Utils4J.checkValidFile(configFile);
        try {
            return create(configFile.toURI().toURL());
        } catch (final MalformedURLException ex) {
            throw new RuntimeException("Error creating file URL '" + configFile + "'!", ex);
        }
    }

}
