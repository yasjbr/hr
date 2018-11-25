package ps.gov.epsilon.hr.firm

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([JoinedDepartmentOperationalTasks,ps.gov.epsilon.hr.firm.Department,ps.gov.epsilon.hr.firm.lookups.OperationalTask])
@Domain([JoinedDepartmentOperationalTasks])
@TestMixin([HibernateTestMixin])
class JoinedDepartmentOperationalTasksDomainSpec extends ConstraintUnitSpec {

    void "test JoinedDepartmentOperationalTasks all constraints"() {
        when:
        List<Map> constraints = [
                [field: "department", value: null, testResult: TestResult.FAIL],
                [field: "department", value: ps.gov.epsilon.hr.firm.Department.build(), testResult: TestResult.PASS],
                [field: "operationalTask", value: null, testResult: TestResult.FAIL],
                [field: "operationalTask", value: ps.gov.epsilon.hr.firm.lookups.OperationalTask.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(JoinedDepartmentOperationalTasks,constraints)
    }
}
