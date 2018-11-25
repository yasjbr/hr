package ps.gov.epsilon.aoc.correspondences.disciplinary

import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType

class AocDisciplinaryListController {

    def listIncoming = {
        redirect(controller:'aocCorrespondenceList', action: 'list',
                params: [correspondenceType: EnumCorrespondenceType.DISCIPLINARY_LIST.toString()])
    }

    def listOutgoing = {
        redirect(controller:'aocCorrespondenceList', action: 'listOutgoing',
                params: [correspondenceType: EnumCorrespondenceType.DISCIPLINARY_LIST.toString()])
    }
}
