package ps.gov.epsilon.hr.firm

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([Department,ps.gov.epsilon.hr.firm.Firm])
@Domain([Department])
@TestMixin([HibernateTestMixin])
class DepartmentDomainSpec extends ConstraintUnitSpec {

    void "test Department all constraints"() {
        when:
        List<Map> constraints = [
                [field: "contactInfos", value: null, testResult: TestResult.PASS],
                [field: "departmentOperationalTasks", value: null, testResult: TestResult.PASS],
                [field: "departmentType", value: null, testResult: TestResult.FAIL],
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "functionalParentDept", value: null, testResult: TestResult.PASS],
                [field: "governorateId", value: null, testResult: TestResult.PASS],
                [field: "locationId", value: null, testResult: TestResult.PASS],
                [field: "managerialParentDept", value: null, testResult: TestResult.PASS],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.PASS],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "unstructuredLocation", value: null, testResult: TestResult.PASS],
                [field: "unstructuredLocation", value: " ", testResult: TestResult.PASS],
                [field: "unstructuredLocation", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "unstructuredLocation", value: getLongString(20000), testResult: TestResult.PASS],
        ]
        then:
        validateObject(Department,constraints)
    }
}
