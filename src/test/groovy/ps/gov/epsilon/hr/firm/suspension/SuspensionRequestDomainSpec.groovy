package ps.gov.epsilon.hr.firm.suspension

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([SuspensionRequest,ps.gov.epsilon.hr.firm.promotion.EmployeePromotion,ps.gov.epsilon.hr.firm.profile.EmploymentRecord,ps.gov.epsilon.hr.firm.profile.EmploymentRecord,ps.gov.epsilon.hr.firm.profile.Employee,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.profile.Employee,ps.gov.epsilon.hr.firm.Department,ps.gov.epsilon.hr.firm.suspension.SuspensionListEmployee])
@Domain([SuspensionRequest])
@TestMixin([HibernateTestMixin])
class SuspensionRequestDomainSpec extends ConstraintUnitSpec {

    void "test SuspensionRequest all constraints"() {
        when:
        List<Map> constraints = [
                [field: "currentEmployeeMilitaryRank", value: null, testResult: TestResult.FAIL],
                [field: "currentEmployeeMilitaryRank", value: ps.gov.epsilon.hr.firm.promotion.EmployeePromotion.build(), testResult: TestResult.PASS],
                [field: "currentEmploymentRecord", value: null, testResult: TestResult.FAIL],
                [field: "currentEmploymentRecord", value: ps.gov.epsilon.hr.firm.profile.EmploymentRecord.build(), testResult: TestResult.PASS],
                [field: "currentRequesterEmploymentRecord", value: null, testResult: TestResult.PASS],
                [field: "currentRequesterEmploymentRecord", value: ps.gov.epsilon.hr.firm.profile.EmploymentRecord.build(), testResult: TestResult.PASS],
                [field: "employee", value: null, testResult: TestResult.FAIL],
                [field: "employee", value: ps.gov.epsilon.hr.firm.profile.Employee.build(), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "fromDate", value: null, testResult: TestResult.FAIL],
                [field: "fromDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "orderDate", value: null, testResult: TestResult.FAIL],
                [field: "orderDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "orderNo", value: "orderNoA1c_", testResult: TestResult.FAIL],
                [field: "orderNo", value: "23142", testResult: TestResult.PASS],
                [field: "parentRequestId", value: null, testResult: TestResult.PASS],
                [field: "periodInMonth", value: null, testResult: TestResult.FAIL],
                [field: "periodInMonth", value: -1, testResult: TestResult.FAIL],
                [field: "periodInMonth", value: 3, testResult: TestResult.PASS],
                [field: "periodInMonth", value: 14, testResult: TestResult.FAIL],
                [field: "periodInMonth", value: 10, testResult: TestResult.PASS],
                [field: "requestDate", value: null, testResult: TestResult.FAIL],
                [field: "requestDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "requestReason", value: null, testResult: TestResult.PASS],
                [field: "requestReason", value: " ", testResult: TestResult.FAIL],
                [field: "requestReason", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "requestReason", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "requestStatus", value: null, testResult: TestResult.FAIL],
                [field: "requestStatusNote", value: null, testResult: TestResult.PASS],
                [field: "requestStatusNote", value: " ", testResult: TestResult.FAIL],
                [field: "requestStatusNote", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "requestStatusNote", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "requestType", value: null, testResult: TestResult.FAIL],
                [field: "requester", value: null, testResult: TestResult.PASS],
                [field: "requester", value: ps.gov.epsilon.hr.firm.profile.Employee.build(), testResult: TestResult.PASS],
                [field: "requesterDepartment", value: null, testResult: TestResult.PASS],
                [field: "requesterDepartment", value: ps.gov.epsilon.hr.firm.Department.build(), testResult: TestResult.PASS],
                [field: "suspensionListEmployee", value: null, testResult: TestResult.PASS],
                [field: "suspensionListEmployee", value: ps.gov.epsilon.hr.firm.suspension.SuspensionListEmployee.build(), testResult: TestResult.PASS],
                [field: "suspensionType", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(SuspensionRequest,constraints)
    }
}
