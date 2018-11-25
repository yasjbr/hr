package ps.gov.epsilon.hr.firm.maritalStatus

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for MaritalStatusList service
 */
class MaritalStatusListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  MaritalStatusList
        service_domain =  MaritalStatusListService
        entity_name = "maritalStatusList"
        required_properties = PCPUtils.getRequiredFields( MaritalStatusList)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}