package ps.gov.epsilon.hr.firm.suspension

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for SuspensionExtensionListEmployeeNote service
 */
class SuspensionExtensionListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  SuspensionExtensionListEmployeeNote
        service_domain =  SuspensionExtensionListEmployeeNoteService
        entity_name = "suspensionExtensionListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields( SuspensionExtensionListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}