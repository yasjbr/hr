package ps.gov.epsilon.aoc.correspondences.absence

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocReturnFromAbsenceListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.RETURN_FROM_ABSENCE_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.RETURN_FROM_ABSENCE_LIST.toString()])
    }
}
