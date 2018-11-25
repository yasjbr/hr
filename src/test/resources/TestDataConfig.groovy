import ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason
import ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchListType
import ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumDepartmentType
import ps.gov.epsilon.hr.enums.v1.EnumInspectionResult
import ps.gov.epsilon.hr.enums.v1.EnumInspectionResultRate
import ps.gov.epsilon.hr.enums.v1.EnumInterviewStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.gov.epsilon.hr.enums.v1.EnumSystemModule
import ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus

import ps.police.common.enums.v1.GeneralStatus
import java.time.ZonedDateTime


testDataConfig {


    sampleData {

        'ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo' {
            def counter = 1
            localName = { -> "localName${counter++}" }
        }

        'ps.police.common.domains.v1.TrackingInfo' {
            status = GeneralStatus.ACTIVE
            dateCreatedUTC = ZonedDateTime.now()
            lastUpdatedUTC = ZonedDateTime.now()
            createdBy = "createdBy"
            lastUpdatedBy = "lastUpdatedBy"
            ipAddress = "localhost"
            sourceApplication = "EPHR"
        }

        'ps.gov.epsilon.hr.firm.Firm' {
            Long counter = 1L
            coreOrganizationId = { -> counter++ }
            code = { -> "firm${counter++}" }
            name = "PCP"
        }

        'ps.gov.epsilon.hr.firm.lookups.InspectionCategory' {
            Long counter = 1L
            orderId = { -> counter++ }
            isRequiredByFirmPolicy = true
            hasResultRate = true
            hasMark = true
        }

        'ps.gov.epsilon.hr.firm.lookups.Inspection' {
            Long counter = 1L
            orderId = { -> counter++ }
        }

        'ps.gov.epsilon.hr.firm.lookups.JobTitle' {
            Long counter = 1L
            code = { -> "firm${counter++}" }
        }

        'ps.gov.epsilon.hr.firm.Department' {
            departmentType = EnumDepartmentType.DEPARTMENT
        }


        'ps.gov.epsilon.hr.firm.DepartmentContactInfo' {
            Long counter = 1L
            contactMethodId = { -> counter++ }
            contactTypeId = { -> counter++ }
            value = "weso"
        }

        'ps.gov.epsilon.hr.firm.settings.FirmSupportContactInfo' {
            name = "firm"
        }

        'ps.gov.epsilon.hr.firm.recruitment.ApplicantStatusHistory' {
            applicantStatus = EnumApplicantStatus.ACCEPTED
            fromDate = ZonedDateTime.now()
            toDate = ZonedDateTime.now().plusMonths(2)
        }

        'ps.gov.epsilon.hr.firm.recruitment.Interview' {
            interviewStatus = EnumInterviewStatus.OPEN
            Long counter = 978L
            locationId = { -> counter++ }
            fromDate = ZonedDateTime.now()
            toDate = ZonedDateTime.now().plusMonths(2)
            description = "interview"
            unstructuredLocation = "locationDescriptionForInterview"
            note = "note"
        }

        'ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycle' {
            name = "recruitmentCycle"
            startDate = ZonedDateTime.now()
            endDate = ZonedDateTime.now().plusMonths(3)
        }

        'ps.gov.epsilon.hr.firm.recruitment.RecruitmentCyclePhase' {
            requisitionAnnouncementStatus = EnumRequisitionAnnouncementStatus.NEW
            fromDate = ZonedDateTime.now()
            toDate = ZonedDateTime.now().plusMonths(2)
        }


        'ps.gov.epsilon.hr.firm.recruitment.RecruitmentListEmployee' {
            recordStatus = EnumListRecordStatus.APPROVED
        }

        'ps.gov.epsilon.hr.firm.recruitment.Vacancy' {
            numberOfPositions = 5L
            vacancyStatus = EnumVacancyStatus.NEW
            fulfillToDate = ZonedDateTime.now()
            fulfillFromDate = ZonedDateTime.now().minusMonths(3)
        }


        'ps.gov.epsilon.hr.firm.recruitment.VacancyAdvertisements' {
            title = "advertisement"
            postingDate = ZonedDateTime.now()
            closingDate = ZonedDateTime.now().plusMonths(6)
        }

        'ps.gov.epsilon.hr.firm.recruitment.Applicant' {
            Long counter = 1750L
            personId = { -> counter++ }
            age = 25d
            locationId = 11L
            height = 50D
            weight = 50D
            fatherProfessionType = 2L
            motherProfessionType = 3L
            previousProfessionType = 4L
            relativesInMilitaryFirms = "Ahmad"
            relativesInCivilianFirm = "Mustafa"
            nominationParty = "PCPFirm"
            personName = "weso"

        }

        'ps.gov.epsilon.hr.firm.lookups.InspectionCategory' {
            Long counter = 1L
            orderId = { -> counter++ }
            isRequiredByFirmPolicy = true
        }

        'ps.gov.epsilon.hr.firm.recruitment.ApplicantInspectionCategoryResult' {
            inspectionResult = EnumInspectionResult.NEW
            inspectionResultRate = EnumInspectionResultRate.EXCELLENT
        }

        'ps.gov.epsilon.hr.firm.Department' {
            departmentType = EnumDepartmentType.DEPARTMENT
        }

        'ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument' {
            operation = EnumOperation.CHANGE_MARITAL_STATUS
            isMandatory = true

        }

        'ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus' {
            correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
        }

        'ps.gov.epsilon.hr.firm.profile.Employee' {
            Long counter = 1750L
            personId = { -> counter++ }
            employmentDate = ZonedDateTime.now()
            joinDate = ZonedDateTime.now().plusMonths(1)
            employmentNumber = 142343
        }

        'ps.gov.epsilon.hr.firm.profile.EmploymentRecord' {
            fromDate = ZonedDateTime.now().plusMonths(5)
        }

        'ps.gov.epsilon.hr.firm.promotion.EmployeePromotion' {
            actualDueDate = ZonedDateTime.now().plusMonths(10)
            dueDate = ZonedDateTime.now().plusMonths(10)
            managerialOrderNumber = "00112233"
        }

        'ps.gov.epsilon.hr.firm.lookups.MilitaryRank' {
            short counter = 1
            orderNo = { -> counter++ }

        }

        'ps.gov.epsilon.hr.firm.lookups.JoinedJobEducationDegree' {
            short counter = 1
            educationDegreeId = { -> counter++ }
        }
        'ps.gov.epsilon.hr.firm.lookups.JoinedJobEducationMajor' {
            short counter = 1
            educationMajorId = { -> counter++ }
        }

        'ps.gov.epsilon.hr.firm.lookups.Job' {
            code = "code1"
            note = "note"
        }


        'ps.gov.epsilon.hr.firm.settings.FirmActiveModule' {
            systemModule = EnumSystemModule.TRAINING
        }


        'ps.gov.epsilon.hr.firm.recruitment.TrainingListEmployeeNote' {
            noteDate = ZonedDateTime.now()
            note = "note"
            orderNo = "1"
        }
        'ps.gov.epsilon.hr.firm.recruitment.RecruitmentListEmployeeNote' {
            noteDate = ZonedDateTime.now()
            note = "note"
            orderNo = "1"
        }

        'ps.gov.epsilon.hr.common.domains.v1.ListNote' {
            noteDate = ZonedDateTime.now()
            note = "note"
            orderNo = "1"
        }

        'ps.gov.epsilon.hr.firm.allowance.AllowanceRequest' {
            requestType = EnumRequestType.ALLOWANCE_REQUEST
        }

        'ps.gov.epsilon.hr.firm.dispatch.DispatchRequest' {
            long counter = 1L;
            requestType = EnumRequestType.DISPATCH
            organizationId = { -> counter++ }
            educationMajorId = { -> counter++ }
            locationId = { -> counter++ }
            requestDate = ZonedDateTime.now()
            dispatchType = EnumDispatchType.OTHERS
            nextVerificationDate = ZonedDateTime.now().plusYears(1)
            fromDate = ZonedDateTime.now()
            toDate = ZonedDateTime.now().plusMonths(12)


        }

        'ps.gov.epsilon.hr.firm.absence.Absence' {
            absenceReason = EnumAbsenceReason.MEDICAL
            fromDate = ZonedDateTime.now()
            noticeDate = ZonedDateTime.now()
        }


        'ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRecordJudgment' {
            int counter = 1
            value = { -> "value${counter++}" }
        }

        "ps.gov.epsilon.hr.firm.recruitment.TraineeList" {
            name = "traineeList"
            trainingLocationId = 1L
            fromDate = ZonedDateTime.now()
            toDate = ZonedDateTime.now().plusMonths(4)
        }
        "ps.gov.epsilon.hr.firm.dispatch.DispatchList" {
            name = "DispatchList"
            dispatchListType = EnumDispatchListType.DISPATCH
        }

        "ps.gov.epsilon.hr.firm.request.BordersSecurityCoordination" {
            fromDate = ZonedDateTime.now()
            toDate = ZonedDateTime.now().plusMonths(2)
        }
        "ps.gov.epsilon.hr.firm.vacation.VacationExtensionRequest" {
            fromDate = ZonedDateTime.now()
            toDate = ZonedDateTime.now().plusDays(5)
        }
        "ps.gov.epsilon.hr.firm.vacation.VacationRequest" {
            fromDate = ZonedDateTime.now()
            toDate = ZonedDateTime.now().plusDays(5)
            currentBalance = 10

        }

        'ps.gov.epsilon.hr.firm.allowance.AllowanceListEmployeeNote' {
            noteDate = ZonedDateTime.now()
            note = "note"
            orderNo = "1"
        }

        'ps.gov.epsilon.hr.firm.transfer.ExternalReceivedTransferredPerson' {
            Long counter = 1750L
            personId = { -> counter++ }
        }

        'ps.gov.epsilon.hr.firm.suspension.SuspensionListEmployeeNote' {
            noteDate = ZonedDateTime.now()
            note = "note"
            orderNo = "12432"
        }


        'ps.gov.epsilon.hr.firm.suspension.SuspensionExtensionRequest' {

            fromDate = ZonedDateTime.now()
            toDate = ZonedDateTime.now().plusYears(1L)
        }

        'ps.gov.epsilon.hr.firm.loan.EndorseOrder' {
            Long counter = 1L
            orderNo = { -> "abc${counter++}" }
        }

        'ps.gov.epsilon.hr.firm.loan.LoanListPersonNote' {
            Long counter = 1L
            orderNo = { -> "abc${counter++}" }
        }

        'ps.gov.epsilon.hr.firm.loan.LoanNominatedEmployeeNote' {
            Long counter = 1L
            orderNo = { -> "abc${counter++}" }
        }

        'ps.gov.epsilon.hr.firm.loan.LoanNotice' {
            Long counter = 1L
            requesterOrganizationId = { -> counter++ }
        }

        'ps.gov.epsilon.hr.firm.loan.LoanList' {
            Long counter = 1L
            code = { -> "code${counter++}" }
        }

        'ps.gov.epsilon.hr.firm.loan.LoanNoticeReplayList' {
            Long counter = 1L
            code = { -> "code${counter++}" }
        }
    }


}