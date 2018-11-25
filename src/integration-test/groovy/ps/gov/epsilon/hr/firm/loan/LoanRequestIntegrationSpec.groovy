package ps.gov.epsilon.hr.firm.loan

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for LoanRequest service
 */
class LoanRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = LoanRequest
        service_domain = LoanRequestService
        entity_name = "loanRequest"
        required_properties = PCPUtils.getRequiredFields(LoanRequest)
        filtered_parameters = ["requestedJob.id"];
        autocomplete_property = "requestedJob.id"
        exclude_methods = ["list"]
        primary_keys = ["encodedId"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
    }
}