package ps.gov.epsilon.hr.firm.maritalStatus

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for MaritalStatusRequest service
 */
class MaritalStatusRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  MaritalStatusRequest
        service_domain =  MaritalStatusRequestService
        entity_name = "maritalStatusRequest"
        required_properties = PCPUtils.getRequiredFields( MaritalStatusRequest)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}