package ps.gov.epsilon.hr.firm.child

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ChildList service
 */
class ChildListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ChildList
        service_domain =  ChildListService
        entity_name = "childList"
        required_properties = PCPUtils.getRequiredFields( ChildList)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}