package ps.gov.epsilon.hr.firm.loan

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

@Integration
@Rollback
/**
 * integration test for LoanRequestRelatedPerson service
 */
class LoanRequestRelatedPersonIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = LoanRequestRelatedPerson
        service_domain = LoanRequestRelatedPersonService
        required_properties = PCPUtils.getRequiredFields(LoanRequestRelatedPerson)
        filtered_parameters = ["id"];
        autocomplete_property = "loanRequest.id"
        exclude_methods = ["list", "create", "autocomplete"]

        primary_keys = ["encodedId"]
        session_parameters = ["firmId": "firm.id", "firm.id": "firm.id"]
        once_save_properties = ["firm"]
    }


//    public LoanRequestRelatedPerson fillEntity(TestDataObject tableData = null) {
//
//        if (!tableData) {
//            tableData = new TestDataObject()
//            tableData.requiredProperties = required_properties
//            tableData.domain = domain_class
//            tableData.data = table_data?.data
//            tableData.objectName = entity_name
//            tableData.hasSecurity = has_security
//            tableData.isJoinTable = is_join_table
//        }
//        LoanRequestRelatedPerson instance
//        Map props = [:]
//        if (tableData?.disableSave) {
//            instance = tableData?.domain?.newInstance(props)
//        } else {
//            Map addedMap = [:]
//            if (tableData.hasSecurity) {
//                addedMap.put("springSecurityService", springSecurityService)
//            }
//            if (tableData.isJoinTable) {
//                addedMap.putAll(props)
//            }
//            instance = tableData?.domain?.buildWithoutValidation(addedMap)
//        }
//
//        //to allow get data when list is closed and request approved
//        instance.requestedPersonId = (counter+10)
//        instance.recordSource = EnumPersonSource.RECEIVED
//        instance.loanRequest = LoanRequest.build()
//        CorrespondenceListStatus currentStatus = CorrespondenceListStatus.build(correspondenceListStatus:EnumCorrespondenceListStatus.CLOSED)
//        LoanList loanList = LoanList.build(currentStatus: currentStatus)
//        LoanRequest loanRequest = LoanRequest.build(requestStatus:EnumRequestStatus.APPROVED )
//        LoanListPerson loanListPerson = LoanListPerson.build(loanList:loanList,loanRequest:loanRequest)
//
//        instance.loanRequest = loanRequest
//
//        return instance
//
//    }
}