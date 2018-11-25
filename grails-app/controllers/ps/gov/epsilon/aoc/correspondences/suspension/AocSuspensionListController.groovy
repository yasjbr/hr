package ps.gov.epsilon.aoc.correspondences.suspension

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocSuspensionListController {

    def listIncoming = {
        redirect(controller: 'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.SUSPENSION_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller: 'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.SUSPENSION_LIST.toString()])
    }
}
