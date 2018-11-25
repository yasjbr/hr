package ps.gov.epsilon.aoc.correspondences.dispatch

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocDispatchListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.DISPATCH_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.DISPATCH_LIST.toString()])
    }
}
