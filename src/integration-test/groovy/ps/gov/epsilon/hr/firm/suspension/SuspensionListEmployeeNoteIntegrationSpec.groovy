package ps.gov.epsilon.hr.firm.suspension

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for SuspensionListEmployeeNote service
 */
class SuspensionListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = SuspensionListEmployeeNote
        service_domain = SuspensionListEmployeeNoteService
        entity_name = "suspensionListEmployeeNote"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(SuspensionListEmployeeNote)
        required_properties << "note"
        filtered_parameters = ["id"]
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["autocomplete"]
    }
}