package ps.gov.epsilon.hr.firm.general

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
 * unit test for GeneralListEmployeeNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([GeneralListEmployeeNote])
@Build([GeneralListEmployeeNote])
@TestFor(GeneralListEmployeeNoteController)
class GeneralListEmployeeNoteControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = GeneralListEmployeeNote
        service_domain = GeneralListEmployeeNoteService
        entity_name = "generalListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields(GeneralListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}