package ps.gov.epsilon.hr.firm.child

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
 * unit test for ChildListEmployeeNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ChildListEmployeeNote])
@Build([ChildListEmployeeNote])
@TestFor(ChildListEmployeeNoteController)
class ChildListEmployeeNoteControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ChildListEmployeeNote
        service_domain = ChildListEmployeeNoteService
        entity_name = "childListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields(ChildListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}