package ps.gov.epsilon.hr.firm.militaryCard

import ps.gov.epsilon.hr.enums.militaryCard.v1.EnumIssuingCardReason
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants

/**
 *<h1>Purpose</h1>
 * To hold the employee request for military card due to many reasons, for example: lost, replacement and new employee
 * <h1>Usage</h1>
 * Used  as to represent the employee request for military card
 * **/

class MilitaryCardRequest extends Request{

    static auditable = true

    String arabicName
    String latinName
    //to define the reason for request a military card like new employee, replacement and lost
    EnumIssuingCardReason issuingReason
    // toDO the picture on the card should be an attachment on the request with type  personal image

    static constraints = {
        latinName(Constants.NAME_NULLABLE)
        arabicName(Constants.NAME)
        issuingReason nullable: false

        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }
}
