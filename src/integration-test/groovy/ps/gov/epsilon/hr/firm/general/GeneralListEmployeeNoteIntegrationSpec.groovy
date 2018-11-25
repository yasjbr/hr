package ps.gov.epsilon.hr.firm.general

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for GeneralListEmployeeNote service
 */
class GeneralListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  GeneralListEmployeeNote
        service_domain =  GeneralListEmployeeNoteService
        entity_name = "generalListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields( GeneralListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}