package ps.gov.epsilon.hr.firm.disciplinary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([EmployeeViolation,ps.gov.epsilon.hr.firm.promotion.EmployeePromotion,ps.gov.epsilon.hr.firm.profile.EmploymentRecord,ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason,ps.gov.epsilon.hr.firm.profile.Employee,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.profile.Employee,java.util.Map])
@Domain([EmployeeViolation])
@TestMixin([HibernateTestMixin])
class EmployeeViolationDomainSpec extends ConstraintUnitSpec {

    void "test EmployeeViolation all constraints"() {
        when:
        List<Map> constraints = [
                [field: "currentEmployeeMilitaryRank", value: null, testResult: TestResult.FAIL],
                [field: "currentEmployeeMilitaryRank", value: ps.gov.epsilon.hr.firm.promotion.EmployeePromotion.build(), testResult: TestResult.PASS],
                [field: "currentEmploymentRecord", value: null, testResult: TestResult.FAIL],
                [field: "currentEmploymentRecord", value: ps.gov.epsilon.hr.firm.profile.EmploymentRecord.build(), testResult: TestResult.PASS],
                [field: "disciplinaryReason", value: null, testResult: TestResult.FAIL],
                [field: "disciplinaryReason", value: ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason.build(), testResult: TestResult.PASS],
                [field: "employee", value: null, testResult: TestResult.FAIL],
                [field: "employee", value: ps.gov.epsilon.hr.firm.profile.Employee.build(), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "informer", value: null, testResult: TestResult.PASS],
                [field: "informer", value: ps.gov.epsilon.hr.firm.profile.Employee.build(), testResult: TestResult.PASS],
                [field: "joinedDisciplinaryEmployeeViolations", value: null, testResult: TestResult.PASS],
                [field: "locationId", value: null, testResult: TestResult.PASS],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "noticeDate", value: null, testResult: TestResult.FAIL],
                [field: "noticeDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "transientData", value: null, testResult: TestResult.PASS],
                [field: "transientData", value: java.util.Map.build(), testResult: TestResult.PASS],
                [field: "unstructuredLocation", value: null, testResult: TestResult.PASS],
                [field: "unstructuredLocation", value: " ", testResult: TestResult.FAIL],
                [field: "unstructuredLocation", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "unstructuredLocation", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "violationDate", value: null, testResult: TestResult.FAIL],
                [field: "violationDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "violationStatus", value: null, testResult: TestResult.FAIL],
        ]
        then:
        validateObject(EmployeeViolation,constraints)
    }
}
