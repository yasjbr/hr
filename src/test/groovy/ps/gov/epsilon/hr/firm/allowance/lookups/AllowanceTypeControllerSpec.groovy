package ps.gov.epsilon.hr.firm.allowance.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.pcore.v2.entity.lookups.RelationshipTypeService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for AllowanceType controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([AllowanceType])
@Build([AllowanceType])
@TestFor(AllowanceTypeController)
class AllowanceTypeControllerSpec extends CommonUnitSpec {

    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    RelationshipTypeService relationshipTypeService = mockService(RelationshipTypeService)

    def setupSpec() {
        domain_class = AllowanceType
        service_domain = AllowanceTypeService
        entity_name = "allowanceType"
        hashing_entity = "id"
        required_properties = PCPUtils.getRequiredFields(AllowanceType)
        filtered_parameters = ["descriptionInfo.localName"]
        autocomplete_property = "descriptionInfo.localName"
        primary_key_values = ["encodedId", "id"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()


        if (!serviceInstance.relationshipTypeService) {
            serviceInstance.relationshipTypeService = relationshipTypeService
            serviceInstance.relationshipTypeService.proxyFactoryService = proxyFactoryService
        }

    }

}