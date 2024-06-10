package nkp.pspValidator.gui;

import nkp.pspValidator.shared.Dmf;
import nkp.pspValidator.shared.Platform;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Martin Řehánek on 2.12.16.
 */
public class ConfigurationManager {

    public static boolean DEV_MODE = false;
    public static boolean DEV_MODE_ONLY_SELECTED_SECTIONS = false;

    private static final String DEFAULT_LOG_DIR = "logs";
    private static final String DEFAULT_FDMF_DIR = "validatorConfig";

    private static final String PROD_CONFIG_DIR = ".validator-epublikaci";
    private static String CONFIG_FILE_NAME_PRODUCTION = "config.properties";
    private static File CONFIG_FILE_DEV_WIN = new File("../../resources/main/dev/config-win.properties");
    private static File CONFIG_FILE_DEV_MAC = new File("../../resources/main/dev/config-mac.properties");
    private static File CONFIG_FILE_DEV_LINUX = new File("../../resources/main/dev/config-linux.properties");

    //fdmf
    public static final String PROP_VALIDATOR_CONFIG_DIR = "validatorConfig.dir";

    //external tools
    public static final String PROP_EXTERNAL_TOOLS_CHECK_SHOWN = "external_tools_check.shown";
    /*public static final String PROP_JHOVE_DIR = "jhove.dir";
    public static final String PROP_JPYLYZER_DIR = "jpylyzer.dir";
    public static final String PROP_IMAGE_MAGICK_DIR = "imageMagick.dir";
    public static final String PROP_KAKADU_DIR = "kakadu.dir";
    public static final String PROP_MP3VAL_DIR = "mp3val.dir";
    public static final String PROP_SHNTOOL_DIR = "shntool.dir";
    public static final String PROP_CHECKMATE_DIR = "checkmate.dir";*/
    public static final String PROP_VERAPDF_DIR = "verapdf.dir";
    public static final String PROP_EPUBCHECK_DIR = "epubcheck.dir";

    //validation
    public static final String PROP_LAST_PSP_DIR = "last.psp.dir";
    public static final String PROP_LAST_PSP_ZIP = "last.psp.zip";
    public static final String PROP_FORCE_EMON_VERSION_ENABLED = "force.emonograph.version.enabled";
    public static final String PROP_FORCE_EMON_VERSION_CODE = "force.emonograph.version.code";
    public static final String PROP_FORCE_EPER_VERSION_ENABLED = "force.eperiodical.version.enabled";
    public static final String PROP_FORCE_EPER_VERSION_CODE = "force.eperiodical.version.code";
    public static final String PROP_PREFER_EMON_VERSION_ENABLED = "prefer.emonograph.version.enabled";
    public static final String PROP_PREFER_EMON_VERSION_CODE = "prefer.emonograph.version.code";
    public static final String PROP_PREFER_EPER_VERSION_ENABLED = "prefer.eperiodical.version.enabled";
    public static final String PROP_PREFER_EPER_VERSION_CODE = "prefer.eperiodical.version.code";
    public static final String PROP_PSP_VALIDATION_CREATE_TXT_LOG = "psp_validation.create_txt_log";
    public static final String PROP_PSP_VALIDATION_CREATE_XML_LOG = "psp_validation.create_xml_log";
    public static final String PROP_LOG_DIR = "validation.log_dir";

    //dictionaries
    public static final String propDictionarySpecUrl(String dictionary) {
        return "dictionary." + dictionary + ".specUrl";
    }

    public static final String propDictionaryDescription(String dictionary) {
        return "dictionary." + dictionary + ".description";
    }

    public static final String propDictionarySyncDate(String dictionary) {
        return "dictionary." + dictionary + ".syncDate";
    }

    public static final String propDictionarySyncUrl(String dictionary) {
        return "dictionary." + dictionary + ".syncUrl";
    }

    //skipped sections
    public static final String propSkippedValidationSections(Dmf dmf) {
        String dmfCode = dmf.getType().name() + "-" + dmf.getVersion();
        return "skipped.validation.sections." + dmfCode;
    }

