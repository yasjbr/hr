package ps.gov.epsilon.hr.firm.transfer

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
 * unit test for ExternalTransferListEmployeeNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ExternalTransferListEmployeeNote])
@Build([ExternalTransferListEmployeeNote])
@TestFor(ExternalTransferListEmployeeNoteController)
class ExternalTransferListEmployeeNoteControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ExternalTransferListEmployeeNote
        service_domain = ExternalTransferListEmployeeNoteService
        entity_name = "externalTransferListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields(ExternalTransferListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}