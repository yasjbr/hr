<%=packageName ? "package ${packageName}" : ''%>

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
 * unit test for ${className} controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([${className}])
@Build([${className}])
@TestFor(${className}Controller)
class ${className}ControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ${className}
        service_domain = ${className}Service
        entity_name = "${propertyName}"
        required_properties = PCPUtils.getRequiredFields(${className})
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}