    //text log verbosity
    public static final String PROP_TEXT_LOG_VERBOSITY = "text_log.verbosity";

    private final Platform platform;
    private final File configFile;
    private final Properties properties = new Properties();

    public ConfigurationManager(Platform platform) throws IOException {
        try {
            this.platform = platform;
            this.configFile = selectConfigFile();
            loadProperties();
            initDefaultProperties();
        } catch (IOException e) {
            throw new IOException(new File(".").getAbsolutePath(), e);
        }
    }

    private void initDefaultProperties() {
        //validator config dir
        File validatorConfigDir = getFileOrNull(PROP_VALIDATOR_CONFIG_DIR);
        if (validatorConfigDir == null) {
            validatorConfigDir = getDefaultValidatorConfigDir();
            setFile(PROP_VALIDATOR_CONFIG_DIR, validatorConfigDir);
        }
        //log dir
        File logDir = getFileOrNull(PROP_LOG_DIR);
        if (logDir == null) {
            logDir = getDefaultLogDir();
            setFile(PROP_LOG_DIR, logDir);
        }
        logDir.mkdirs();
        //dictionaries
        initDictionary("siglaInstitutionCodes",
                "kódy institucí (Sigla)",
                null,
                "https://raw.githubusercontent.com/NLCR/validator-e-publikaci/master/modules/sharedModule/src/main/resources/nkp/pspValidator/shared/validatorConfig/dictionaries/siglaInstitutionCodes.dict");
        initDictionary("iso31661Alpha2languageCodes",
                "kódy zemí ISO3166-1 alpha-2",
                "https://www.iso.org/iso-3166-country-codes.html",
                null);
        initDictionary("iso6392languageCodes",
                "kódy jazyků podle ISO 639-2",
                "http://www.loc.gov/standards/iso639-2/php/code_list.php",
                null);
        initDictionary("marcRelatorCodes",
                "kódy rolí podle MARC",
                "https://www.loc.gov/marc/relators/relaterm.html",
                null);
    }

