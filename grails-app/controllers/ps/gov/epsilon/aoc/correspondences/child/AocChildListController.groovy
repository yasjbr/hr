package ps.gov.epsilon.aoc.correspondences.child

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocChildListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.CHILD_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.CHILD_LIST.toString()])
    }
}
