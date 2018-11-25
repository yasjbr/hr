package ps.gov.epsilon.aoc.correspondences.transfer

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocExternalTransferListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.EXTERNAL_TRANSFER_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.EXTERNAL_TRANSFER_LIST.toString()])
    }
}
