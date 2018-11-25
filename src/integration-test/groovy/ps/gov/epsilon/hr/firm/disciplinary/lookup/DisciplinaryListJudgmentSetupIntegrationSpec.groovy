package ps.gov.epsilon.hr.firm.disciplinary.lookup

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for DisciplinaryListJudgmentSetup service
 */
class DisciplinaryListJudgmentSetupIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = DisciplinaryListJudgmentSetup
        service_domain = DisciplinaryListJudgmentSetupService
        entity_name = "disciplinaryListJudgmentSetup"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(DisciplinaryListJudgmentSetup)
        filtered_parameters = ["id"];
        autocomplete_property = "listNamePrefix"
        primary_keys = ["id", 'encodedId']
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]

    }
}