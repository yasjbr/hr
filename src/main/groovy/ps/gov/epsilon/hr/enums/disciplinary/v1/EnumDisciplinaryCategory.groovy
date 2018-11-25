package ps.gov.epsilon.hr.enums.disciplinary.v1

import grails.util.Holders
import ps.police.common.utils.v1.PCPSessionUtils

/**
 * Created by mkharma on 13/03/17.
 */
enum EnumDisciplinaryCategory {
    //عقوبة انضباطية
    DISCIPLINARY(1L)

    final Long value;

    EnumDisciplinaryCategory(Long value) {
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