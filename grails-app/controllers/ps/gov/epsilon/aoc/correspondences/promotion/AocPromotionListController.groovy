package ps.gov.epsilon.aoc.correspondences.promotion

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocPromotionListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.PROMOTION_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.PROMOTION_LIST.toString()])
    }
}
