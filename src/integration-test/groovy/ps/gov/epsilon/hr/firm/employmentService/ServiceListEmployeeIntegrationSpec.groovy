package ps.gov.epsilon.hr.firm.employmentService

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ServiceListEmployee service
 */
class ServiceListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ServiceListEmployee
        service_domain =  ServiceListEmployeeService
        entity_name = "serviceListEmployee"
        required_properties = PCPUtils.getRequiredFields( ServiceListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}