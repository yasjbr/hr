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
 * unit test for ServiceListEmployeeNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ServiceListEmployeeNote])
@Build([ServiceListEmployeeNote])
@TestFor(ServiceListEmployeeNoteController)
class ServiceListEmployeeNoteControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ServiceListEmployeeNote
        service_domain = ServiceListEmployeeNoteService
        entity_name = "serviceListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields(ServiceListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}