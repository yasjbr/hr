package ps.gov.epsilon.hr.enums.disciplinary.v1

import grails.util.Holders
import ps.police.common.utils.v1.PCPSessionUtils

/**
 * Created by hatallah on 24/08/17.
 */
enum EnumDisciplinaryReason {
    //غياب
    ABSENCE_REASON(1L)

    final Long value;

    EnumDisciplinaryReason(Long value) {
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