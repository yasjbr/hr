package ps.gov.epsilon.aoc.correspondences.endOfService

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocEndOfServiceListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.END_OF_SERVICE_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.END_OF_SERVICE_LIST.toString()])
    }
}
