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
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for FirmSetting controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([FirmSetting])
@Build([FirmSetting])
@TestFor(FirmSettingController)
class FirmSettingControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = FirmSetting
        service_domain = FirmSettingService
        entity_name = "firmSetting"
        required_properties = PCPUtils.getRequiredFields(FirmSetting)
        filtered_parameters = ["id"];
        autocomplete_property = "propertyName"
        primary_key_values = ["encodedId", "id"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}