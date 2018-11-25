package ps.gov.epsilon.hr.firm.disciplinary.lookup

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import ps.police.common.enums.v1.GeneralStatus
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for DisciplinaryReason controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([DisciplinaryReason])
@Build([DisciplinaryReason])
@TestFor(DisciplinaryReasonController)
class DisciplinaryReasonControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = DisciplinaryReason
        service_domain = DisciplinaryReasonService
        entity_name = "disciplinaryReason"
        required_properties = PCPUtils.getRequiredFields(DisciplinaryReason)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_actions = ['delete']
        primary_key_values = ["id", "encodedId"]
        is_virtual_delete=true
    }

}