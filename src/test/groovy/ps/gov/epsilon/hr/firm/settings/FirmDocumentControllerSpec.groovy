package ps.gov.epsilon.hr.firm.settings

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import org.junit.Assume
import ps.gov.epsilon.hr.firm.Firm
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for FirmDocument controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([FirmDocument])
@Build([FirmDocument])
@TestFor(FirmDocumentController)
class FirmDocumentControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = FirmDocument
        service_domain = FirmDocumentService
        entity_name = "firmDocument"
        required_properties = PCPUtils.getRequiredFields(FirmDocument)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        primary_key_values = ["encodedId", "id"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]

    }
}