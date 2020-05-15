package io.github.lsyf.log4j2.desensitization;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

import java.util.ArrayList;
import java.util.List;


@Plugin(name = "desensitized", category = Core.CATEGORY_NAME, printObject = true)
public final class DesensitizedReplacement {

    private static final Logger LOGGER = StatusLogger.getLogger();

    private final DesensitizedField[] fields;


    private final LoggingScanner loggingScanner;


    private DesensitizedReplacement(DesensitizedField[] fields) {
        this.fields = fields;
        List<LoggingScanner.Config> configs = new ArrayList<>(fields.length);
        for (DesensitizedField f : fields) {
            configs.add(new LoggingScanner.Config(
                    f.getName(),
                    f.getContent(),
                    f.getSkipHead(),
                    f.getSkipTail(),
                    f.getC(),
                    f.getSkipSymbols(),
                    f.getMaxMissed()
            ));
        }
        loggingScanner = new LoggingScanner(true, configs);
    }


    public String format(final String msg) {
        return loggingScanner.processLog(msg);
    }

    @Override
    public String toString() {
        return "desensitized(fields=" + fields + ')';
    }


    @PluginFactory
    public static DesensitizedReplacement createRegexReplacement(
            @PluginElement("desensitizedFields") final DesensitizedField[] fields,
            @PluginAttribute("enable") final Boolean enable) {
        if (!Boolean.TRUE.equals(enable)) {
            return null;
        }
        if (fields == null) {
            LOGGER.error("fields is required for rules");
            return null;
        }

        return new DesensitizedReplacement(fields);
    }

}
