package ps.gov.epsilon.hr.firm.child

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ChildRequest service
 */
class ChildRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ChildRequest
        service_domain =  ChildRequestService
        entity_name = "childRequest"
        required_properties = PCPUtils.getRequiredFields( ChildRequest)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}