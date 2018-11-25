package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for VacationStopListEmployee service
 */
class VacationStopListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  VacationStopListEmployee
        service_domain =  VacationStopListEmployeeService
        entity_name = "vacationStopListEmployee"
        required_properties = PCPUtils.getRequiredFields( VacationStopListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}