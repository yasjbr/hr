package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for VacationStopListEmployeeNote service
 */
class VacationStopListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  VacationStopListEmployeeNote
        service_domain =  VacationStopListEmployeeNoteService
        entity_name = "vacationStopListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields( VacationStopListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}