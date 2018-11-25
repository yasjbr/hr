package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for RecruitmentListEmployeeNote service
 */
class RecruitmentListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = RecruitmentListEmployeeNote
        service_domain = RecruitmentListEmployeeNoteService
        entity_name = "recruitmentListEmployeeNote"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(RecruitmentListEmployeeNote)
        required_properties << "note"
        filtered_parameters = ["id"]
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["autocomplete"]

    }
}