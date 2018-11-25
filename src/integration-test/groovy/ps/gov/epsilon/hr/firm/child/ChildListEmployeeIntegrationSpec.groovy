package ps.gov.epsilon.hr.firm.child

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ChildListEmployee service
 */
class ChildListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ChildListEmployee
        service_domain =  ChildListEmployeeService
        entity_name = "childListEmployee"
        required_properties = PCPUtils.getRequiredFields( ChildListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}