package ps.gov.epsilon.hr.enums.allowance.v1

import grails.util.Holders
import ps.police.common.utils.v1.PCPSessionUtils

/**
 * Created by mkharma on 30/03/17.
 */

// REFLECT THE AllowanceType LOOKUP
enum EnumAllowanceType {
    SON(1L),
    DAUGHTER(2L),
    WIFE(3L),
    HUSBAND(4L)

    final Long value;

    EnumAllowanceType(Long value) {
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