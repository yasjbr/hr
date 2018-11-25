package ps.gov.epsilon.hr.firm.settings

import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils

class JoinedFirmOperationDocumentTagLib {

    static namespace = "joinedFirmOperationDocumentTagLib"
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService

    def getOperationSelectElement = { attrs, body ->
        GrailsParameterMap operationParams = new GrailsParameterMap([max: Integer.MAX_VALUE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        def operationUsed = joinedFirmOperationDocumentService.searchWithoutDuplicate(operationParams)?.resultList?.operation
        out << el.select(valueMessagePrefix: 'EnumOperation', value: '', size: 8, name: "operation", from: (ps.gov.epsilon.hr.enums.v1.EnumOperation.values() - operationUsed ), class: "isRequired", label: "${message(code: 'joinedFirmOperationDocument.operation.label', default: 'operation')}")
    }

}
