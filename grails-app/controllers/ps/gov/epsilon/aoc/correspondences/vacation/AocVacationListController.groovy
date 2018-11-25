package ps.gov.epsilon.aoc.correspondences.vacation

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocVacationListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.VACATION_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.VACATION_LIST.toString()])
    }
}
