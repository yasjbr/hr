package ps.gov.epsilon.hr.firm.lookups

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
 * unit test for MilitaryRankClassification controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([MilitaryRankClassification])
@Build([MilitaryRankClassification])
@TestFor(MilitaryRankClassificationController)
class MilitaryRankClassificationControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = MilitaryRankClassification
        service_domain = MilitaryRankClassificationService
        entity_name = "militaryRankClassification"
        required_properties = PCPUtils.getRequiredFields(MilitaryRankClassification)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        primary_key_values = ["encodedId", "id"]
        is_virtual_delete = true
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

}