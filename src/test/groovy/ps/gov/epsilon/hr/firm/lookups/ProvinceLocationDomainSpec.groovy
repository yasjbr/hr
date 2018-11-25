package ps.gov.epsilon.hr.firm.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([ProvinceLocation,ps.gov.epsilon.hr.firm.lookups.Province])
@Domain([ProvinceLocation])
@TestMixin([HibernateTestMixin])
class ProvinceLocationDomainSpec extends ConstraintUnitSpec {

    void "test ProvinceLocation all constraints"() {
        when:
        List<Map> constraints = [
                [field: "locationId", value: null, testResult: TestResult.FAIL],
                [field: "locationId", value: 5, testResult: TestResult.PASS],
                [field: "locationId", value: "Number", testResult: TestResult.FAIL],
                [field: "locationId", value: "101", testResult: TestResult.FAIL],
                [field: "locationId", value: -1, testResult: TestResult.PASS],
                [field: "province", value: null, testResult: TestResult.FAIL],
                [field: "province", value: ps.gov.epsilon.hr.firm.lookups.Province.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(ProvinceLocation,constraints)
    }
}
