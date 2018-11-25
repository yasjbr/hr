package ps.gov.epsilon.hr.firm.employmentService

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
 * unit test for ServiceListEmployee controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ServiceListEmployee])
@Build([ServiceListEmployee])
@TestFor(ServiceListEmployeeController)
class ServiceListEmployeeControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ServiceListEmployee
        service_domain = ServiceListEmployeeService
        entity_name = "serviceListEmployee"
        required_properties = PCPUtils.getRequiredFields(ServiceListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

}