package ps.gov.epsilon.hr.firm.vacation

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([VacationListEmployee,ps.gov.epsilon.hr.firm.promotion.EmployeePromotion,ps.gov.epsilon.hr.firm.profile.EmploymentRecord,ps.gov.epsilon.hr.firm.vacation.VacationList,ps.gov.epsilon.hr.firm.vacation.VacationRequest])
@Domain([VacationListEmployee])
@TestMixin([HibernateTestMixin])
class VacationListEmployeeDomainSpec extends ConstraintUnitSpec {

    void "test VacationListEmployee all constraints"() {
        when:
        List<Map> constraints = [
                [field: "currentEmployeeMilitaryRank", value: null, testResult: TestResult.FAIL],
                [field: "currentEmployeeMilitaryRank", value: ps.gov.epsilon.hr.firm.promotion.EmployeePromotion.build(), testResult: TestResult.PASS],
                [field: "currentEmploymentRecord", value: null, testResult: TestResult.FAIL],
                [field: "currentEmploymentRecord", value: ps.gov.epsilon.hr.firm.profile.EmploymentRecord.build(), testResult: TestResult.PASS],
                [field: "recordStatus", value: null, testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "vacationList", value: null, testResult: TestResult.FAIL],
                [field: "vacationList", value: ps.gov.epsilon.hr.firm.vacation.VacationList.build(), testResult: TestResult.PASS],
                [field: "vacationListEmployeeNotes", value: null, testResult: TestResult.PASS],
                [field: "vacationRequest", value: null, testResult: TestResult.FAIL],
                [field: "vacationRequest", value: ps.gov.epsilon.hr.firm.vacation.VacationRequest.build(), testResult: TestResult.PASS],
        ]
        then:
        validateObject(VacationListEmployee,constraints)
    }
}
