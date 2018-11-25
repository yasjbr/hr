package ps.gov.epsilon.hr.firm.loan

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for LoanNominatedEmployeeNote service
 */
class LoanNominatedEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = LoanNominatedEmployeeNote
        service_domain = LoanNominatedEmployeeNoteService
        entity_name = "loanNominatedEmployeeNote"
        List requiredProperties = PCPUtils.getRequiredFields(LoanNominatedEmployeeNote)
        requiredProperties << "orderNo"
        required_properties = requiredProperties
        filtered_parameters = ["orderNo"];
        autocomplete_property = "orderNo"
        exclude_methods = ["autocomplete"]
        primary_keys = ["encodedId"]
    }
}