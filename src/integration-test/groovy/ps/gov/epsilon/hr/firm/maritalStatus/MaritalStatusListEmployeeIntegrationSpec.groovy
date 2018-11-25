package ps.gov.epsilon.hr.firm.maritalStatus

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for MaritalStatusListEmployee service
 */
class MaritalStatusListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  MaritalStatusListEmployee
        service_domain =  MaritalStatusListEmployeeService
        entity_name = "maritalStatusListEmployee"
        required_properties = PCPUtils.getRequiredFields( MaritalStatusListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}