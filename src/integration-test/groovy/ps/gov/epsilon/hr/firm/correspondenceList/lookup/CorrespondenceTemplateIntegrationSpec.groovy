package ps.gov.epsilon.hr.firm.correspondenceList.lookup

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for CorrespondenceTemplate service
 */
class CorrespondenceTemplateIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  CorrespondenceTemplate
        service_domain =  CorrespondenceTemplateService
        entity_name = "correspondenceTemplate"
        required_properties = PCPUtils.getRequiredFields( CorrespondenceTemplate)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}