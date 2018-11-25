package ps.gov.epsilon.hr.firm.vacation.lookup

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for VacationType service
 */
class VacationTypeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = VacationType
        service_domain = VacationTypeService
        entity_name = "vacationType"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(VacationType)
        filtered_parameters = ["id"];
        autocomplete_property = "descriptionInfo.localName"
        primary_keys = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
        is_encrypted_delete = false
    }
}