package ps.gov.epsilon.aoc.correspondences.loan

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocLoanNoticeReplayListController {


    def listIncoming = {
        redirect(controller: 'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.LOAN_NOTICE_REPLAY_LIST.toString()])
    }
    def listOutgoing = {
        redirect(controller: 'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.LOAN_NOTICE_REPLAY_LIST.toString()])
    }

}
