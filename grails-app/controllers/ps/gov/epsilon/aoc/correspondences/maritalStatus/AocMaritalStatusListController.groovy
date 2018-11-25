package ps.gov.epsilon.aoc.correspondences.maritalStatus

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocMaritalStatusListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.MARITAL_STATUS_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.MARITAL_STATUS_LIST.toString()])
    }
}
