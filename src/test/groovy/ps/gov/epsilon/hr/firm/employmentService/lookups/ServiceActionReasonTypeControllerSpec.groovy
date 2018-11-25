package ps.gov.epsilon.hr.firm.employmentService.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
/**
 * unit test for ServiceActionReasonType controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ServiceActionReasonType])
@Build([ServiceActionReasonType])
@TestFor(ServiceActionReasonTypeController)
class ServiceActionReasonTypeControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ServiceActionReasonType
        service_domain = ServiceActionReasonTypeService
        entity_name = "serviceActionReasonType"
        required_properties = PCPUtils.getRequiredFields(ServiceActionReasonType)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        primary_key_values = ["encodedId","id"]
        is_virtual_delete = false
    }

}