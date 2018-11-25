package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for VacationExtensionListEmployeeNote service
 */
class VacationExtensionListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  VacationExtensionListEmployeeNote
        service_domain =  VacationExtensionListEmployeeNoteService
        entity_name = "vacationExtensionListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields( VacationExtensionListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}