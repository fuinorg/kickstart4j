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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.UIManager;

import org.apache.commons.io.IOUtils;
import org.fuin.utils4j.ToDebugStringCapable;
import org.fuin.utils4j.Utils4J;

/**
 * Configuration for the application. The <code>cmdLineOptions</code> map
 * contains all arguments from the command line that are not known standard
 * arguments. This way it's possible to add application specific parameters for
 * the installation process. The entries <code>userHome</code> and
 * <code>destDir</code> are predefined values.
 */
public final class Config implements ToDebugStringCapable, SimpleAttributesEqualsCapable {

    /** Fully qualified LnF class name. */
    private String lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();

    /** Unique id filename for the application. */
    private String idFilename;

    /** Determines if the program terminates with <code>System.exit(..)</code>. */
    private boolean exitAfterExecute = true;

    /** Local path where the files should be copied to. */
    private String destPath = null;

    /** Determines if the user should be prompted for the destination directory. */
    private boolean silentInstall = false;

    /** Determines if the user should be prompted if an update is available. */
    private boolean silentUpdate = false;

    /** Determines if this is the first installation. */
    private boolean firstInstallation = true;

    /** Locale to use. */
    private Locale locale = Locale.getDefault();

    /** File containing the localized installation messages. */
    private String msgFileUrl = null;

    /** Message properties needed for the installer. */
    private MessagesWrapper messages = null;

    /** Load only the "jar" entries on startup? */
    private boolean lazyLoading = false;

    /** Options from the command line. */
    private Map cmdLineOptions = new HashMap();

    /** Java executable. */
    private String javaExe = null;

    /** Command line including (without java executable itself). */
    private String javaArgs = null;

    /** Title of the application. */
    private String title = null;

    /** Vendor of the application. */
    private String vendor = null;

    /** Short description of the application. */
    private String description = null;

    /** List of known files. */
    private List srcFiles = new ArrayList();

    /** List of directories to create. */
    private List mkDirs = new ArrayList();

    /** List of known directories. */
    private List srcDirs = new ArrayList();

    /** URL of the configuration file (only required when lazyLoading=true). */
    private String configFileUrl = null;

    /** Target application version. */
    private String version = null;

    /** Encoding to use for XML output. */
    private String xmlEncoding = "ISO-8859-1";

    /** Show a "starting application" frame? */
    private boolean showStartFrame = true;

    /** Show the start frame for N seconds after application has started. */
    private int startFrameDelaySeconds = 2;

    /** Name and path of the log file. */
    private String logFilename = System.getProperty("user.home") + File.separator
            + "kickstart4j.log";

    /**
     * Default constructor.
     */
    public Config() {
        super();
        cmdLineOptions.put("userHome", System.getProperty("user.home"));
    }

    /**
     * Returns the name of the LookAndFeel class.
     * 
     * @return Fully qualified LnF class name - Always non-null.
     */
    public final String getLookAndFeelClassName() {
        return lookAndFeelClassName;
    }

    /**
     * Sets the name of the Look and Feel class.
     * 
     * @param lnfClassName
     *            Full qualified Java LookAndFeel class name - System
     *            LookAndFeel is used when <code>null</code>.
     */
    public final void setLookAndFeelClassName(final String lnfClassName) {
        if (lnfClassName == null) {
            this.lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        } else {
            this.lookAndFeelClassName = lnfClassName;
        }
    }

    /**
     * Returns whether the program should terminate after executing.
     * 
     * @return If the program terminates with <code>System.exit(..)</code>
     *         <code>true</code> (DEFAULT) else <code>false</code>.
     */
    public final boolean isExitAfterExecute() {
        return exitAfterExecute;
    }

    /**
     * Sets whether the program should terminate after executing.
     * 
     * @param exitAfterExecute
     *            If the program should terminate with
     *            <code>System.exit(..)</code> <code>true</code> (DEFAULT) else
     *            <code>false</code>.
     */
    public final void setExitAfterExecute(final boolean exitAfterExecute) {
        this.exitAfterExecute = exitAfterExecute;
    }

