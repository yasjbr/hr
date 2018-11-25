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
 * unit test for FirmActiveModule controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([FirmActiveModule])
@Build([FirmActiveModule])
@TestFor(FirmActiveModuleController)
class FirmActiveModuleControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = FirmActiveModule
        service_domain = FirmActiveModuleService
        entity_name = "firmActiveModule"
        required_properties = PCPUtils.getRequiredFields(FirmActiveModule)
        filtered_parameters = ["id"];
        autocomplete_property = "systemModule"
        primary_key_values = ["encodedId", "id"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}