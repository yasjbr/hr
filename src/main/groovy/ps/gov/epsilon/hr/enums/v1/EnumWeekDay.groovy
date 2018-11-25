package ps.gov.epsilon.hr.enums.v1

import grails.util.Holders
import ps.police.common.utils.v1.PCPSessionUtils


public enum EnumWeekDay {

    MONDAY(2L),
    TUESDAY(3L),
    WEDNESDAY(4L),
    THURSDAY(5L),
    FRIDAY(6L),
    SATURDAY(7L),
    SUNDAY(1L)
    private final Long value;

    EnumWeekDay(Long value) {
        this.value = value;
    }


    String getValue() {
        toString()
    }
    String toString() {
        return getFirm() + "-" + this.value.toString();
    }

    String getKey() {
        name()
    }

    String getFirm(){
        String defaultFirmCode = Holders.grailsApplication.config.grails.defaultFirmCode ?: "FIRM"
        String value = PCPSessionUtils.getValue("firmCode") ?: defaultFirmCode
        return value
    }


}