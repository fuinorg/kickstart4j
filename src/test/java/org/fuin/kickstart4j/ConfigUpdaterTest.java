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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.fuin.utils4j.Utils4J;

/**
 * Tests for {@link Utils}.
 */
// CHECKSTYLE:OFF
public class ConfigUpdaterTest {

    /**
     * @testng.test
     */
    public void testConstructor() {
        final ConfigUpdater testee = new ConfigUpdater();
        Assert.assertTrue(testee.getListener() instanceof ConfigUpdater.DefaultListener);
    }

    /**
     * @testng.test
     */
    public void testConstructorListener() {
        final ConfigUpdaterListener listener = createConfigUpdaterListener();
        final ConfigUpdater testee = new ConfigUpdater(listener);
        Assert.assertSame(listener, testee.getListener());
    }

    /**
     * @testng.test
     */
    public void testConfigUpdaterFile() throws IOException, InvalidConfigException {

        // Prepare
        final Config config = createConfig();
        final File configFile = createConfigFile(config);

        // Test
        final ConfigUpdater testee = new ConfigUpdater(configFile);
        
        // Assert
        Assert.assertTrue(testee.getListener() instanceof ConfigUpdater.DefaultListener);
        final Config testeeConfig = testee.getConfig();
        Assert.assertNotNull(testeeConfig);
        Assert.assertTrue(testeeConfig.simpleAttributesEquals(config));
        Assert.assertEquals(0, testeeConfig.getSrcDirs().size());
        Assert.assertEquals(0, testeeConfig.getSrcFiles().size());
        Assert.assertEquals(1, testeeConfig.getCmdLineOptions().size());
        Assert.assertEquals(0, testeeConfig.getMkDirs().size());
        
    }

    /**
     * @testng.test
     */
    public void testConfigUpdaterFileListener() throws IOException, InvalidConfigException {

        // Prepare
        final Config config = createConfig();
        final ConfigUpdaterListener listener = createConfigUpdaterListener();
        final File configFile = createConfigFile(config);
        
        // Test
        final ConfigUpdater testee = new ConfigUpdater(configFile, listener);

        // Assert
        Assert.assertSame(listener, testee.getListener());
        final Config testeeConfig = testee.getConfig();
        Assert.assertNotNull(testeeConfig);
        Assert.assertTrue(testeeConfig.simpleAttributesEquals(config));
        Assert.assertEquals(0, testeeConfig.getSrcDirs().size());
        Assert.assertEquals(0, testeeConfig.getSrcFiles().size());
        Assert.assertEquals(1, testeeConfig.getCmdLineOptions().size());
        Assert.assertEquals(0, testeeConfig.getMkDirs().size());
        
    }

    /**
     * @testng.test
     */
    public void testUpdateExistingWithLoading() throws IOException, InvalidConfigException {
        
        // Prepare
        final String path = "jre6";
        final String filename = "README.txt";
        final String md5Hash = "0011223344556677889900112233445";
        final long size = 99999;
        final boolean unzip = false;
        final boolean loadAlways = true;
        final boolean addToClasspath = false;
        final String srcFileUrl = "http://www.fuin.org/examples/kickstart4j/README.txt";
        final int order = 1;
        
        final List remoteFileList = new ArrayList();
        final RemoteFile remoteFile = new RemoteFile(new URL(srcFileUrl), path, filename, false, null, 0);
        remoteFileList.add(remoteFile);

        final ConfigUpdater testee = new ConfigUpdater();
        final List srcFiles = testee.getConfig().getSrcFiles();
        srcFiles.add(new SrcFile(path, filename, md5Hash, size, unzip, loadAlways, addToClasspath, srcFileUrl, order));
        
        // Test
        testee.update(remoteFileList);
        
        // Assert
        Assert.assertEquals(1, srcFiles.size());
        final SrcFile srcFile = (SrcFile) srcFiles.get(0);
        Assert.assertEquals("f4d7b1802f97b747d921dff10eb533e7", srcFile.getMd5Hash());
        Assert.assertEquals(15773, srcFile.getSize());
        Assert.assertEquals(path, srcFile.getPath());
        Assert.assertEquals(filename, srcFile.getFilename());
        Assert.assertEquals(unzip, srcFile.isUnzip());
        Assert.assertEquals(loadAlways, srcFile.isLoadAlways());
        Assert.assertEquals(addToClasspath, srcFile.isAddToClasspath());
        Assert.assertEquals(srcFileUrl, srcFile.getSrcFileUrl());
        Assert.assertEquals(order, srcFile.getOrder());
        
    }

    /**
     * @testng.test
     */
    public void testUpdateExistingWithoutLoading() throws IOException, InvalidConfigException {
        
        // Prepare
        final String path = "jre6";
        final String filename = "README.txt";
        final String md5Hash = "0011223344556677889900112233445";
        final long size = 99999;
        final boolean unzip = true;
        final boolean loadAlways = false;
        final boolean addToClasspath = true;
        final String srcFileUrl = "http://www.fuin.org/examples/kickstart4j/README.txt";
        final int order = 2;
        
        final List remoteFileList = new ArrayList();
        final RemoteFile remoteFile = new RemoteFile(new URL(srcFileUrl), path, filename, false, md5Hash, size);
        remoteFileList.add(remoteFile);

        final ConfigUpdater testee = new ConfigUpdater();
        final List srcFiles = testee.getConfig().getSrcFiles();
        srcFiles.add(new SrcFile(path, filename, md5Hash, size, unzip, loadAlways, addToClasspath, srcFileUrl, order));
        
        // Test
        testee.update(remoteFileList);
        
        // Assert
        Assert.assertEquals(1, srcFiles.size());
        final SrcFile srcFile = (SrcFile) srcFiles.get(0);
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
    
    private ConfigUpdaterListener createConfigUpdaterListener() {
        return new ConfigUpdaterListener() {
            public void onCopy(RemoteFile remoteFile, File file, int nr, int max) {
                // Do nothing
            }

            public void onNotFound(RemoteFile remoteFile, File file, int nr, int max) {
                // Do nothing
            }
        };
    }

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
