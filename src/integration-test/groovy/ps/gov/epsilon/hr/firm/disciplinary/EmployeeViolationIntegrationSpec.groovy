package ps.gov.epsilon.hr.firm.disciplinary

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for EmployeeViolation service
 */
class EmployeeViolationIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  EmployeeViolation
        service_domain =  EmployeeViolationService
        entity_name = "employeeViolation"
        required_properties = PCPUtils.getRequiredFields( EmployeeViolation)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}