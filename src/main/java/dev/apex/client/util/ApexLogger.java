package dev.apex.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApexLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("ApexClient");

    public static void info(String msg)  { LOGGER.info("[Apex] " + msg); }
    public static void warn(String msg)  { LOGGER.warn("[Apex] " + msg); }
    public static void error(String msg) { LOGGER.error("[Apex] " + msg); }
}
