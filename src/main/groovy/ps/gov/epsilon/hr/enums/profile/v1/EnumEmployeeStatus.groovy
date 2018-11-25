package ps.gov.epsilon.hr.enums.profile.v1

import grails.util.Holders
import ps.police.common.utils.v1.PCPSessionUtils

/**
 * Created by hamayel on 11/07/17.
 */

// REFLECT THE EmployeeStatus LOOKUP
enum EnumEmployeeStatus {
    //Committed Status
    WORKING(1L),
    ABSENCE(2L),
    DISPATCHED(3L),
    LOAN_OUT(4L),
    IN_VACATION(12L),

    //Un Committed Status
    TRANSFERRED(5L),
    LOAN_IN(6L),
    RETIREMENT(7L),
    FIRING(8L),
    RESIGNATION(9L),
    DEATH(10L),
    STUDYING(11L),
    SUSPENDED(13L),

    final Long value;

    EnumEmployeeStatus(Long value) {
        this.value = value;
    }

    String getValue() {
        toString()
    }

    String getValue(String firmCode) {
        firmCode + "-" + this.value.toString();
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