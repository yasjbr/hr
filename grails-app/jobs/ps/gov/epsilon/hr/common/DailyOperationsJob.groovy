package ps.gov.epsilon.hr.common

import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.absence.AbsenceService
import ps.gov.epsilon.hr.firm.dispatch.DispatchListService
import ps.gov.epsilon.hr.firm.dispatch.DispatchRequestService
import ps.gov.epsilon.hr.firm.employmentService.ServiceListService
import ps.gov.epsilon.hr.firm.profile.EmployeeInternalAssignationService
import ps.gov.epsilon.hr.firm.promotion.PromotionListService
import ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycleService
import ps.gov.epsilon.hr.firm.suspension.SuspensionListService
import ps.gov.epsilon.hr.firm.suspension.SuspensionRequestService
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferRequestService
import ps.gov.epsilon.hr.firm.vacation.VacationListService
import ps.gov.epsilon.hr.firm.vacation.VacationRequestService

import java.time.ZonedDateTime

class DailyOperationsJob {

    DispatchListService dispatchListService
    DispatchRequestService dispatchRequestService
    SuspensionListService suspensionListService
    ServiceListService serviceListService
    VacationListService vacationListService
    PromotionListService promotionListService
    RecruitmentCycleService recruitmentCycleService
    VacationRequestService vacationRequestService
    SuspensionRequestService suspensionRequestService
    EmployeeInternalAssignationService employeeInternalAssignationService
    AbsenceService absenceService
    ExternalTransferRequestService externalTransferRequestService

    static triggers = {
        cron   name:'DailyCronTrigger', startDelay: 10000, cronExpression: '1 0 0 * * ?' // execute daily on 12:00:01 am
        // TODO: Added for testing purposes, to be removed after finish tests
        simple name: 'DailySimpleTrigger', startDelay: 300000, repeatInterval: 600000
    }


    def concurrent = false

    def execute() {
        log.info("DailyOperationsJob is executed on ${(new Date()).format('dd/MM/yyyy HH:mm:ss')}")
        List<Firm> firmList = Firm.list()
        firmList.each { Firm firm ->

            try {
                log.debug("starting job for dispatchListService")
                dispatchListService.updateEmployeeStatusToDispatch(firm)
                log.debug("finished job for dispatchListService")
            } catch (Exception ex) {
                log.error("Failed to run job on dispatchListService", ex)
            }

            try {
                log.debug("starting job for suspensionListService")
                suspensionListService.updateEmployeeStatusToSuspended(firm)
                log.debug("finished job for suspensionListService")
            } catch (Exception ex) {
                log.error("Failed to run job on suspensionListService", ex)
            }

            try {
                log.debug("starting job for serviceListService: EndOfService")
                serviceListService.updateEmployeeStatusToEndOfService(firm)
                log.debug("finished job for serviceListService: EndOfService")
            } catch (Exception ex) {
                log.error("Failed to run job on serviceListService", ex)
            }


            try {
                log.debug("starting job for externalTransferRequestService")
                externalTransferRequestService.updateEmployeeStatusToTransferred(firm)
                log.debug("finished job for externalTransferRequestService")
            } catch (Exception ex) {
                log.error("Failed to run job on externalTransferRequestService", ex)
            }

            try {
                log.debug("starting job for serviceListService: ReturnToService")
                serviceListService.updateEmployeeStatusToReturnToService(firm)
                log.debug("finished job for serviceListService: ReturnToService")
            } catch (Exception ex) {
                log.error("Failed to run job on serviceListService", ex)
            }

            try {
                log.debug("starting job for vacationListService")
                vacationListService.updateEmployeeStatusToVacation(firm)
                log.debug("finished job for vacationListService")
            } catch (Exception ex) {
                log.error("Failed to run job on vacationListService", ex)
            }

            try {
                log.debug("starting job for promotionListService")
                promotionListService.updateEmployeePromotionRecord(firm)
                log.debug("finished job for promotionListService")
            } catch (Exception ex) {
                log.error("Failed to run job on promotionListService", ex)
            }


            try {
                log.debug("starting job for recruitment cycle notification")
                recruitmentCycleService.createNotification(firm, ZonedDateTime.now().minusDays(5), ZonedDateTime.now())
                log.debug("finished job for recruitment cycle notification")
            } catch (Exception ex) {
                log.error("Failed to run job on recruitment cycle notification", ex)
            }

            try {
                log.debug("starting job for vacation notification")
                vacationRequestService.createNotification(firm, ZonedDateTime.now(), ZonedDateTime.now())
                log.debug("finished job for vacation notification")
            } catch (Exception ex) {
                log.error("Failed to run job on vacation notification", ex)
            }

            try {
                log.debug("starting job for suspension notification")
                suspensionRequestService.createNotification(ZonedDateTime.now().minusDays(5), ZonedDateTime.now())
                log.debug("finished job for suspension notification")
            } catch (Exception ex) {
                log.error("Failed to run job on suspension notification", ex)
            }

            try {
                log.debug("starting job for dispatch notification")
                dispatchRequestService.createDispatchRequestNotification(firm, ZonedDateTime.now(), ZonedDateTime.now().plusDays(3))
                log.debug("finished job for dispatch notification")
            } catch (Exception ex) {
                log.error("Failed to run job on dispatch notification", ex)
            }

            try {
                log.debug("starting job for EmployeeInternalAssignation notification")
                employeeInternalAssignationService.createEmployeeInternalAssignationNotification(firm, ZonedDateTime.now(), ZonedDateTime.now().plusDays(3))
                log.debug("finished job for EmployeeInternalAssignation notification")
            } catch (Exception ex) {
                log.error("Failed to run job on EmployeeInternalAssignation notification", ex)
            }

            try {
                log.debug("starting job for absence notification")
                absenceService.createAbsenceNotification(firm)
                log.debug("finished job for absence notification")
            } catch (Exception ex) {
                log.error("Failed to run job on absence notification", ex)
            }
        }
    }
}