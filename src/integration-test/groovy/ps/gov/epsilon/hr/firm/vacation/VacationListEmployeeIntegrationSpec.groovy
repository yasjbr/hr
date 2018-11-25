package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for VacationListEmployee service
 */
class VacationListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = VacationListEmployee
        service_domain = VacationListEmployeeService
        entity_name = "vacationListEmployee"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(VacationListEmployee)
        filtered_parameters = ["id"]
        exclude_methods = ["autocomplete"]
        primary_keys = ["id", 'encodedId']
    }
}