    /**
     * Returns the local destination path where the files should be copied to.
     * 
     * @return Local path - Always non-<code>null</code> if <code>check()</code>
     *         throws no exceptions.
     */
    public final File getDestDir() {
        return new File(getDestPath());
    }

    /**
     * Returns the local destination path where the files should be copied to.
     * 
     * @return Path.
     */
    public final String getDestPath() {
        return Utils4J.replaceVars(destPath, cmdLineOptions);
    }

    /**
     * Sets the local destination path where the files should be copied to.
     * 
     * @param destPath
     *            Path - Cannot be <code>null</code>
     */
    public final void setDestPath(final String destPath) {
        if (destPath == null) {
            throw new IllegalArgumentException("The argument 'destPath' cannot be null!");
        }
        this.destPath = destPath;
    }

    /**
     * Returns the unique id filename for the application.
     * 
     * @return Name of the application ID file - Always non-<code>null</code> if
     *         <code>check()</code> throws no exceptions.
     */
    public final String getIdFilename() {
        return Utils4J.replaceVars(idFilename, cmdLineOptions);
    }

    /**
     * Sets the unique id filename for the application.
     * 
     * @param id
     *            Unique ID - Must be a valid filename on the target system - A
     *            <code>null</code> value is not allowed!
     */
    public final void setIdFilename(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("The argument 'id' cannot be null!");
        }
        this.idFilename = id;
    }

    /**
     * Determines if the user should be prompted for the destination directory.
     * 
     * @return If "destDir" should be used without asking the user
     *         <code>true</code> else <code>false</code>.
     */
    public final boolean isSilentInstall() {
        return silentInstall;
    }

    /**
     * Determines if the user should be prompted for the destination directory.
     * 
     * @param silentInstall
     *            If "destDir" should be used without asking the user
     *            <code>true</code> else <code>false</code>.
     */
    public final void setSilentInstall(final boolean silentInstall) {
        this.silentInstall = silentInstall;
    }

    /**
     * Determines if the user should be asked if an update is available.
     * 
     * @return If updates should be executed without asking the user
     *         <code>true</code> else <code>false</code>.
     */
    public final boolean isSilentUpdate() {
        return silentUpdate;
    }

    /**
     * Determines if the user should be asked if an update is available.
     * 
     * @param silentUpdate
     *            If updates should be executed without asking the user
     *            <code>true</code> else <code>false</code>.
     */
    public final void setSilentUpdate(final boolean silentUpdate) {
        this.silentUpdate = silentUpdate;
    }

    /**
     * Determines if this is the first installation.
     * 
     * @return If this is the first installation <code>true</code> else
     *         <code>false</code>.
     */
    public final boolean isFirstInstallation() {
        return firstInstallation;
    }

    /**
     * Determines if this is the first installation.
     * 
     * @param firstInstallation
     *            If this is the first installation <code>true</code> else
     *            <code>false</code>.
     */
    public final void setFirstInstallation(final boolean firstInstallation) {
        this.firstInstallation = firstInstallation;
    }

    /**
     * Returns the locale to use.
     * 
     * @return Locale - Always non-<code>null</code>.
     */
    public final Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale to use.
     * 
     * @param locale
     *            Locale to set - A <code>null</code> value resets the property
     *            to <code>Locale.getDefault()</code>.
     */
    public final void setLocale(final Locale locale) {
        if (locale == null) {
            this.locale = Locale.getDefault();
        } else {
            this.locale = locale;
        }
    }

    /**
     * Sets the locale to use as a String.
     * 
     * @param locale
     *            Locale "lang" or "lang,country" or "lang,country,variant" - A
     *            <code>null</code> value resets the property to
     *            <code>Locale.getDefault()</code>.
     */
    public final void setLocale(final String locale) {
        if (locale == null) {
            this.locale = Locale.getDefault();
        } else {
            final StringTokenizer tok = new StringTokenizer(locale, ",");
            final int count = tok.countTokens();
            final String language;
            final String country;
            final String variant;
            if (count == 1) {
                language = tok.nextToken();
                country = "";
                variant = "";
            } else if (count == 2) {
                language = tok.nextToken();
                country = tok.nextToken();
                variant = "";
            } else if (count == 3) {
                language = tok.nextToken();
                country = tok.nextToken();
                variant = tok.nextToken();
            } else {
                throw new IllegalArgumentException("The argument '" + locale + "' is not valid!");
            }
            this.locale = new Locale(language, country, variant);
        }
    }

    /**
     * Returns the file containing the localized installation messages.
     * 
     * @return Message file URL - If <code>null</code> the internal default
     *         messages will be used.
     */
    public final String getMsgFileUrl() {
        return msgFileUrl;
    }

    /**
     * Returns the file containing the localized installation messages. A
     * <code>MalformedURLException</code> is wrapped into a
     * <code>RuntimeException</code>.
     * 
     * @return Message file URL - If <code>null</code> the internal default
     *         messages will be used.
     */
    public final URL getMsgFileURL() {
        if (msgFileUrl == null) {
            return null;
        }
        try {
            return new URL(msgFileUrl);
        } catch (final MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Sets the file containing the localized installation messages.
     * 
     * @param msgFileUrl
     *            Message file URL - A <code>null</code> value will result in
     *            usage of the internal default messages.
     */
    public final void setMsgFileUrl(final String msgFileUrl) {
        if (msgFileUrl == null) {
            this.msgFileUrl = null;
        } else {
            this.msgFileUrl = msgFileUrl.trim();
        }
    }

    /**
     * Checks if the configuration is valid.
     * 
     * @throws InvalidConfigException
     *             The configuration is not valid.
     */
    public final void check() throws InvalidConfigException {
        if (destPath == null) {
            throw new InvalidConfigException("The 'destPath' is null!");
        }
        if (idFilename == null) {
            throw new InvalidConfigException("The 'idFilename' is null!");
        }
        if ((lazyLoading) && ((configFileUrl == null) || (configFileUrl.trim().length() == 0))) {
            throw new InvalidConfigException("The 'configFileUrl' is null or empty!");
        }
    }

    /**
     * Returns the message properties needed for the installer.
     * 
     * @return Localized messages.
     */
    public final MessagesWrapper getMessages() {
        if (messages == null) {
            try {
                messages = new MessagesWrapper(Utils4J.loadProperties(getMsgFileURL()));
            } catch (final RuntimeException ex) {
                // Load default messages
                messages = new MessagesWrapper(locale);
            }
        }

        return messages;
    }

    /**
     * Returns if lazy loading is active.
     * 
     * @return If lazy loading is enabled <code>true</code> else
     *         <code>false</code>.
     */
    public final boolean isLazyLoading() {
        return lazyLoading;
    }

    /**
     * Sets the information if lazy loading is active.
     * 
     * @param lazyLoading
     *            To enable lazy loading <code>true</code> else
     *            <code>false</code> (disable lazy loading)).
     */
    public final void setLazyLoading(final boolean lazyLoading) {
        this.lazyLoading = lazyLoading;
    }

    /**
     * Returns the options form the command line.
     * 
     * @return Key/value <code>String</code> pairs.
     */
    public final Map getCmdLineOptions() {
        return cmdLineOptions;
    }

    /**
     * Returns the command line arguments.
     * 
     * @return Arguments for the Java executable.
     */
    public final String getJavaArgs() {
        return Utils4J.replaceVars(javaArgs, cmdLineOptions);
    }

    /**
     * Sets the command line arguments.
     * 
     * @param javaArgs
     *            Arguments for the Java executable.
     */
    public final void setJavaArgs(final String javaArgs) {
        this.javaArgs = javaArgs;
    }

    /**
     * Returns the Java executable.
     * 
     * @return Java executable.
     */
    public final String getJavaExe() {
        return Utils4J.replaceVars(javaExe, cmdLineOptions);
    }

    /**
     * Sets the Java executable.
     * 
     * @param javaExe
     *            Java executable.
     */
    public final void setJavaExe(final String javaExe) {
        this.javaExe = javaExe;
    }

    /**
     * Returns a short description of the application.
     * 
     * @return Description.
     */
    public final String getDescription() {
        return Utils4J.replaceVars(description, cmdLineOptions);
    }

    /**
     * Sets the short description of the application.
     * 
     * @param description
     *            Description.
     */
    public final void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Returns the title of the application.
     * 
     * @return Title.
     */
    public final String getTitle() {
        return Utils4J.replaceVars(title, cmdLineOptions);
    }

    /**
     * Sets the title of the application.
     * 
     * @param title
     *            Title.
     */
    public final void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Returns the vendor information.
     * 
     * @return Vendor.
     */
    public final String getVendor() {
        return Utils4J.replaceVars(vendor, cmdLineOptions);
    }

    /**
     * Sets the vendor information.
     * 
     * @param vendor
     *            Vendor.
     */
    public final void setVendor(final String vendor) {
        this.vendor = vendor;
    }

    /**
     * Returns the list of known source files.
     * 
     * @return List of <code>SrcFile</code> objects.
     */
    public final List getSrcFiles() {
        return srcFiles;
    }

    /**
     * Returns the list of directories to create.
     * 
     * @return List of <code>MkDir</code> objects.
     */
    public final List getMkDirs() {
        return mkDirs;
    }

    /**
     * Returns the list of known source directories.
     * 
     * @return List of <code>SrcDir</code> objects.
     */
    public final List getSrcDirs() {
        return srcDirs;
    }

    /**
     * Returns the URL of the configuration file (only used when
     * lazyLoading=true).
     * 
     * @return URL or <code>null</code>.
     */
    public final String getConfigFileUrl() {
        return Utils4J.replaceVars(configFileUrl, cmdLineOptions);
    }

    /**
     * Returns the URL of the configuration file (only used when
     * lazyLoading=true).
     * 
     * @return URL or <code>null</code>.
     */
    public final URL getConfigFileURL() {
        if (configFileUrl == null) {
            return null;
        }
        try {
            return new URL(Utils4J.replaceVars(configFileUrl, cmdLineOptions));
        } catch (final MalformedURLException ex) {
            throw new RuntimeException("Error creating URL from String '" + configFileUrl + "'!",
                    ex);
        }
    }

    /**
     * Sets the URL of the configuration file (only used when lazyLoading=true).
     * 
     * @param configFileUrl
     *            URL or <code>null</code>.
     */
    public final void setConfigFileUrl(final String configFileUrl) {
        this.configFileUrl = configFileUrl;
    }

    /**
     * Returns the target application version.
     * 
     * @return Version or <code>null</code> if not set.
     */
    public final String getVersion() {
        return Utils4J.replaceVars(version, cmdLineOptions);
    }

    /**
     * Sets the target application version.
     * 
     * @param version
     *            Version or <code>null</code>.
     */
    public final void setVersion(final String version) {
        this.version = version;
    }

    /**
     * Returns the encoding to use for XML out.
     * 
     * @return Encoding like "ISO-8859-1" (default) or "UTF-8".
     */
    public final String getXmlEncoding() {
        return xmlEncoding;
    }

    /**
     * Sets the encoding to use for XML out.
     * 
     * @param encoding
     *            Encoding like "ISO-8859-1" (default) or "UTF-8".
     */
    public final void setXmlEncoding(final String encoding) {
        this.xmlEncoding = encoding;
    }

    /**
     * Show a "starting application" frame?
     * 
     * @return If a frame will be displayed <code>true</code> (default) else
     *         <code>false</code>.
     */
    public final boolean isShowStartFrame() {
        return showStartFrame;
    }

    /**
     * Show a "starting application" frame?
     * 
     * @param b
     *            If a frame should be displayed <code>true</code> else
     *            <code>false</code>.
     */
    public final void setShowStartFrame(final boolean b) {
        showStartFrame = b;
    }

    /**
     * Show the start frame for N seconds after application has started.
     * 
     * @return Seconds (Default = 2)
     */
    public final int getStartFrameDelaySeconds() {
        return startFrameDelaySeconds;
    }

    /**
     * Show the start frame for N seconds after application has started.
     * 
     * @param seconds
     *            Number of seconds.
     */
    public final void setStartFrameDelaySeconds(final int seconds) {
        startFrameDelaySeconds = seconds;
    }

    /**
     * Returns the name and path of the log file.
     * 
     * @return Name and path of log file - ALways non-<code>null</code>.
     */
    public final String getLogFilename() {
        return Utils4J.replaceVars(logFilename, cmdLineOptions);
    }

    /**
     * Sets the name and path of the log file.
     * 
     * @param logFilename
     *            Log filename - A <code>null</code> value will set the name to
     *            "kickstart4j.log".
     */
    public final void setLogFilename(final String logFilename) {
        if (logFilename == null) {
            this.logFilename = System.getProperty("user.home") + File.separator + "kickstart4j.log";
        } else {
            this.logFilename = logFilename.trim();
        }
    }

    /**
     * Find a source file by it's path and filename.
     * 
     * @param path
     *            Path - Cannot be <code>null</code> but empty.
     * @param filename
     *            Filename to find - Cannot be <code>null</code>.
     * 
     * @return Source file - Always non-<code>null</code>.
     * 
     * @throws SrcFileNotFoundException
     *             The file was not found.
     */
    public final SrcFile findSrcFile(final String path, final String filename)
            throws SrcFileNotFoundException {

        Utils4J.checkNotNull("path", path);
        Utils4J.checkNotNull("filename", filename);

        final String systemPath = path.replace('/', File.separatorChar);

        for (int i = 0; i < srcFiles.size(); i++) {
            final SrcFile srcFile = (SrcFile) srcFiles.get(i);
            if (srcFile.getPath().equals(systemPath) && srcFile.getFilename().equals(filename)) {
                return srcFile;
            }
        }

        throw new SrcFileNotFoundException(path, filename);
    }

    /**
     * Find a source directory by it's path.
     * 
     * @param path
     *            Path - Cannot be <code>null</code> but empty.
     * 
     * @return Source directory - Always non-<code>null</code>.
     * 
     * @throws SrcDirNotFoundException
     *             The directory was not found.
     */
    public final SrcDir findSrcDir(final String path) throws SrcDirNotFoundException {

        Utils4J.checkNotNull("path", path);

        final String systemPath = path.replace('/', File.separatorChar);

        for (int i = 0; i < srcDirs.size(); i++) {
            final SrcDir srcDir = (SrcDir) srcDirs.get(i);
            if (srcDir.getPath().equals(systemPath)) {
                return srcDir;
            }
        }

        throw new SrcDirNotFoundException(path);
    }

    /**
     * Replaces a source file with another one.
     * 
     * @param oldSrcFile
     *            Source file to replace.
     * @param newSrcFile
     *            File to insert.
     */
    public final void replace(final SrcFile oldSrcFile, final SrcFile newSrcFile) {
        final int i = srcFiles.indexOf(oldSrcFile);
        if (i == -1) {
            throw new IllegalArgumentException("The source file '" + oldSrcFile
                    + "' was not found!");
        }
        srcFiles.remove(i);
        srcFiles.add(i, newSrcFile);
    }

    private String getText(final String value, final String defaultVal) {
        if (value == null) {
            return defaultVal;
        }
        return value;
    }

    private String getTag(final String tag, final String value, final String defaultVal) {
        if ((value == null) && (defaultVal == null)) {
            return "<" + tag + "/>";
        }
        return "<" + tag + ">" + getText(value, defaultVal) + "</" + tag + ">";
    }

    private String getTag(final String tag, final boolean value) {
        return "<" + tag + ">" + value + "</" + tag + ">";
    }

    private String getTag(final String tag, final int value) {
        return "<" + tag + ">" + value + "</" + tag + ">";
    }

    private String getTag(final String tag, final Locale locale, final Locale defaultLocale) {
        if ((locale == null) && (defaultLocale == null)) {
            return "<" + tag + "/>";
        }
        if (locale == null) {
            return "<" + tag + ">" + defaultLocale.getLanguage() + "</" + tag + ">";
        }
        return "<" + tag + ">" + locale.getLanguage() + "</" + tag + ">";
    }

    private String getTagLine(final String tag, final String value, final String defaultVal) {
        return getTag(tag, value, defaultVal) + IOUtils.LINE_SEPARATOR;
    }

    private String getTagLine(final String tag, final boolean value) {
        return getTag(tag, value) + IOUtils.LINE_SEPARATOR;
    }

    private String getTagLine(final String tag, final int value) {
        return getTag(tag, value) + IOUtils.LINE_SEPARATOR;
    }

    private String getTagLine(final String tag, final Locale locale, final Locale defaultLocale) {
        return getTag(tag, locale, defaultLocale) + IOUtils.LINE_SEPARATOR;
    }

    /**
     * Returns the configuration as XML with all variables replaced with their
     * values.
     * 
     * @return XML configuration.
     */
    public final String toStaticXML() {
        final StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"" + xmlEncoding + "\"?>"
                + IOUtils.LINE_SEPARATOR);
        sb.append("<application>" + IOUtils.LINE_SEPARATOR);
        sb.append("  " + getTagLine("version", getVersion(), null));
        sb.append("  " + getTagLine("title", getTitle(), null));
        sb.append("  " + getTagLine("vendor", getVendor(), null));
        sb.append("  " + getTagLine("description", getDescription(), null));
        sb.append("  " + getTagLine("exitAfterExecute", isExitAfterExecute()));
        sb.append("  " + getTagLine("destPath", getDestPath(), null));
        sb.append("  " + getTagLine("idFilename", getIdFilename(), ".yourapp"));
        sb.append("  " + getTagLine("silentInstall", isSilentInstall()));
        sb.append("  " + getTagLine("silentUpdate", isSilentUpdate()));
        sb.append("  " + getTagLine("locale", locale, Locale.getDefault()));
        sb.append("  " + getTagLine("lazyLoading", isLazyLoading()));
        sb.append("  " + getTagLine("showStartFrame", isShowStartFrame()));
        sb.append("  " + getTagLine("startFrameDelaySeconds", getStartFrameDelaySeconds()));
        sb.append("  " + getTagLine("logFilename", getLogFilename(), null));
        sb.append("  " + getTagLine("javaExe", getJavaExe(), null));
        sb.append("  "
                + getTagLine("javaArgs", getJavaArgs(),
                        "-classpath ${classpath} com.company.product.MainClass"));
        sb.append("  " + getTagLine("msgFileUrl", msgFileUrl, null));
        for (int i = 0; i < mkDirs.size(); i++) {
            final MkDir mkDir = (MkDir) mkDirs.get(i);
            sb.append("  " + mkDir.toXML() + IOUtils.LINE_SEPARATOR);
        }
        for (int i = 0; i < srcDirs.size(); i++) {
            final SrcDir srcDir = (SrcDir) srcDirs.get(i);
            sb.append("  " + srcDir.toXML() + IOUtils.LINE_SEPARATOR);
        }
        for (int i = 0; i < srcFiles.size(); i++) {
            final SrcFile srcFile = (SrcFile) srcFiles.get(i);
            sb.append("  " + srcFile.toXML() + IOUtils.LINE_SEPARATOR);
        }
        sb.append("</application>" + IOUtils.LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * Returns the configuration as XML with no variables replaced.
     * 
     * @return XML configuration.
     */
    public final String toVarXML() {
        final StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"" + xmlEncoding + "\"?>"
                + IOUtils.LINE_SEPARATOR);
        sb.append("<application>" + IOUtils.LINE_SEPARATOR);
        sb.append("  " + getTagLine("version", version, null));
        sb.append("  " + getTagLine("title", title, "Your title"));
        sb.append("  " + getTagLine("vendor", vendor, null));
        sb.append("  " + getTagLine("description", description, null));
        sb.append("  " + getTagLine("exitAfterExecute", exitAfterExecute));
        sb.append("  " + getTagLine("destPath", destPath, "C:\\Program Files\\yourapp\\"));
        sb.append("  " + getTagLine("idFilename", idFilename, ".yourapp"));
        sb.append("  " + getTagLine("silentInstall", silentInstall));
        sb.append("  " + getTagLine("silentUpdate", silentUpdate));
        sb.append("  " + getTagLine("locale", locale, Locale.getDefault()));
        sb.append("  " + getTagLine("lazyLoading", lazyLoading));
        sb.append("  " + getTagLine("showStartFrame", isShowStartFrame()));
        sb.append("  " + getTagLine("startFrameDelaySeconds", getStartFrameDelaySeconds()));
        sb.append("  " + getTagLine("logFilename", logFilename, null));
        sb.append("  " + getTagLine("javaExe", javaExe, "jre/bin/java.exe"));
        sb.append("  "
                + getTagLine("javaArgs", javaArgs,
                        "-classpath ${classpath} com.company.product.MainClass"));
        sb.append("  " + getTagLine("msgFileUrl", msgFileUrl, null));
        for (int i = 0; i < mkDirs.size(); i++) {
            final MkDir mkDir = (MkDir) mkDirs.get(i);
            sb.append("  " + mkDir.toXML() + IOUtils.LINE_SEPARATOR);
        }
        for (int i = 0; i < srcDirs.size(); i++) {
            final SrcDir srcDir = (SrcDir) srcDirs.get(i);
            sb.append("  " + srcDir.toXML() + IOUtils.LINE_SEPARATOR);
        }
        for (int i = 0; i < srcFiles.size(); i++) {
            final SrcFile srcFile = (SrcFile) srcFiles.get(i);
            sb.append("  " + srcFile.toXML() + IOUtils.LINE_SEPARATOR);
        }
        sb.append("</application>" + IOUtils.LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        return toDebugString();
    }

    /**
     * {@inheritDoc}
     */
    public final String toDebugString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("configFileUrl=" + getConfigFileUrl() + ", ");
        sb.append("version=" + getVersion() + ", ");
        sb.append("title=" + getTitle() + ", ");
        sb.append("vendor=" + getVendor() + ", ");
        sb.append("description=" + getDescription() + ", ");
        sb.append("exitAfterExecute=" + isExitAfterExecute() + ", ");
        sb.append("destPath=" + getDestPath() + ", ");
        sb.append("idFilename=" + getIdFilename() + ", ");
        sb.append("silentInstall=" + isSilentInstall() + ", ");
        sb.append("silentUpdate=" + isSilentUpdate() + ", ");
        sb.append("locale=" + getLocale() + ", ");
        sb.append("lazyLoading=" + isLazyLoading() + ", ");
        sb.append("showStartFrame=" + isShowStartFrame() + ", ");
        sb.append("startFrameDelaySeconds=" + getStartFrameDelaySeconds() + ", ");
        sb.append("javaExe=" + getJavaExe() + ", ");
        sb.append("javaArgs=" + getJavaArgs() + ", ");
        sb.append("msgFileUrl=" + getMsgFileUrl() + ", ");
        sb.append("xmlEncoding=" + getXmlEncoding());
        sb.append("logFilename=" + getLogFilename());
        sb.append("srcFiles.size()=" + srcFiles.size());
        sb.append("srcDirs.size()=" + srcDirs.size());
        sb.append("mkDirs.size()=" + mkDirs.size());
        return sb.toString();
    }

    /**
     * Writes this configuration to an XML file with no variables replaced.
     * 
     * @param configFile
     *            Target file.
     * @param backup
     *            Create a backup if this file already exists (same filename but
     *            with ".bak" extension).
     * 
     * @throws IOException
     *             Error writing the file.
     */
    public final void writeToVarXML(final File configFile, final boolean backup) throws IOException {
        writeToXML(toVarXML(), configFile, backup);
    }

    /**
     * Writes this configuration to an XML file with all variables replaced with
     * their values.
     * 
     * @param configFile
     *            Target file.
     * @param backup
     *            Create a backup if this file already exists (same filename but
     *            with ".bak" extension).
     * 
     * @throws IOException
     *             Error writing the file.
     */
    public final void writeToStaticXML(final File configFile, final boolean backup)
            throws IOException {
        writeToXML(toStaticXML(), configFile, backup);
    }

    /**
     * Writes this configuration to an XML file.
     * 
     * @param configFile
     *            Target file. If this file already exists a backup (same
     *            filename but with ".bak" extension) will be created.
     * @param backup
     *            Create a backup if this file already exists (same filename but
     *            with ".bak" extension).
     * 
     * @throws IOException
     *             Error writing the file.
     */
    private final void writeToXML(final String xml, final File file, final boolean backup)
            throws IOException {

        // Save old file?
        if (backup) {
            final File bakFile = new File(file + ".bak");
            if (bakFile.exists()) {
                bakFile.delete();
            }
            file.renameTo(bakFile);
        }

        // Write current values
        final Writer writer = new BufferedWriter(new FileWriter(file));
        try {
            writer.write(xml);
        } finally {
            writer.close();
        }

    }

    /**
     * {@inheritDoc}
     */
    public final boolean simpleAttributesEquals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Config)) {
            return false;
        }
        final Config theOther = (Config) obj;
        return Utils.nullSafeEquals(lookAndFeelClassName, theOther.lookAndFeelClassName)
                && Utils.nullSafeEquals(idFilename, theOther.idFilename)
                && (exitAfterExecute == theOther.exitAfterExecute)
                && Utils.nullSafeEquals(destPath, theOther.destPath)
                && (silentInstall == theOther.silentInstall)
                && (silentUpdate == theOther.silentUpdate)
                && (firstInstallation == theOther.firstInstallation)
                && Utils.nullSafeEquals(locale, theOther.locale)
                && Utils.nullSafeEquals(msgFileUrl, theOther.msgFileUrl)
                && (lazyLoading == theOther.lazyLoading)
                && Utils.nullSafeEquals(javaExe, theOther.javaExe)
                && Utils.nullSafeEquals(javaArgs, theOther.javaArgs)
                && Utils.nullSafeEquals(title, theOther.title)
                && Utils.nullSafeEquals(vendor, theOther.vendor)
                && Utils.nullSafeEquals(description, theOther.description)
                && Utils.nullSafeEquals(configFileUrl, theOther.configFileUrl)
                && Utils.nullSafeEquals(version, theOther.version)
                && Utils.nullSafeEquals(xmlEncoding, theOther.xmlEncoding)
                && (showStartFrame == theOther.showStartFrame)
                && (startFrameDelaySeconds == theOther.startFrameDelaySeconds)
                && Utils.nullSafeEquals(logFilename, theOther.logFilename);
    }
}
