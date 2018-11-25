package ps.gov.epsilon.hr.firm.loan

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for LoanListPersonNote service
 */
class LoanListPersonNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = LoanListPersonNote
        service_domain = LoanListPersonNoteService
        entity_name = "loanListPersonNote"
        List requiredProperties = PCPUtils.getRequiredFields(LoanListPersonNote)
        requiredProperties << "orderNo"
        required_properties = requiredProperties
        filtered_parameters = ["orderNo"];
        autocomplete_property = "orderNo"
        exclude_methods = ["autocomplete"]
        primary_keys = ["encodedId"]
    }
}