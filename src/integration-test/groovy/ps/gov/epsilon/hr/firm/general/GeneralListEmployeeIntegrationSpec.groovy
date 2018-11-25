package ps.gov.epsilon.hr.firm.general

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for GeneralListEmployee service
 */
class GeneralListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  GeneralListEmployee
        service_domain =  GeneralListEmployeeService
        entity_name = "generalListEmployee"
        required_properties = PCPUtils.getRequiredFields( GeneralListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}