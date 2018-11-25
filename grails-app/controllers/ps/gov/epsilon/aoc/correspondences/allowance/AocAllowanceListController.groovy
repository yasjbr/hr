package ps.gov.epsilon.aoc.correspondences.allowance

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocAllowanceListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.ALLOWANCE_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.ALLOWANCE_LIST.toString()])
    }
}
