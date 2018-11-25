package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([TrainingListEmployeeNote, ps.gov.epsilon.hr.firm.recruitment.TraineeListEmployee])
@Domain([TrainingListEmployeeNote])
@TestMixin([HibernateTestMixin])
class TrainingListEmployeeNoteDomainSpec extends ConstraintUnitSpec {

    void "test TrainingListEmployeeNote all constraints"() {
        when:
        List<Map> constraints = [
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.PASS],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "noteDate", value: null, testResult: TestResult.FAIL],
                [field: "noteDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: "orderNo", testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "traineeListEmployee", value: null, testResult: TestResult.FAIL],
                [field: "traineeListEmployee", value: ps.gov.epsilon.hr.firm.recruitment.TraineeListEmployee.build(), testResult: TestResult.PASS],
        ]
        then:
        validateObject(TrainingListEmployeeNote, constraints)
    }
}
