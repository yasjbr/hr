package ps.gov.epsilon.hr.common

import grails.artefact.TagLibrary
import grails.gsp.TagLib

@TagLib
class SystemReportTagLib implements TagLibrary {

    static namespace = "systemReport"


    SystemReportService systemReportService

    /**
     * using to get disciplinary judgments.
     * @attr disciplinaryReasonIds
     */
    def showStatic = { attrs, body ->
        Map map = systemReportService.getGeneralReportSettings()
        map.each { key, value ->
            attrs[key] = value
        }
        out << report.showStatic(attrs)
    }

}
