package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for VacationExtensionListEmployee service
 */
class VacationExtensionListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  VacationExtensionListEmployee
        service_domain =  VacationExtensionListEmployeeService
        entity_name = "vacationExtensionListEmployee"
        required_properties = PCPUtils.getRequiredFields( VacationExtensionListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}