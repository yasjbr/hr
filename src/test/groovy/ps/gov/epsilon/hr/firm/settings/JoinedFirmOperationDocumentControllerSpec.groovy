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
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for JoinedFirmOperationDocument controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([JoinedFirmOperationDocument])
@Build([JoinedFirmOperationDocument])
@TestFor(JoinedFirmOperationDocumentController)
class JoinedFirmOperationDocumentControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = JoinedFirmOperationDocument
        service_domain = JoinedFirmOperationDocumentService
        entity_name = "joinedFirmOperationDocument"
        required_properties = PCPUtils.getRequiredFields(JoinedFirmOperationDocument)
        filtered_parameters = ["id"];
        autocomplete_property = "operation"
        primary_key_values = ["id", "encodedId", "operation"]
        exclude_actions = ['delete', 'save', 'update', 'filter', 'show', 'edit']

    }


}