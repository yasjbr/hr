package ps.gov.epsilon.aoc.correspondences.returnToService

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocReturnToServiceListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.RETURN_TO_SERVICE_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.RETURN_TO_SERVICE_LIST.toString()])
    }
}
