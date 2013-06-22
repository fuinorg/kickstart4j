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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.fuin.utils4j.Utils4J;
import org.testng.Assert;

/**
 * Tests for {@link ConfigParser}.
 */
// CHECKSTYLE:OFF
public final class ConfigParserTest {

	/**
	 * @testng.test
	 */
	public final void testPut() {
		final Config config = new Config();
		final ConfigParser configParser = new ConfigParser();
		String value;

		value = "TITLE";
		configParser.put(config, "title", value);
		Assert.assertEquals(config.getTitle(), value);

		value = "VENDOR";
		configParser.put(config, "vendor", value);
		Assert.assertEquals(config.getVendor(), value);

		value = "true";
		configParser.put(config, "exitAfterExecute", value);
		Assert.assertTrue(config.isExitAfterExecute());

		value = "false";
		configParser.put(config, "exitAfterExecute", value);
		Assert.assertFalse(config.isExitAfterExecute());

		value = "/xyz/abc/def";
		configParser.put(config, "destPath", value);
		Assert.assertEquals(config.getDestPath(), value);

		value = ".abc123def";
		configParser.put(config, "idFilename", value);
		Assert.assertEquals(config.getIdFilename(), value);

		value = "true";
		configParser.put(config, "silentInstall", value);
		Assert.assertTrue(config.isSilentInstall(), value);

		value = "false";
		configParser.put(config, "silentInstall", value);
		Assert.assertFalse(config.isSilentInstall(), value);

		value = "true";
		configParser.put(config, "silentUpdate", value);
		Assert.assertTrue(config.isSilentUpdate(), value);

		value = "false";
		configParser.put(config, "silentUpdate", value);
		Assert.assertFalse(config.isSilentUpdate(), value);

		value = Locale.GERMANY.getLanguage() + ","
				+ Locale.GERMANY.getCountry() + ","
				+ Locale.GERMANY.getDisplayVariant();
		configParser.put(config, "locale", value);
		Assert.assertEquals(config.getLocale(), Locale.GERMANY);
		Assert.assertEquals(config.getLocale().getLanguage(), Locale.GERMANY.getLanguage());
		Assert.assertEquals(config.getLocale().getCountry(), Locale.GERMANY.getCountry());
		Assert.assertEquals(config.getLocale().getVariant(), Locale.GERMANY.getVariant());

		value = "true";
		configParser.put(config, "lazyLoading", value);
		Assert.assertTrue(config.isLazyLoading());

		value = "false";
		configParser.put(config, "lazyLoading", value);
		Assert.assertFalse(config.isLazyLoading());

		value = "true";
		configParser.put(config, "showStartFrame", value);
		Assert.assertTrue(config.isShowStartFrame());

		value = "false";
		configParser.put(config, "showStartFrame", value);
		Assert.assertFalse(config.isShowStartFrame());

		value = "123";
		configParser.put(config, "startFrameDelaySeconds", value);
		Assert.assertEquals(config.getStartFrameDelaySeconds(), 123);

		value = "${destDir}/jre6/bin/java.exe";
		configParser.put(config, "javaExe", value);
		Assert.assertEquals(config.getJavaExe(), value);

		value = "-classpath ${classpath} SwingSet2";
		configParser.put(config, "javaArgs", value);
		Assert.assertEquals(config.getJavaArgs(), value);

		value = "1.0.2";
		configParser.put(config, "version", value);
		Assert.assertEquals(config.getVersion(), value);

		value = "http://www.fuin.org/test/test.properties";
		configParser.put(config, "msgFileUrl", value);
		Assert.assertEquals(config.getMsgFileUrl(), value);

		value = "${destDir}/logs/kickstart.log";
		configParser.put(config, "logFilename", value);
		Assert.assertEquals(config.getLogFilename(), value);

		value = "A very cool program";
		configParser.put(config, "description", value);
		Assert.assertEquals(config.getDescription(), value);

		value = "com.jgoodies.looks.windows.WindowsLookAndFeel";
		configParser.put(config, "lookAndFeelClassName", value);
		Assert.assertEquals(config.getLookAndFeelClassName(), value);

		value = "UTF-8";
		configParser.put(config, "xmlEncoding", value);
		Assert.assertEquals(config.getXmlEncoding(), value);

	}

	/**
	 * @testng.test
	 */
	public final void testCreate() throws IOException, InvalidConfigException {	    

	    // Prepare
        final String path = "jre6";
        final String filename = "README.txt";
        final String md5Hash = "0011223344556677889900112233445";
        final long size = 99999;
        final boolean unzip = true;
        final boolean loadAlways = false;
        final boolean addToClasspath = true;
        final String srcFileUrl = "http://www.fuin.org/ctx/func=load&path=sub&filename=README.txt";
        final int order = 2;
        
	    final Config config = createConfig();
        final List srcFiles = config.getSrcFiles();
        srcFiles.add(new SrcFile(path, filename, md5Hash, size, unzip, loadAlways, addToClasspath, srcFileUrl, order));
		final File configFile = createConfigFile(config);

		// Test
        final Config result = ConfigParser.create(configFile);
		
        // Assert
        Assert.assertEquals(1, result.getSrcFiles().size());
        final SrcFile srcFile = (SrcFile) result.getSrcFiles().get(0);
        Assert.assertEquals(md5Hash, srcFile.getMd5Hash());
        Assert.assertEquals(size, srcFile.getSize());
        Assert.assertEquals(path, srcFile.getPath());
        Assert.assertEquals(filename, srcFile.getFilename());
        Assert.assertEquals(unzip, srcFile.isUnzip());
        Assert.assertEquals(loadAlways, srcFile.isLoadAlways());
        Assert.assertEquals(addToClasspath, srcFile.isAddToClasspath());
        Assert.assertEquals(srcFileUrl, srcFile.getSrcFileUrl());
        Assert.assertEquals(order, srcFile.getOrder());
		
		
	}

	
//
//	/**
//	 * @testng.test
//	 */
//	public final void testParseConfigURL() {
//		Assert.fail("Not implemented!");
//	}
//
//	/**
//	 * @testng.test
//	 */
//	public final void testCreateFile() {
//		Assert.fail("Not implemented!");
//	}
//
//	/**
//	 * @testng.test
//	 */
//	public final void testCreateURL() {
//		Assert.fail("Not implemented!");
//	}
//
//	/**
//	 * @testng.test
//	 */
//	public final void testCopyToConfig() {
//		Assert.fail("Not implemented!");
//	}
//
//	/**
//	 * @testng.test
//	 */
//	public final void testParseURL() {
//		Assert.fail("Not implemented!");
//	}

	
    private Config createConfig() {
        final Config config = new Config();
        config.setIdFilename("myTestApp");
        config.setTitle("My Test App");
        config.setDestPath("c:/test");
        config.setLocale(Locale.ENGLISH);
        config.setJavaExe("jre/bin/java.exe");
        config.setJavaArgs("-classpath ${classpath} org.fuin.kickstart4j.TestMain");
        return config;
    }

    private File createConfigFile(final Config config) throws IOException {        
        final File configFile = new File(Utils4J.getTempDir(), "test-config.xml");
        config.writeToVarXML(configFile, false);
        return configFile;
    }
    
}
// CHECKSTYLE:ON
