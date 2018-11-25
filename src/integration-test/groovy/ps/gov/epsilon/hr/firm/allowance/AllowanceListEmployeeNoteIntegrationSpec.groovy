package ps.gov.epsilon.hr.firm.allowance

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for AllowanceListEmployeeNote service
 */
class AllowanceListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = AllowanceListEmployeeNote
        service_domain = AllowanceListEmployeeNoteService
        entity_name = "allowanceListEmployeeNote"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(AllowanceListEmployeeNote)
        required_properties << "note"
        filtered_parameters = ["id"]
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["autocomplete"]

    }

}