package ps.gov.epsilon.hr.firm.vacation

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for VacationListEmployeeNote service
 */
class VacationListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = VacationListEmployeeNote
        service_domain = VacationListEmployeeNoteService
        entity_name = "vacationListEmployeeNote"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(VacationListEmployeeNote)
        required_properties << "note"
        filtered_parameters = ["id"]
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["autocomplete"]
    }
}