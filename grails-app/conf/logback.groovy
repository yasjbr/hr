import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.util.FileSize
import grails.util.BuildSettings
import grails.util.Environment
import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.PatternLayout
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.ERROR


// See http://logback.qos.ch/manual/groovy.html for details on configuration
//appender('STDOUT', ConsoleAppender) {
//    encoder(PatternLayoutEncoder) {
//        pattern = "%level %logger - %msg%n"
//    }
//}
//
//root(ERROR, ['STDOUT'])
//
//def targetDir = BuildSettings.TARGET_DIR
//if (Environment.isDevelopmentMode() && targetDir) {
//    appender("FULL_STACKTRACE", FileAppender) {
//        file = "${targetDir}/stacktrace.log"
//        append = true
//        encoder(PatternLayoutEncoder) {
//            pattern = "%level %logger - %msg%n"
//        }
//    }
//    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
//}
//

scan("30 seconds")

def APP_NAME = "EPHR"
def targetDir = BuildSettings.TARGET_DIR
def LOG_PATH
def LOG_ARCHIVE


switch (Environment.current) {
    case Environment.PRODUCTION:
        LOG_PATH = "/opt/webapps/${APP_NAME}/logs"
        LOG_ARCHIVE = "${LOG_PATH}/log_archive"
        break;

    case Environment.DEVELOPMENT:
        LOG_PATH = "${targetDir}/${APP_NAME}/logs"
        LOG_ARCHIVE = "${LOG_PATH}/log_archive"
        break;

    case Environment.TEST:
        LOG_PATH = "${targetDir}/${APP_NAME}/logs"
        break;
}


appender("Console-Appender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{dd-MM-yy HH:mm:ss,SSS} %level %logger - %msg%n"
    }
}

appender("File-Appender", FileAppender) {
    file = "${LOG_PATH}/logfile.log"
    append = true
    encoder(PatternLayoutEncoder) {
        pattern = "%d{dd-MM-yy HH:mm:ss,SSS} %level %logger - %msg%n"
        outputPatternAsHeader = true
    }
}

appender("RollingFile-Appender", RollingFileAppender) {
    file = "${LOG_PATH}/rollingfile.log"
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${LOG_ARCHIVE}/%d{yyyy-MM-dd}-rollingfile.log"
        maxHistory = 15
        totalSizeCap = FileSize.valueOf("64 mb")
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{dd-MM-yy HH:mm:ss,SSS} %level %logger - %msg%n"
    }
}

appender("Async-Appender", AsyncAppender) {
    appenderRef("RollingFile-Appender")
}

switch (Environment.current) {
    case Environment.PRODUCTION:
//        logger("FullTrace", INFO, ["Console-Appender", "File-Appender", "Async-Appender"], false) // log all
        logger("FullTrace", ERROR, ["File-Appender", "Async-Appender"], false)   //log errors
        root(INFO, ["Console-Appender"])
        break;

    case Environment.DEVELOPMENT:
        logger("FullTrace", DEBUG, ["Console-Appender", "File-Appender", "Async-Appender"], false)
        root(INFO, ["Console-Appender"])
        break;

    case Environment.TEST:
        root(INFO, ["Console-Appender"])
        break;
}
