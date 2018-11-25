package ps.gov.epsilon.hr.firm.suspension

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
 * unit test for SuspensionExtensionListEmployeeNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([SuspensionExtensionListEmployeeNote])
@Build([SuspensionExtensionListEmployeeNote])
@TestFor(SuspensionExtensionListEmployeeNoteController)
class SuspensionExtensionListEmployeeNoteControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = SuspensionExtensionListEmployeeNote
        service_domain = SuspensionExtensionListEmployeeNoteService
        entity_name = "suspensionExtensionListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields(SuspensionExtensionListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}