package ps.gov.epsilon.hr.enums.v1

import grails.util.Holders
import ps.police.common.utils.v1.PCPSessionUtils


public enum EnumJobCategory {

    CIVIL_EMPLOYEE(1L),
    SOLDIER(2L),
    HEAD_OF_FIRM(3L),
    HEAD_OF_DEPARTMENT(4L),
    HEAD_OF_UNIT(5L),
    HEAD_OF_SECTION(6L),
    HEAD_OF_GOVERNORATE(7L),
    DEPUTY_HEAD_OF_FIRM(8L),
    DEPUTY_HEAD_OF_DEPARTMENT(9L),
    DEPUTY_HEAD_OF_UNIT(10L),
    DEPUTY_HEAD_OF_SECTION(11L),
    DEPUTY_HEAD_OF_GOVERNORATE(12L),

    private final Long value;

    EnumJobCategory(Long value) {
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