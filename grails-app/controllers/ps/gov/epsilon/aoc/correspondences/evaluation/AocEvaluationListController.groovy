package ps.gov.epsilon.aoc.correspondences.evaluation

import grails.converters.JSON
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocEvaluationListController {
    AocEvaluationListService aocEvaluationListService

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.EVALUATION_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.EVALUATION_LIST.toString()])
    }

    /**
     * represent the import evaluation data empty instance
     */
    def importEvaluationDataModal(){
        if (params.aocCorrespondenceListId) {
            AocCorrespondenceList aocCorrespondenceList= AocCorrespondenceList.read(params.long('aocCorrespondenceListId'))
            return [aocCorrespondenceList: aocCorrespondenceList, requestEntityName:message(code:'EnumCorrespondenceType.'+aocCorrespondenceList.correspondenceType)]
        } else {
            render ""
        }
    }

    /**
     * Import the evaluations data from excel file
     */
    def importEvaluationData() {
        String successMessage = message(code: 'aocEvaluationListRecord.importData.message')
        String failMessage = message(code: 'aocEvaluationListRecord.not.importData.message')
        if(params["excelFile"]){
            def file = request.getFile("excelFile")
            Map dataMap = aocEvaluationListService?.importEvaluationData(params, file)
            if (request.xhr) {
                Map json = [:]
                List<Map> errors = dataMap.errors
                json.success = !errors;
                def errorFormat = msg.errorList(data: (errors), isCustom: "true");
                json.message = json.success ? msg.success(label: successMessage) : errorFormat
                json.data = json.success ? dataMap.data : null
                json.errorList = !json.success ? errors : []
                render text: (json as JSON), contentType: "application/json"
            }
        }else {
            flash.message = msg.error(label: failMessage)
        }
    }
}
