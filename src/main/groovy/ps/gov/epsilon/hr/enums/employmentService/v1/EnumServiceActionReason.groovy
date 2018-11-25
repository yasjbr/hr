package ps.gov.epsilon.hr.enums.employmentService.v1

import grails.util.Holders
import ps.police.common.utils.v1.PCPSessionUtils

/**
 * USED TO REFLECT LOOKUP VALUES DEPENDS ON THE CASES
 *
 *  RETIREMENT,
 FIRING,
 EXCEPTIONAL,
 MEDICAL,
 DEATH,
 RESIGNATION
 *
 */


enum EnumServiceActionReason {
    RETIREMENT(1L),
    SUSPENSION(2L),

    final Long value;

    EnumServiceActionReason(Long value) {
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