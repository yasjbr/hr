package ps.gov.epsilon.hr.firm.disciplinary.lookup

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
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.lookups.UnitOfMeasurementService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for DisciplinaryJudgment controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([DisciplinaryJudgment])
@Build([DisciplinaryJudgment])
@TestFor(DisciplinaryJudgmentController)
class DisciplinaryJudgmentControllerSpec extends CommonUnitSpec {

    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    UnitOfMeasurementService unitOfMeasurementService = mockService(UnitOfMeasurementService)

    def setupSpec() {
        domain_class = DisciplinaryJudgment
        service_domain = DisciplinaryJudgmentService
        entity_name = "disciplinaryJudgment"
        required_properties = PCPUtils.getRequiredFields(DisciplinaryJudgment)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_actions = ['delete']
        primary_key_values = ["id", "encodedId"]
        is_virtual_delete=true
    }


    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if (!serviceInstance.unitOfMeasurementService) {
            serviceInstance.unitOfMeasurementService = unitOfMeasurementService
            serviceInstance.unitOfMeasurementService.proxyFactoryService = proxyFactoryService
        }
    }



}