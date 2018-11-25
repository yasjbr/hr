package ps.gov.epsilon.hr.firm.suspension

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for SuspensionExtensionListEmployee service
 */
class SuspensionExtensionListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  SuspensionExtensionListEmployee
        service_domain =  SuspensionExtensionListEmployeeService
        entity_name = "suspensionExtensionListEmployee"
        required_properties = PCPUtils.getRequiredFields( SuspensionExtensionListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}