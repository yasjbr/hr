package ps.gov.epsilon.aoc.correspondences.violation

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocViolationListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.VIOLATION_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.VIOLATION_LIST.toString()])
    }
}
