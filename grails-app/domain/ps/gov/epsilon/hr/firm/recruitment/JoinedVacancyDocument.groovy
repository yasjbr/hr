package ps.gov.epsilon.hr.firm.recruitment

import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument

//todo we need to discuss the ability of restructure the vacancy and the job requisition to be
// todo extends from parent class called abstract requisition that contains all intersected feilds
// todo this will affect the join domains
// todo 1) ps.gov.epsilon.hr.firm.recruitment.JoinedVacancyDocument
// todo 2) ps.gov.epsilon.hr.firm.recruitment.JoinedJobRequisitionDocument
// todo to be merged into one domain with referrence to the new join relation that the abstract  requisition


/**
 * <h1>Purpose</h1>
 * To hold the Vacancy and the Joined Firm Operation Document many-to-many relation
 * **/

class JoinedVacancyDocument {

    String id

    JoinedFirmOperationDocument firmOperationDocument

    Vacancy vacancy

    //used to override the isMandatory in  JoinFirmOperationDocument
    Boolean isMandatory

    static constraints = {
        firmOperationDocument nullable: false,widget:"autocomplete"
        vacancy nullable: false,widget:"autocomplete"
        isMandatory nullable: false
    }
}
