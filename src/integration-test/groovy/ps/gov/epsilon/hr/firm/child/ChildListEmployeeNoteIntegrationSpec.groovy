package ps.gov.epsilon.hr.firm.child

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ChildListEmployeeNote service
 */
class ChildListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ChildListEmployeeNote
        service_domain =  ChildListEmployeeNoteService
        entity_name = "childListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields( ChildListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}