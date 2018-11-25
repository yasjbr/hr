package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ApplicantInspectionResultListEmployeeNote service
 */
class ApplicantInspectionResultListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ApplicantInspectionResultListEmployeeNote
        service_domain =  ApplicantInspectionResultListEmployeeNoteService
        entity_name = "applicantInspectionResultListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields( ApplicantInspectionResultListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}