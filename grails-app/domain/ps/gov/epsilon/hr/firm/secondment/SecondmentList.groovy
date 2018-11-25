package ps.gov.epsilon.hr.firm.secondment

import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList

/**
 *<h1>Purpose</h1>
 * To hold the Secondment List which contains many records of secondment nominated employee
 * <h1>Usage</h1>
 * Used as to represents the Secondment List related to secondment notice
 * **/

class SecondmentList extends CorrespondenceList{

    static belongsTo = [secondmentNotice:SecondmentNotice]

    static constraints = {
    }
}
