package ps.gov.epsilon.hr.firm.suspension

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for SuspensionExtensionList service
 */
class SuspensionExtensionListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  SuspensionExtensionList
        service_domain =  SuspensionExtensionListService
        entity_name = "suspensionExtensionList"
        required_properties = PCPUtils.getRequiredFields( SuspensionExtensionList)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}