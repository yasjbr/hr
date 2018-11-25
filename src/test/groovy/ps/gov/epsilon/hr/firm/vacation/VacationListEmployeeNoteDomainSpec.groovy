package ps.gov.epsilon.hr.firm.vacation

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([VacationListEmployeeNote,ps.gov.epsilon.hr.firm.vacation.VacationListEmployee])
@Domain([VacationListEmployeeNote])
@TestMixin([HibernateTestMixin])
class VacationListEmployeeNoteDomainSpec extends ConstraintUnitSpec {

    void "test VacationListEmployeeNote all constraints"() {
        when:
        List<Map> constraints = [
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: "note", testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.PASS],
                [field: "noteDate", value: null, testResult: TestResult.FAIL],
                [field: "noteDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: "orderNo", testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "vacationListEmployee", value: null, testResult: TestResult.FAIL],
                [field: "vacationListEmployee", value: ps.gov.epsilon.hr.firm.vacation.VacationListEmployee.build(), testResult: TestResult.PASS],
        ]
        then:
        validateObject(VacationListEmployeeNote,constraints)
    }
}
