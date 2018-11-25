package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.context.ServletContextHolder
import grails.web.servlet.mvc.GrailsParameterMap
import org.apache.catalina.connector.Request
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.ObjectError
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.enums.v1.EnumRecruitmentCycleDepartmentStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.DepartmentService
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.JobCategory
import ps.gov.epsilon.hr.firm.lookups.JobCategoryService
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.Notification
import ps.police.notifications.NotificationParams
import ps.police.notifications.NotificationService
import ps.police.notifications.NotificationTerm
import ps.police.notifications.NotificationType
import ps.police.notifications.enums.UserTerm

import javax.servlet.http.HttpServletRequest
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create a recruitment cycle and manage its life cycle
 * <h1>Usage</h1>
 * - used to create a recruitment cycle
 * - invite departments to use it
 * - send notification when the phase is changed
 * - change the cycle phase
 * <h1>Restriction</h1>
 * restriction on change phase
 * @see MessageSource
 * @see FormatService
 * */

@Transactional
class RecruitmentCycleService {

    MessageSource messageSource
    def formatService
    DepartmentService departmentService
    ApplicantService applicantService
    def sessionFactory
    NotificationService notificationService
    RequestService requestService
    JobCategoryService jobCategoryService

    /**
     * this closure is used to return the current phase, which will be used in show/hide columns
     */
    public static getCurrentPhase = { cService, RecruitmentCycle rec, object, params ->
        if (rec?.currentRecruitmentCyclePhase) {
            return rec?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus.toString()
        } else {
            return ""
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "name", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "currentRecruitmentCyclePhase.fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "currentRecruitmentCyclePhase.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "currentRecruitmentCyclePhase.requisitionAnnouncementStatus", messagePrefix: "EnumRequisitionAnnouncementStatus", type: "enum", source: 'domain'],
            [sort: false, search: true, hidden: true, name: "currentPhase", type: getCurrentPhase, source: 'domain']
    ]
    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        if (column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }

        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        List<String> ids = params.listString('ids[]')
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }
        String status = params["status"]

        String name = params["name"]

        String description = params["description"]


        Set joinedRecruitmentCycleDepartmentIds = params.listString("joinedRecruitmentCycleDepartment.id")

        ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus requisitionAnnouncementStatus = params["requisitionAnnouncementStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.valueOf(params["requisitionAnnouncementStatus"]) : null

        ZonedDateTime startDate = PCPUtils.parseZonedDateTime(params['startDate'])

        ZonedDateTime endDate = PCPUtils.parseZonedDateTime(params['endDate'])

        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])

        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])

        ZonedDateTime cyclePeriodDate = PCPUtils.parseZonedDateTime(params['cyclePeriodDate'])

        List<Map<String, String>> orderBy = params.list("orderBy")

        String departmentId = params["departmentId"]

        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])


        return RecruitmentCycle.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("description", sSearch)
                    ilike("name", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (description) {
                    ilike("description", "%${description}%")
                }

                if (joinedRecruitmentCycleDepartmentIds) {
                    joinedRecruitmentCycleDepartment {
                        inList("id", joinedRecruitmentCycleDepartmentIds)
                    }
                }

                //to filter RC in JR creation based on invitation for a specific department
                if (departmentId) {
                    joinedRecruitmentCycleDepartment {
                        department {
                            eq("id", departmentId)
                        }
                    }
                }
                if (name) {
                    ilike("name", "%${name}%")
                }
                if (requisitionAnnouncementStatus) {
                    currentRecruitmentCyclePhase {
                        eq("requisitionAnnouncementStatus", requisitionAnnouncementStatus)
                    }
                }
                if (startDate) {
                    eq("startDate", startDate)
                }
                if (endDate) {
                    eq("endDate", endDate)
                }
                if (fromDate) {
                    currentRecruitmentCyclePhase {
                        eq("fromDate", fromDate)
                    }
                }
                if (toDate) {
                    currentRecruitmentCyclePhase {
                        eq("toDate", toDate)
                    }
                }

                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }

                if (cyclePeriodDate) {
                    currentRecruitmentCyclePhase {
                        and {
                            le("fromDate", cyclePeriodDate)
                            or {
                                ge("toDate", cyclePeriodDate)
                                eq("toDate", PCPUtils.getDEFAULT_ZONED_DATE_TIME())
                            }

                        }

                    }
                }

                if (fromFromDate) {
                    ge("startDate", fromFromDate)
                }
                if (toFromDate) {
                    le("startDate", toFromDate)
                }

                if (fromToDate) {
                    ge("endDate", fromToDate)
                }
                if (toToDate) {
                    le("endDate", toToDate)
                }


                eq("firm.id", PCPSessionUtils.getValue("firmId"))
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {

                switch (columnName) {
                    case "currentRecruitmentCyclePhase.fromDate":
                        currentRecruitmentCyclePhase {
                            order("fromDate", dir)

                        }
                        break;
                    case "currentRecruitmentCyclePhase.toDate":
                        currentRecruitmentCyclePhase {
                            order("toDate", dir)

                        }
                        break;
                    case "currentRecruitmentCyclePhase.requisitionAnnouncementStatus":
                        currentRecruitmentCyclePhase {
                            order("requisitionAnnouncementStatus", dir)

                        }
                        break;
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break;
                    default:
                        order(columnName, dir)
                        break;
                }
            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return RecruitmentCycle.
     */
    RecruitmentCycle save(GrailsParameterMap params) {
        RecruitmentCycle recruitmentCycleInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            recruitmentCycleInstance = RecruitmentCycle.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (recruitmentCycleInstance.version > version) {
                    recruitmentCycleInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('recruitmentCycle.label', null, 'recruitmentCycle', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this recruitmentCycle while you were editing")
                    return recruitmentCycleInstance
                }
            }
            if (!recruitmentCycleInstance) {
                recruitmentCycleInstance = new RecruitmentCycle()
                recruitmentCycleInstance.errors.reject('default.not.found.message', [messageSource.getMessage('recruitmentCycle.label', null, 'recruitmentCycle', LocaleContextHolder.getLocale())] as Object[], "This recruitmentCycle with ${params.id} not found")
                return recruitmentCycleInstance
            }
        } else {
            recruitmentCycleInstance = new RecruitmentCycle()
            recruitmentCycleInstance.startDate = ZonedDateTime.now()
            //TODO: read from session
            Firm firm = Firm.findAllByName("DCO")[0]
            if (firm) {
                //set the firm to params
                recruitmentCycleInstance.firm = firm
            }
        }
        try {

            recruitmentCycleInstance.properties = params;
            List<JobRequisition> jobRequisitionList = null

            //manage the departments to add and delete the departments which will be invited to use this recruitment cycle
            if (params.manageDepartment) {


                if (recruitmentCycleInstance?.id) {
                    JoinedRecruitmentCycleJobCategory.executeUpdate('delete from JoinedRecruitmentCycleJobCategory a where a.recruitmentCycle.id =:recruitmentCycleId', ['recruitmentCycleId': recruitmentCycleInstance?.id])
                }

                //return the list of departments which match the passed params.department.id
                List departmentIds = params.listString("department.id")

                //return the list of jobCategory which match the passed jobCategory
                List jobCategoriesIds = params.listString("jobCategory.id")

                //return the list of departments
                List<Department> departmentList
                if (departmentIds) {
                    departmentList = Department.findAllByIdInList(departmentIds)
                }

                //return the list of jobCategory
                List<JobCategory> jobCategoriesList = null
                if (jobCategoriesIds) {
                    jobCategoriesList = JobCategory.findAllByIdInList(jobCategoriesIds)
                }

                //** case1: if recruitment cycle in OPEN/NEW phase, we can manage departments
                if (recruitmentCycleInstance?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.OPEN, EnumRequisitionAnnouncementStatus.NEW]) {

                    List<Department> joinedRecruitmentCycleDepartmentIdList = JoinedRecruitmentCycleDepartment.findAllByRecruitmentCycle(recruitmentCycleInstance)?.department

                    //step2: loop on component department list, and check 3 cases: delete/new/update
                    joinedRecruitmentCycleDepartmentIdList?.each { Department department ->
                        //step1: if the id from join department table is exist in the component department list:
                        // remove it from the list, since its already saved in join table in db
                        if (department in departmentList) {
                            departmentList.remove(department)
                        } else {
                            //step2: if the id from join department table is not exist in the component department list:
                            // reject the save, since the user deleted it.
                            if (recruitmentCycleInstance?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus == EnumRequisitionAnnouncementStatus.OPEN) {
                                recruitmentCycleInstance.errors.reject('recruitmentCycle.departments.delete.notAllowed.label', [messageSource.getMessage('recruitmentCycle.label', null, 'recruitmentCycle', LocaleContextHolder.getLocale())] as Object[], "You cant add or remove departments from recruitmentCycle with ${params.id} in this phase")
                                throw new Exception("Delete department in open phase")
                            } else {
                                //deleted the department from join table.
                                JoinedRecruitmentCycleDepartment.executeUpdate('delete from JoinedRecruitmentCycleDepartment a where a.department.id =:departmentId', ['departmentId': department?.id])
                            }
                        }
                    }





                    JoinedRecruitmentCycleDepartment joinedRecruitmentCycleDepartment
                    JoinedRecruitmentCycleJobCategory joinedRecruitmentCycleJobCategory

                    //get the notification for selected recruitment cycle
                    Notification notification = Notification.findByObjectSourceIdAndObjectSourceReference(recruitmentCycleInstance?.id, RecruitmentCycle.getName())

                    //step3: save the remaining list : which are added newly
                    departmentList.each { Department department ->
                        //3- create new JoinedRecruitmentCycleDepartment and then add to recruitmentCycleInstance
                        joinedRecruitmentCycleDepartment = new JoinedRecruitmentCycleDepartment(recruitmentCycle: recruitmentCycleInstance, department: department, recruitmentCycleDepartmentStatus: EnumRecruitmentCycleDepartmentStatus.NEW);
                        recruitmentCycleInstance.addToJoinedRecruitmentCycleDepartment(joinedRecruitmentCycleDepartment)

                        //assign notification for added department when recruitment cycle status is OPEN & notification is exist
                        if (notification) {
                            notification.addToNotificationTerms(new NotificationTerm(key: UserTerm.DEPARTMENT.value(), value: department?.id))
                        }
                    }

                    //get list of employment records
                    List<EmploymentRecord> employmentRecordList = EmploymentRecord.executeQuery("From EmploymentRecord employmentRecord where " +
                            "employmentRecord.department.id in (:departmentIds) and " +
                            "employmentRecord.jobTitle.jobCategory.id in (:jobCategoriesIds)  ", [departmentIds   : departmentIds,
                                                                                                  jobCategoriesIds: jobCategoriesIds])

                    NotificationTerm notificationTerm = null
                    String jobTitleId = null
                    jobCategoriesList.each { JobCategory jobCategory ->
                        joinedRecruitmentCycleJobCategory = new JoinedRecruitmentCycleJobCategory(recruitmentCycle: recruitmentCycleInstance, jobCategory: jobCategory);
                        recruitmentCycleInstance.addToJoinedRecruitmentCycleJobCategory(joinedRecruitmentCycleJobCategory)

                        //assign notification for added jobCategory
                        if (notification) {
                            jobTitleId = employmentRecordList?.find {
                                it?.jobTitle?.jobCategory?.id == jobCategory?.id
                            }?.jobTitle?.id

                            //add new term if term not exist.
                            notificationTerm = NotificationTerm.findByNotificationAndKeyAndValue(notification, UserTerm.JOB_TITLE.value(), jobTitleId)
                            if (!notificationTerm) {
                                notification.addToNotificationTerms(new NotificationTerm(key: UserTerm.JOB_TITLE.value(), value: jobTitleId))
                            }
                        }
                    }

                    //incase: there is a notification for recruitment cycle
                    if (notification) {
                        try {
                            notification.save(flush: true, failOnError: true)
                        } catch (Exception ex) {
                            recruitmentCycleInstance.errors.reject('recruitmentCycle.notification.error.assignDepartment.message', [messageSource.getMessage('recruitmentCycle.label', null, 'recruitmentCycle', LocaleContextHolder.getLocale())] as Object[], "failed to assign new added departments to notification")
                            throw new Exception("failed to assign new added departments to notification")
                        }
                    }

                } else {//** case 2: not allowed to add/delete due to current phase
                    recruitmentCycleInstance.errors.reject('recruitmentCycle.department.error', [messageSource.getMessage('recruitmentCycle.label', null, 'recruitmentCycle', LocaleContextHolder.getLocale())] as Object[], "You cant add or remove departments from recruitmentCycle with ${params.id} in this phase")
                    throw new Exception("Delete department is not allowed")
                }
            }

            //save the phase:
            if (!recruitmentCycleInstance?.currentRecruitmentCyclePhase && !params.id) {//**** create new Phase
                //1- in case of add new recruitment cycle: the status will be NEW
                RecruitmentCyclePhase recruitmentCyclePhase = new RecruitmentCyclePhase(recruitmentCycle: recruitmentCycleInstance, fromDate: ZonedDateTime.now(), toDate: PCPUtils.getDEFAULT_ZONED_DATE_TIME(), requisitionAnnouncementStatus: EnumRequisitionAnnouncementStatus.NEW)
                recruitmentCycleInstance.addToRecruitmentCyclePhases(recruitmentCyclePhase)
//
                //2- validate the recruitment cycle instance and the recruitment cycle phase instance:
                if (!recruitmentCyclePhase?.validate()) {
                    if (recruitmentCyclePhase?.hasErrors()) {
                        recruitmentCyclePhase?.errors.allErrors.each { ObjectError error ->
                            recruitmentCycleInstance?.errors.reject(messageSource.getMessage(error.code, error.arguments, error.defaultMessage, LocaleContextHolder.getLocale()))
                        }
                    }
                    throw new Exception("Not valid data to save")
                }

                //3- save the recruitmentCycleInstance with (has many) phase history instance
                recruitmentCycleInstance.save(flush: true, failOnError: true);

                if (recruitmentCycleInstance.id && recruitmentCyclePhase) {
                    //4- save the current phase
                    recruitmentCycleInstance.currentRecruitmentCyclePhase = recruitmentCyclePhase
                    recruitmentCycleInstance.save(flush: true, failOnError: true)
                }
            } else if (params.nextPhase) {
                //**** change to next phase - (this param is passed from controller in case of change phase only):
                /*
                 * here we are applying the constrains on recruitment cycle next phase:-
                 */
                if (recruitmentCycleInstance?.currentRecruitmentCyclePhase) {

                    // get next phase name
                    EnumRequisitionAnnouncementStatus nextPhaseName = recruitmentCycleInstance.getNextPhase()

                    // 1. OPEN -> REVIEWED : at least one job requisition is used the recruitment cycle instance
                    if (nextPhaseName == EnumRequisitionAnnouncementStatus.REVIEWED) {
                        if (JobRequisition.countByRecruitmentCycle(recruitmentCycleInstance) == 0) {
                            recruitmentCycleInstance.errors.reject('recruitmentCycle.reviewed.error');
                            throw new Exception("recruitmentCyclePhase error")
                        }
                    }

                    if (nextPhaseName == EnumRequisitionAnnouncementStatus.VACANCY) {//requisitionStatus
                        if (JobRequisition.countByRecruitmentCycleAndRequisitionStatusInList(recruitmentCycleInstance, [EnumRequestStatus.CREATED, EnumRequestStatus.IN_PROGRESS]) > 0) {
                            JobRequisition.findByRecruitmentCycleAndRequisitionStatusInList(recruitmentCycleInstance, [EnumRequestStatus.CREATED, EnumRequestStatus.IN_PROGRESS]).each { JobRequisition jobRequisition ->
                                jobRequisition.requisitionStatus = EnumRequestStatus.REJECTED
                                jobRequisition.numberOfApprovedPositions = 0;
                                jobRequisition.save(flush: true, failOnError: true);
                            }
                        }
                        //get list of jobRequisition
                        jobRequisitionList = JobRequisition.findAllByRecruitmentCycle(recruitmentCycleInstance)
                        GrailsParameterMap notificationParams = null
                        jobRequisitionList?.each { JobRequisition jobRequisition ->
                            notificationParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                            //create notification for each jobRequisition
                            try {

                                //fill notification params and save notification
                                notificationParams["objectSourceId"] = jobRequisition?.id
                                notificationParams.objectSourceReference = JobRequisition.getName()
                                notificationParams.title = "${messageSource.getMessage("jobRequisition.label", [] as Object[], new Locale("ar"))}"

                                if (jobRequisition.requisitionStatus == EnumRequestStatus.APPROVED) {
                                    notificationParams.text = "${messageSource.getMessage("jobRequisition.notification.approveRequest.message", ["${jobRequisition?.id}", "${jobRequisition?.numberOfApprovedPositions}"] as Object[], new Locale("ar"))}"
                                } else {
                                    notificationParams.text = "${messageSource.getMessage("jobRequisition.notification.rejectRequest.message", ["${jobRequisition?.id}"] as Object[], new Locale("ar"))}"
                                }

                                notificationParams.notificationDate = ZonedDateTime.now()
                                notificationParams["notificationType"] = NotificationType.findByTopic("myNotification")

                                Map<String, Map<Integer, String>> notificationTermsMap = [:]
                                Map<Integer, String> notificationKeys = [:]
                                Map<Integer, String> notificationValues = [:]

                                //set firm
                                notificationKeys.put(new Integer(1), UserTerm.FIRM.value())
                                notificationValues.put(new Integer(1), "${jobRequisition?.firm?.id}")

                                //set department
                                notificationKeys.put(new Integer(2), UserTerm.DEPARTMENT.value())
                                notificationValues.put(new Integer(2), "${jobRequisition?.requestedForDepartment?.id}")


                                notificationTermsMap.put("key", notificationKeys)
                                notificationTermsMap.put("value", notificationValues)
                                notificationParams["notificationTerms"] = notificationTermsMap

                                //save notification
                                notificationService.save(notificationParams)
                            } catch (Exception ex) {
                                ex.printStackTrace()
                            }

                        }
                    }

                    // 2. VACANCY -> ADVERT : at least one vacancy is used the recruitment cycle instance
                    if (nextPhaseName == EnumRequisitionAnnouncementStatus.ADVERT) {
                        if (Vacancy.countByRecruitmentCycle(recruitmentCycleInstance) == 0) {
                            recruitmentCycleInstance.errors.reject('recruitmentCycle.advert.error');
                            throw new Exception("recruitmentCycle.advert.error")
                        }
                    }

                    // 3. ADVERT -> INTERVIEW : at least one applicant is used the recruitment cycle instance
                    if (nextPhaseName == EnumRequisitionAnnouncementStatus.INTERVIEW) {
                        if (Applicant.countByRecruitmentCycle(recruitmentCycleInstance) == 0) {
                            recruitmentCycleInstance.errors.reject('recruitmentCycle.interview.error');
                            throw new Exception("recruitmentCycle.advert.error")
                        }
                    }

                    // 3. TRAINING -> CLOSE : no applicant (with under training status) should be there.
                    // criteria: select from applicant where applicant is active, use the recruitment cycle instance, applicant status is under training
                    if (nextPhaseName == EnumRequisitionAnnouncementStatus.CLOSED) {
                        //create new params of this case criteria
                        GrailsParameterMap applicantParams = new GrailsParameterMap(["recruitmentCycle.id": recruitmentCycleInstance.id, "applicantCurrentStatusValue": EnumApplicantStatus.UNDER_TRAINING], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                        //search about applicant
                        PagedResultList applicantList = applicantService.search(applicantParams)
                        //check if there are at least one applicant, then reject the close phase
                        if (applicantList.size() > 0) {
                            recruitmentCycleInstance.errors.reject('recruitmentCycle.close.error');
                            throw new Exception("recruitmentCycle.close.error")
                        } else {
                            //the requirements from QA,
                            // to change the invited department state on joined table
                            // condition: (departments in NEW state and  they do not send there requests)

                            List<JoinedRecruitmentCycleDepartment> joinedDepartmentList = JoinedRecruitmentCycleDepartment.findAllByRecruitmentCycleAndRecruitmentCycleDepartmentStatus(recruitmentCycleInstance, EnumRecruitmentCycleDepartmentStatus.NEW)
                            joinedDepartmentList.each { JoinedRecruitmentCycleDepartment joinedDepartment ->
                                joinedDepartment.recruitmentCycleDepartmentStatus = EnumRecruitmentCycleDepartmentStatus.CLOSED_BY_PERIOD
                                joinedDepartment.save(flush: true, failOnError: true)
                            }
                        }
                    }

                    //create new RecruitmentCyclePhase, then assign to current phase in recruitment cycle instance :

                    //1- change the old toDate:
                    recruitmentCycleInstance?.currentRecruitmentCyclePhase?.toDate = ZonedDateTime.now()
//PCPUtils.parseZonedDateTime(params['fromDate'])

                    //2- create next phase instance:
                    RecruitmentCyclePhase recruitmentCyclePhase = new RecruitmentCyclePhase(recruitmentCycle: recruitmentCycleInstance, fromDate: params.fromDate, toDate: PCPUtils.parseZonedDateTime(params['toDate']) ?: PCPUtils.getDEFAULT_ZONED_DATE_TIME(), requisitionAnnouncementStatus: recruitmentCycleInstance.getNextPhase())

                    //3- add the join table to cycle instance
                    recruitmentCycleInstance.addToRecruitmentCyclePhases(recruitmentCyclePhase)

                    //4- validate the recruitment cycle instance
                    if (!recruitmentCycleInstance.validate()) {
                        if (recruitmentCycleInstance.hasErrors()) {
                            recruitmentCycleInstance.errors.addAllErrors(recruitmentCyclePhase.errors)
                        }
                        throw new Exception("recruitmentCyclePhase error")
                    }

                    //4- save the recruitmentCycleInstance with (has many) phase history instance
                    recruitmentCycleInstance.save(flush: true, failOnError: true);

                    if (recruitmentCycleInstance?.id && recruitmentCyclePhase?.id) {
                        recruitmentCycleInstance.currentRecruitmentCyclePhase = recruitmentCyclePhase
                        recruitmentCycleInstance.save(flush: true, failOnError: true)
                    }

                } else {
                    recruitmentCycleInstance.errors.reject('default.not.found.message', [messageSource.getMessage('recruitmentCycle.label', null, 'recruitmentCycle', LocaleContextHolder.getLocale())] as Object[], "This recruitmentCycle with ${params.id} not found")
                    return recruitmentCycleInstance
                }

            } else {//normal edit
                recruitmentCycleInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            recruitmentCycleInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return recruitmentCycleInstance
    }

    /**
     * to delete model entry and its phases
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, isEncrypted = false) {
        try {
            def id
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                id = HashHelper.decode(deleteBean?.ids[0])
            } else {
                id = deleteBean?.ids[0]
            }

            if (id) {
                RecruitmentCycle recruitmentCycle = RecruitmentCycle.get(id)
                //  delete recruitmentCycle which current status is NEW
                if (recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.NEW]) {
                    if (recruitmentCycle && recruitmentCycle?.trackingInfo?.status != GeneralStatus.DELETED) {
                        recruitmentCycle?.trackingInfo.status = GeneralStatus.DELETED
                        recruitmentCycle.save(flush: true)
                        deleteBean.status = true
                    }
                } else {
                    deleteBean.status = false
                    deleteBean.responseMessage << messageSource.getMessage('recruitmentCycle.deleteErrorMessage.label')
                }
            }
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return RecruitmentCycle.
     */
    @Transactional(readOnly = true)
    RecruitmentCycle getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }

        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            //search for the recruitmentCycle instance using the passed params (in case of edit/show):
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * to auto complete model entry.
     * @param GrailsParameterMap params the search map.
     * @return JSON.
     */
    @Transactional(readOnly = true)
    JSON autoComplete(GrailsParameterMap params) {
        List<Map> dataList = []
        String idProperty = params["idProperty"] ?: "id"
        String nameProperty = params["nameProperty"] ?: "name"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = this.search(params)
            dataList = PCPUtils.toMapList(resultList, nameProperty, idProperty, autoCompleteReturnedInfo)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return dataList as JSON
    }

    /**
     * this method to check what is the current phase and return the map of:
     *      1- recruitmentCycle instance
     *      2- expected next phase as String
     *      3- toDate as Long,
     *      4- errorType as String
     * @param params , boolean isEncrypted
     * @return map of result
     */
    Map getNextPhase(GrailsParameterMap params) {
        RecruitmentCycle recruitmentCycle = getInstance(params)
        Map map = [:]
        if (RecruitmentCycle) {
            if (recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.CLOSED]) {
                map.put("errorType", "notAllowed")
            } else {
                EnumRequisitionAnnouncementStatus nextPhaseName = recruitmentCycle.getNextPhase()

                //return the zoneDateTime as formatted date value to be used in comparision jquery
                def toDateValue = PCPUtils.parseDate(recruitmentCycle?.currentRecruitmentCyclePhase?.toDate as String, 'yyyy-MM-dd')
                Long formattedToDate = toDateValue ? toDateValue.getTime() : -1L

                //return the zoneDateTime as formatted date value to be used in comparision jquery
                def fromDateValue = PCPUtils.parseDate(recruitmentCycle?.currentRecruitmentCyclePhase?.fromDate as String, 'yyyy-MM-dd')
                Long formattedFromDate = fromDateValue ? fromDateValue.getTime() : -1L

                // 1. NEW -> OPEN : send warning if no departments were invited to system:
                if (nextPhaseName == EnumRequisitionAnnouncementStatus.OPEN) {
                    if (JoinedRecruitmentCycleDepartment.countByRecruitmentCycle(recruitmentCycle) == 0) {
                        map.put("warningMessage", messageSource.getMessage('recruitmentCycle.open.warning.message', null, 'recruitmentCycle', LocaleContextHolder.getLocale()))
                    }
                }

                // 2. REVIEWED -> VACANCY : send warning if job requisitions are included and not in approved state
                //    the action used here, to move status into rejected
                if (nextPhaseName == EnumRequisitionAnnouncementStatus.VACANCY) {//requisitionStatus
                    if (JobRequisition.countByRecruitmentCycleAndRequisitionStatusInList(recruitmentCycle, [EnumRequestStatus.CREATED, EnumRequestStatus.IN_PROGRESS]) > 0) {
                        map.put("warningMessage", messageSource.getMessage('recruitmentCycle.vacancy.warning.message', null, 'recruitmentCycle', LocaleContextHolder.getLocale()))
                    }
                }
                map.put("recruitmentCycle", recruitmentCycle)
                map.put("nextPhaseName", nextPhaseName)
                map.put("formattedToDate", formattedToDate)
                map.put("formattedFromDate", formattedFromDate)
                map.put("errorType", "success")
            }
        } else {
            map.put("errorType", "notFound")
        }
        return map
    }

    /**
     * manage the departments data to be used in tabs
     * @param params
     * @return
     */
    Map manageDepartmentData(GrailsParameterMap params) {
        //1- get the recruitment cycle instance to be used in multi list value
        RecruitmentCycle recruitmentCycle = getInstance(params)
        Map map = [:]

        if (RecruitmentCycle) {
            if (recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.NEW, EnumRequisitionAnnouncementStatus.OPEN]) {
                //2- remove the params id since params will be used to search departments
                params.remove("id")
                params.remove("encodedId")
                //3- department types should be GOVERNEROTE/DEPARTMENT
                params.departmentTypeList = [ps.gov.epsilon.hr.enums.v1.EnumDepartmentType.GOVERNEROTE, ps.gov.epsilon.hr.enums.v1.EnumDepartmentType.DEPARTMENT]
                //4- get all departments list

                params.max = Integer.MAX_VALUE
                List<Department> departmentsList = departmentService.search(params)
                List<JobCategory> JobCategoriesList = jobCategoryService.search(params)

                //5- passed the recruitmentCycle instance and department list to be used in view
                map.put("tabEntityName", "recruitmentCycle")
                map.put("recruitmentCycle", recruitmentCycle)
                map.put("departmentsList", departmentsList)
                map.put("JobCategoriesList", JobCategoriesList)
                map.put("errorType", "success")

            } else {
                map.put("errorType", "notAllowed")
            }
        } else {
            map.put("errorType", "notFound")
        }
        return map
    }

    /**
     * Add recruitment cycle for each job requisition
     * add related department to RC
     * @param GrailsParameterMap params
     * @return boolean
     */
    Boolean addRecruitmentCycleToJobRequisition(GrailsParameterMap params) {
        List checkedJobRequisitionIdsList = params.list("check_jobRequisitionTableToChoose")
        //retrieve the selected rows
        RecruitmentCycle recruitmentCycle = RecruitmentCycle.load(params["recruitmentCycleId"])
        //load the recruitment cycle instance
        JobRequisition jobRequisition
        Boolean isAddedStatus = true
        try {
            if (checkedJobRequisitionIdsList) {
                checkedJobRequisitionIdsList.each { def id ->
                    jobRequisition = JobRequisition.load(id)
                    // check if the job requisition related department was not invited in the recruitment cycle,
                    //   then add it with status : تم ملئ الشواغر
                    //   (if size is zero, add the department to RC with status)
                    if (JoinedRecruitmentCycleDepartment?.countByRecruitmentCycleAndDepartment(recruitmentCycle, jobRequisition.requestedForDepartment) < 1) {
                        JoinedRecruitmentCycleDepartment joinedRecruitmentCycleDepartment = new JoinedRecruitmentCycleDepartment(recruitmentCycle: recruitmentCycle, department: jobRequisition.requestedForDepartment, recruitmentCycleDepartmentStatus: EnumRecruitmentCycleDepartmentStatus.CLOSED_WITH_REPLAY).save(flush: true, failOnError: true)
                    } else {
                        //else, check the already invited departments in the RC - if status is NEW:
                        // change the department status in RC to be CLOSED_WITH_REPLAY
                        JoinedRecruitmentCycleDepartment joinedDepartment = JoinedRecruitmentCycleDepartment.findByRecruitmentCycleAndDepartmentAndRecruitmentCycleDepartmentStatus(recruitmentCycle, jobRequisition.requestedForDepartment, EnumRecruitmentCycleDepartmentStatus.NEW)
                        joinedDepartment?.recruitmentCycleDepartmentStatus = EnumRecruitmentCycleDepartmentStatus.CLOSED_WITH_REPLAY
                        joinedDepartment?.save(flush: true, failOnError: true)
                    }
                    jobRequisition?.recruitmentCycle = recruitmentCycle
                    jobRequisition?.save(flush: true, failOnError: true)
                }
            } else {
                isAddedStatus = false
            }
        } catch (Exception ex) {
            isAddedStatus = false
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            jobRequisition?.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return isAddedStatus;
    }

    /**
     * Convert paged result list to map depends on DOMAINS_COLUMNS.
     * @param def resultList may be PagedResultList or PagedList.
     * @param GrailsParameterMap params the search map
     * @param List < String >  DOMAIN_COLUMNS the list of model column names.
     * @return Map.
     * @see PagedResultList.
     * @see PagedList.
     */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS = null) {
        if (!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    /**
     * get list of  scheduled recruitment cycle in phase: OPEN
     * @params from date.
     * @params to date.
     * @return list of recruitmentCycle.
     */
    private List getListOfRecruitmentCycle(Firm firm, ZonedDateTime fromDate, ZonedDateTime toDate) {

        final session = sessionFactory.currentSession
        def list = []

        //query to get all recruitmentCycle where phase status is open.
        String query = "from recruitment_cycle rc, " +
                "recruitment_cycle_phase rcp, " +
                "joined_recruitment_cycle_department jrcd, " +
                "joined_recruitment_cycle_job_category jrcg " +
                " where  rc.status= '${GeneralStatus.ACTIVE}' " +
                " and rc.firm_id=:firmId " +
                " and rc.current_recruitment_cycle_phase_id =rcp.id " +
                " and rcp.from_date_datetime >= :fromDate " +
                " and rcp.from_date_datetime <= :toDate " +
                " and rcp.requisition_announcement_status= '${EnumRequisitionAnnouncementStatus.OPEN}'" +
                " and rcp.status= '${GeneralStatus.ACTIVE}'" +
                " and rc.id=jrcd.recruitment_cycle_id " +
                " and jrcd.status='${GeneralStatus.ACTIVE}' " +
                " and jrcg.status='${GeneralStatus.ACTIVE}' " +
                " and jrcd.recruitment_cycle_department_status= '${EnumRecruitmentCycleDepartmentStatus.NEW}' " +
                " and  NOT EXISTS (    " +
                " select * from notification " +
                " where notification_type_id=${EnumNotificationType.MY_NOTIFICATION.toString()} " +
                " and object_source_id=rc.id " +
                " and object_source_reference='${RecruitmentCycle.getName()}' )" +
                " group by jrcd.recruitment_cycle_id,rc.id,rc.name, rcp.to_date_datetime,rcp.from_date_datetime;"

        //create sqlQuery
        Query sqlQuery = session.createSQLQuery("select  rc.id," +
                "rc.name," +
                "rcp.to_date_datetime," +
                "string_agg(jrcd.department_id, ';')  as departmentIds," +
                "string_agg(jrcg.job_category_id, ';')  as jobCategoryIds," +
                "rcp.from_date_datetime "
                + query)

        //fill map parameter
        Map sqlParamsMap = [firmId  : firm?.id,
                            fromDate: PCPUtils.convertZonedDateTimeToTimeStamp(fromDate),
                            toDate  : PCPUtils.convertZonedDateTimeToTimeStamp(toDate)]

        //assign value to each parameter
        sqlParamsMap?.each {
            sqlQuery.setParameter(it.key.toString(), it.value)
        }

        //execute query
        final queryResults = sqlQuery?.list()

        //fill result into list
        queryResults?.eachWithIndex { def entry, int i ->
            list.add(entry)
        }
        //return query list
        return list
    }

    /**
     * get list of recruitmentCycle that have not notification.
     * @params from date.
     * @params to date.
     * @return boolean.
     */
    public Boolean createNotification(Firm firm, ZonedDateTime fromDate, ZonedDateTime toDate) {

        //get list of recruitmentCycle
        List resultList = getListOfRecruitmentCycle(firm, fromDate, toDate).toList()

        List departmentList = null
        List jobCategoryList = null
        List<Map> notificationActionsList
        List<UserTerm> userTermKeyList = []
        List<String> userTermValueList = []
        List<EmploymentRecord> employmentRecordList = null
        resultList?.eachWithIndex { entry, index ->

            userTermKeyList = []
            userTermValueList = []


            userTermKeyList.add(UserTerm.FIRM)
            userTermValueList.add("${firm?.id}")

            //set notification terms
            departmentList = entry[3].split(";")
            jobCategoryList = entry[4].split(";")
            departmentList?.each { value ->
                userTermKeyList.add(UserTerm.DEPARTMENT)
                userTermValueList.add("${value}")
            }

            //get list of employment records by department and job categories
            List jobTitleIds = EmploymentRecord.executeQuery("From EmploymentRecord employmentRecord where " +
                    "employmentRecord.department.id in (:departmentIds) and " +
                    "employmentRecord.jobTitle.jobCategory.id in (:jobCategoriesIds)  ",
                    [departmentIds   : departmentList,
                     jobCategoriesIds: jobCategoryList])?.jobTitle?.id

            jobTitleIds?.each { value ->
                userTermKeyList.add(UserTerm.JOB_TITLE)
                userTermValueList.add("${value}")
            }


            notificationActionsList = [
                    [action            : "create",
                     controller        : "jobRequisition",
                     label             : "${messageSource.getMessage("default.create.label", [] as Object[], LocaleContextHolder.getLocale())}",
                     icon              : "icon-plus-3",

                     notificationParams: [
                             new NotificationParams(name: "recruitmentCycle.id", value: "${entry[0]}"),
                     ]
                    ]
            ]


            requestService.createRequestNotification("${entry[0]}",
                    RecruitmentCycle.getName(),
                    PCPUtils.convertTimeStampToZonedDateTime("${entry[5]}"),
                    null,
                    userTermKeyList,
                    userTermValueList,
                    notificationActionsList,
                    EnumNotificationType.MY_NOTIFICATION,
                    'recruitmentCycle.notification.status.open.message',
                    ["${entry[1]}", "${PCPUtils.convertTimeStampToZonedDateTime(entry[2])?.toLocalDate()}"],
                    [:])
        }
        return true
    }
}