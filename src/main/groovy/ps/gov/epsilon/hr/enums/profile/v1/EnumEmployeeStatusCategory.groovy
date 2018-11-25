package ps.gov.epsilon.hr.enums.profile.v1

import grails.util.Holders
import ps.police.common.utils.v1.PCPSessionUtils

/**
 * Created by hamayel on 11/07/17.
 */

// REFLECT THE EmployeeStatusCategory LOOKUP
enum EnumEmployeeStatusCategory {
    COMMITTED(1L),
    UNCOMMITTED(2L)

    final Long value;

    EnumEmployeeStatusCategory(Long value) {
        this.value = value;
    }


    String getValue(String firmCode) {
        firmCode + "-" + this.value.toString();
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