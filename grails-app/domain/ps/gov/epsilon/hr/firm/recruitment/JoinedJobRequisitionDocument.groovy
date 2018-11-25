package ps.gov.epsilon.hr.firm.recruitment

import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument

/**
 * <h1>Purpose</h1>
 * To hold the Job Requisition and the Join Firm Operation Document many-to-many relation
 * **/

class JoinedJobRequisitionDocument {

    String id

    JoinedFirmOperationDocument firmOperationDocument

    JobRequisition jobRequisition


    //used to override the isMandatory in  JoinFirmOperationDocument
    Boolean isMandatory

    static constraints = {
        firmOperationDocument nullable: false,widget:"autocomplete"
        jobRequisition nullable: false,widget:"autocomplete"
        isMandatory nullable: false
    }
}
