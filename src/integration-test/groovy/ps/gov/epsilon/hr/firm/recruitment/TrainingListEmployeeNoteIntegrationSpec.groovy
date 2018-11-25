package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for TrainingListEmployeeNote service
 */
class TrainingListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = TrainingListEmployeeNote
        service_domain = TrainingListEmployeeNoteService
        entity_name = "trainingListEmployeeNote"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(TrainingListEmployeeNote)
        required_properties << "note"
        filtered_parameters = ["id"]
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["autocomplete"]
    }
}