    private File getDefaultLogDir() {
        String userHome = System.getProperty("user.home");
        File logDir = Paths.get(userHome, PROD_CONFIG_DIR, DEFAULT_LOG_DIR).toFile();
        System.out.println("getDefaultLogDir(): " + logDir.getAbsolutePath());
        if (!logDir.exists()) {
            try {
                //Log dire does not exist, create it and parent dirs
                logDir.getParentFile().mkdirs();
                logDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logDir;
    }

    private File getDefaultValidatorConfigDir() {
        String jarPath = getJarPath();
        //TODO: Linux
        File appDir = Paths.get(jarPath).toFile(); //ValidatorEpublikaci.app/Contents/app
        File contentsDir = appDir.getParentFile(); //ValidatorEpublikaci.app/Contents
        File appContentDir = new File(contentsDir, "app-content"); //ValidatorEpublikaci.app/Contents/app-content
        File validatorConfigDir = new File(appContentDir, "validatorConfig"); //ValidatorEpublikaci.app/Contents/app-content/validatorConfig
        System.out.println("getDefaultValidatorConfigDir(): " + validatorConfigDir);
        return validatorConfigDir;
    }

    private String getJarPath() {
        try {
            String path = ConfigurationManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            String result = new File(decodedPath).getParent();
            System.out.println("getJarPath(): " + result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to determine JAR path", e);
        }
    }

    private void initDictionary(String name, String description, String specUrl, String syncUrl) {
        updateStringPropertyIfOldValueEmptyAndNewValueNot(propDictionaryDescription(name), description);
        updateStringPropertyIfOldValueEmptyAndNewValueNot(propDictionarySpecUrl(name), specUrl);
        updateStringPropertyIfOldValueEmptyAndNewValueNot(propDictionarySyncUrl(name), syncUrl);
    }

    private void updateStringPropertyIfOldValueEmptyAndNewValueNot(String key, String newValue) {
        String oldValue = getStringOrDefault(key, null);
        if (oldValue == null || oldValue.isEmpty()) {
            if (newValue != null && !newValue.isEmpty()) {
                setString(key, newValue);
            }
        }
    }

    private File selectConfigFile() {
        System.out.println("selectConfigFile() current dir: " + new File(".").getAbsolutePath());
        //list files in current dir:
        //System.out.println("files here: ");
        //Arrays.stream(new File(".").listFiles()).sorted().forEach(System.out::println);
        if (DEV_MODE) {
            switch (platform.getOperatingSystem()) {
                case LINUX:
                    return CONFIG_FILE_DEV_LINUX;
                case WINDOWS:
                    return CONFIG_FILE_DEV_WIN;
                case MAC:
                    return CONFIG_FILE_DEV_MAC;
                default:
                    throw new RuntimeException("unknown platform: " + platform.getOperatingSystem());
            }
        } else {
            return detectProductionConfigFile();
        }
    }

    private File detectProductionConfigFile() {
        String userHome = System.getProperty("user.home");
        File configFile = Paths.get(userHome, PROD_CONFIG_DIR, CONFIG_FILE_NAME_PRODUCTION).toFile();
        System.out.println("detectProductionConfigFile(): " + configFile.getAbsolutePath());
        if (!configFile.exists()) {
            try {
                //Config file does not exist, create it and parent dirs
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return configFile;
    }

    private void loadProperties() throws IOException {
        if (configFile.exists()) {
            properties.load(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
        }
    }

    public File getFileOrNull(String propertyName) {
        String file = properties.getProperty(propertyName);
        return file == null ? null : new File(file);
    }

    public boolean getBooleanOrDefault(String propertyName, boolean defaultValue) {
        String stringVal = properties.getProperty(propertyName);
        if (stringVal == null) {
            return defaultValue;
        } else {
            return Boolean.valueOf(stringVal);
        }
    }

    public Integer getIntegerOrNull(String propertyName) {
        String stringVal = properties.getProperty(propertyName);
        if (stringVal == null) {
            return null;
        } else {
            return Integer.valueOf(stringVal);
        }
    }

    public String getStringOrDefault(String propertyName, String defaultValue) {
        String stringVal = properties.getProperty(propertyName);
        if (stringVal == null) {
            return defaultValue;
        } else {
            return stringVal;
        }
    }

    /**
     * @param propertyName
     * @return never null, if value is not found an empty Set<String> is returned
     */
    public Set<String> getStringSet(String propertyName) {
        String stringVal = properties.getProperty(propertyName);
        if (stringVal == null) {
            return Collections.emptySet();
        } else {
            String[] items = stringVal.split(",");
            HashSet<String> result = new HashSet<>(items.length);
            for (String item : items) {
                result.add(item);
            }
            return result;
        }
    }

    public void setInteger(String propertyName, Integer value) {
        if (value != null) {
            properties.setProperty(propertyName, value.toString());
            saveProperties();
        }
    }

    public void setBoolean(String propertyName, Boolean value) {
        if (value != null) {
            properties.setProperty(propertyName, value.toString());
            saveProperties();
        }
    }

    public void setString(String propertyName, String value) {
        if (value != null) {
            properties.setProperty(propertyName, value);
            saveProperties();
        }
    }

    public void setStringSet(String propertyName, Set<String> set) {
        if (set != null) {
            StringBuilder builder = new StringBuilder();
            int counter = 0;
            for (String item : set) {
                builder.append(item);
                if (counter != set.size() - 1) {
                    builder.append(',');
                }
                counter++;
            }
            String value = builder.toString();
            properties.setProperty(propertyName, value);
            saveProperties();
        }
    }

    public void setFile(String propertyName, File file) {
        try {
            if (file == null) {
                properties.remove(propertyName);
            } else {
                properties.setProperty(propertyName, file.getCanonicalPath());
            }
            saveProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveProperties() {
        try {
            OutputStream out = new FileOutputStream(configFile);
            properties.store(out, null);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(String.format("chyba při zápisu do souboru %s", configFile.getAbsolutePath()), e);
        }
    }

    public Platform getPlatform() {
        return platform;
    }


}
