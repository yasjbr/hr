package ps.gov.epsilon.hr.firm.loan

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for LoanListPerson service
 */
class LoanListPersonIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = LoanListPerson
        service_domain = LoanListPersonService
        entity_name = "loanListPerson"
        required_properties = PCPUtils.getRequiredFields(LoanListPerson)
        filtered_parameters = ["loanRequest.id"];
        autocomplete_property = "loanRequest.id"
        primary_keys = ["encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_methods = ["autocomplete","save"]
    }
}