package ps.gov.epsilon.hr.firm.loan

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for LoanNominatedEmployee service
 */
class LoanNominatedEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = LoanNominatedEmployee
        service_domain = LoanNominatedEmployeeService
        entity_name = "loanNominatedEmployee"
        required_properties = PCPUtils.getRequiredFields(LoanNominatedEmployee)
        filtered_parameters = ["employee.id"];
        autocomplete_property = "employee.id"
        primary_keys = ["encodedId"]
        exclude_methods = ["autocomplete"]
    }
}